package com.neoproject.deal.service;

import com.neoproject.deal.converter.ClientMapper;
import com.neoproject.deal.converter.CreditMapper;
import com.neoproject.deal.converter.ScoringDataMapper;
import com.neoproject.deal.exception.DealServiceException;
import com.neoproject.deal.model.dto.*;
import com.neoproject.deal.model.entity.Client;
import com.neoproject.deal.model.entity.Credit;
import com.neoproject.deal.model.entity.Statement;
import com.neoproject.deal.model.enums.ApplicationStatus;
import com.neoproject.deal.model.enums.ChangeType;
import com.neoproject.deal.model.enums.CreditStatus;
import com.neoproject.deal.repository.ClientRepository;
import com.neoproject.deal.repository.CreditRepository;
import com.neoproject.deal.repository.StatementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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

    private final RestClient restClient;

    /**
     * Метод для получения всех возможных условий кредита и сохранения этих данных в базу
     * @param dto специальный объект со всеми входными данными для составления различных условий кредита
     */
    @Transactional
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto) {
        Client newClient = clientMapper.toEntity(dto);

        Client saveClient = clientRepository.save(newClient);
        log.debug("В базу сохранены данные о клиенте: {}", saveClient);

        Statement newStatement = new Statement();
        newStatement.setClient(saveClient);
        newStatement.setCreationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        Statement saveStatement = statementRepository.save(newStatement);
        log.debug("В базу сохранены данные о сделке: {}", saveStatement);

        List<LoanOfferDto> offers;
        try {
             offers = restClient.post()
                    .uri("/offers")
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            throw new DealServiceException("Ошибка при запросе в другой сервис");
        }
        if (offers == null){
            throw new DealServiceException("Полученные данные равны null");
        }
        log.debug("Получены данные о предложениях {} от сервиса калькулятора", offers);

        for (LoanOfferDto offer : offers) {
            offer.setStatementId(saveStatement.getStatementId());
        }

        return offers;
    }

    /**
     * Метод для подтверждения выбора одного из предложений по кредиту
     * @param dto специальный объект со всеми входными данными по одному из предложений
     */
    @Transactional
    public void selectOffer(LoanOfferDto dto) {
        Statement statement = statementRepository.findById(dto.getStatementId())
                .orElseThrow(() -> new DealServiceException("Заявка не найдена"));
        updateStatus(statement, ApplicationStatus.PREAPPROVAL);
        statement.setAppliedOffer(dto);
        statementRepository.save(statement);
        log.debug("В базе обновлены данные о сделке: {}", statement);
    }

    /**
     * Метод для завершения регистрации клиента и расчёта всех параметров кредита
     * @param statementId id сделки
     * @param dto специальный объект с данными для завершения оформления кредита
     */
    @Transactional
    public void finishRegistration(String statementId, FinishRegistrationRequestDto dto) {
        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new DealServiceException("Заявка не найдена"));

        statement.getClient().setGender(dto.getGender());
        statement.getClient().setMaritalStatus(dto.getMaritalStatus());
        statement.getClient().setDependentAmount(dto.getDependentAmount());
        statement.getClient().getPassport().setIssueDate(dto.getPassportIssueDate());
        statement.getClient().getPassport().setIssueBranch(dto.getPassportIssueBranch());
        statement.getClient().setEmployment(dto.getEmployment());
        statement.getClient().setAccountNumber(dto.getAccountNumber());

        ScoringDataDto scoringDataDto = scoringDataMapper.toDto(statement);

        CreditDto creditDto;
        try {
             creditDto = restClient.post()
                    .uri("/calc")
                    .body(scoringDataDto)
                    .retrieve()
                    .body(CreditDto.class);
        } catch (Exception e) {
            throw new DealServiceException("Ошибка при запросе в другой сервис");
        }
        if (creditDto == null) {
            throw new DealServiceException("Полученные данные равны null");
        }
        log.debug("Получены данные о кредите {} от сервиса калькулятора", creditDto);

        Credit credit = creditMapper.toEntity(creditDto);
        credit.setCreditStatus(CreditStatus.CALCULATED);
        creditRepository.save(credit);
        log.debug("В базу добавлены данные о кредите: {}", credit);

        statement.setCredit(credit);
        updateStatus(statement, ApplicationStatus.APPROVED);
        statementRepository.save(statement);
        log.debug("В базу добавлены финальные данные о сделке: {}", statement);
    }

    /**
     * Вспомогательный метод для обновления статуса заявки
     * @param statement объект с данными о заявке по кредиту
     * @param status новый статус, присеваемый заявке
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
}
