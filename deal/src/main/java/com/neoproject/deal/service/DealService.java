package com.neoproject.deal.service;

import com.neoproject.deal.converter.ClientMapper;
import com.neoproject.deal.converter.CreditMapper;
import com.neoproject.deal.converter.ScoringDataMapper;
import com.neoproject.deal.converter.StatementMapper;
import com.neoproject.deal.exception.DealDatabaseNotFoundException;
import com.neoproject.deal.exception.InvalidSesCodeException;
import com.neoproject.deal.model.dto.*;
import com.neoproject.deal.model.entity.Client;
import com.neoproject.deal.model.entity.Credit;
import com.neoproject.deal.model.entity.Statement;
import com.neoproject.deal.model.enums.ApplicationStatus;
import com.neoproject.deal.model.enums.ChangeType;
import com.neoproject.deal.model.enums.CreditStatus;
import com.neoproject.deal.producer.EmailProducer;
import com.neoproject.deal.repository.ClientRepository;
import com.neoproject.deal.repository.CreditRepository;
import com.neoproject.deal.repository.StatementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Сервис сделок
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DealService {

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;

    private final ScoringDataMapper scoringDataMapper;
    private final CreditMapper creditMapper;
    private final ClientMapper clientMapper;
    private final StatementMapper statementMapper;

    private final CalculatorClientService calculatorClientService;
    private final EmailProducer emailProducer;

    /**
     * Метод для получения всех возможных условий кредита и сохранения этих данных в базу
     *
     * @param dto специальный объект со всеми входными данными для составления различных условий кредита
     */
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto) {
        Client newClient = clientMapper.toEntity(dto);

        Client saveClient = clientRepository.save(newClient);
        log.debug("В базу сохранены данные о клиенте: {}", saveClient);

        Statement newStatement = new Statement();
        newStatement.setClient(saveClient);
        updateStatus(newStatement, ApplicationStatus.PREAPPROVAL);
        newStatement.setCreationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        Statement saveStatement = statementRepository.save(newStatement);
        log.debug("В базу сохранены данные о сделке: {}", saveStatement);

        List<LoanOfferDto> offers = calculatorClientService.getOffers(dto);

        for (LoanOfferDto offer : offers) {
            offer.setStatementId(saveStatement.getStatementId());
        }

        return offers;
    }

    /**
     * Метод для подтверждения выбора одного из предложений по кредиту
     *
     * @param dto специальный объект со всеми входными данными по одному из предложений
     */
    @Transactional
    public void selectOffer(LoanOfferDto dto) {
        Statement statement = statementRepository.findByIdWithLock(dto.getStatementId())
                .orElseThrow(() -> new DealDatabaseNotFoundException("Заявка не найдена"));

        if (statement.getStatus() == ApplicationStatus.APPROVED) {
            log.warn("Заявка с id: {} уже обработана", dto.getStatementId());
            return;
        }

        updateStatus(statement, ApplicationStatus.APPROVED);
        statement.setAppliedOffer(dto);
        statementRepository.save(statement);
        log.debug("В базе обновлены данные о сделке: {}", statement);

        emailProducer.produceMessageForFinishRegistration(statement.getClient().getEmail(), statement.getStatementId());
    }

    /**
     * Метод для завершения регистрации клиента и расчёта всех параметров кредита
     *
     * @param statementId id сделки
     * @param dto         специальный объект с данными для завершения оформления кредита
     */
    public void finishRegistration(String statementId, FinishRegistrationRequestDto dto) {
        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new DealDatabaseNotFoundException("Заявка не найдена"));

        statementMapper.updateStatementFromDto(statement, dto);
        ScoringDataDto scoringDataDto = scoringDataMapper.toDto(statement);
        CreditDto creditDto = calculatorClientService.calculateCredit(scoringDataDto);

        Credit credit = creditMapper.toEntity(creditDto);
        credit.setCreditStatus(CreditStatus.CALCULATED);
        creditRepository.save(credit);
        log.debug("В базу добавлены данные о кредите: {}", credit);

        statement.setCredit(credit);
        updateStatus(statement, ApplicationStatus.CC_APPROVED);
        statementRepository.save(statement);
        log.debug("В базу добавлены финальные данные о сделке: {}", statement);

        emailProducer.produceMessageForCreateDocument(statement.getClient().getEmail(), statement.getStatementId());
    }

    /**
     * Вспомогательный метод для обновления статуса заявки
     *
     * @param statement объект с данными о заявке по кредиту
     * @param status    новый статус, присеваемый заявке
     */
    private void updateStatus(Statement statement, ApplicationStatus status) {
        statement.setStatus(status);

        List<StatementStatusHistoryDto> statusHistory = statement.getStatusHistory();
        if (statusHistory == null) {
            statusHistory = new ArrayList<>();
        }

        StatementStatusHistoryDto newStatus = new StatementStatusHistoryDto();
        newStatus.setStatus(String.valueOf(status));
        newStatus.setTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        newStatus.setChangeType(ChangeType.AUTOMATIC);
        statusHistory.add(newStatus);
        statement.setStatusHistory(statusHistory);
    }

    /**
     * Метод для отмены заявки клиентом
     *
     * @param statementId id сделки
     */
    public void rejectStatement(String statementId) {
        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new DealDatabaseNotFoundException("Заявка не найдена"));

        updateStatus(statement, ApplicationStatus.CLIENT_DENIED);
        statementRepository.save(statement);
        log.debug("Обновлен статус заявки с id: {} на {}", statement.getStatementId(), statement.getStatus());

        emailProducer.produceMessageForRejectStatement(statement.getClient().getEmail(), statement.getStatementId());
    }

    /**
     * Метод для обновления данных заявки и передачи данных микросервису dossier
     * для дальнейшей отправки на почту пользователя
     *
     * @param statementId id сделки
     */
    public void sendDocuments(String statementId) {
        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new DealDatabaseNotFoundException("Заявка не найдена"));

        updateStatus(statement, ApplicationStatus.PREPARE_DOCUMENTS);
        statementRepository.save(statement);
        log.debug("Обновлен статус заявки с id: {} на {}", statement.getStatementId(), statement.getStatus());

        emailProducer.produceMessageForSendDocuments(statement.getClient().getEmail(), statement.getStatementId());

        updateStatus(statement, ApplicationStatus.DOCUMENT_CREATED);
        statementRepository.save(statement);
        log.debug("Обновлен статус заявки с id: {} на {}", statement.getStatementId(), statement.getStatus());
    }

    /**
     * Метод для обновления данных заявки и передачи данных микросервису dossier
     * для дальнейшего запроса подписи документов
     *
     * @param statementId id сделки
     */
    public void requestForSignDocuments(String statementId) {
        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new DealDatabaseNotFoundException("Заявка не найдена"));

        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }

        statement.setSesCode(code.toString());
        statementRepository.save(statement);
        log.debug("Добавлен ses код {}", statement);

        emailProducer.produceMessageForRequestToSign(statement.getClient().getEmail(),
                statement.getStatementId(), code.toString());
    }

    /**
     * Метод для обновления данных заявки и передачи данных микросервису dossier
     * для дальнейшего подписания документов и выдачи кредита
     *
     * @param statementId id сделки
     * @param code        ses код для подтверждения пользователя
     */
    public void signDocuments(String statementId, String code) {
        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new DealDatabaseNotFoundException("Заявка не найдена"));

        if (!code.equals(statement.getSesCode())) {
            throw new InvalidSesCodeException("Не совпадает ses код");
        }

        statement.setSignDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        updateStatus(statement, ApplicationStatus.DOCUMENT_SIGNED);
        statementRepository.save(statement);
        log.debug("Обновлен статус и дата подписания заявки с id: {}", statement.getStatementId());

        emailProducer.produceMessageForSignDocuments(statement.getClient().getEmail(), statement.getStatementId());

        statement.getCredit().setCreditStatus(CreditStatus.ISSUED);
        updateStatus(statement, ApplicationStatus.CREDIT_ISSUED);
        statementRepository.save(statement);
        log.debug("Обновлен статус заявки с id: {} на {}", statement.getStatementId(), statement.getStatus());
    }

    public DocumentDto getDocumentData(String statementId) {
        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new DealDatabaseNotFoundException("Заявка не найдена"));

        DocumentDto documentDto = statementMapper.toDocumentDto(statement);
        log.debug("Получены данные для формирования документов с statementId: {}", statementId);

        return documentDto;
    }

    public StatementDto getById(String statementId){
        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new DealDatabaseNotFoundException("Заявка не найдена"));

        return statementMapper.toDto(statement);
    }

    public List<StatementDto> getAllStatements(){
        List<Statement> statementList = statementRepository.findAll();
        return statementMapper.toDtoList(statementList);
    }
}
