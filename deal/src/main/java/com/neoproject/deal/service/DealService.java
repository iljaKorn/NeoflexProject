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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Transactional
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto) {
        Client newClient = clientMapper.toEntity(dto);

        Client saveClient = clientRepository.save(newClient);

        Statement newStatement = new Statement();
        newStatement.setClient(saveClient);
        newStatement.setCreationDate(LocalDateTime.now());
        List<StatementStatusHistoryDto> statementStatusHistory = new ArrayList<>();
        newStatement.setStatusHistory(statementStatusHistory);
        Statement saveStatement = statementRepository.save(newStatement);

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

        for (LoanOfferDto offer : offers) {
            offer.setStatementId(saveStatement.getStatementId());
        }

        return offers;
    }

    @Transactional
    public void selectOffer(LoanOfferDto dto) {
        Statement statement = statementRepository.findById(dto.getStatementId())
                .orElseThrow(() -> new DealServiceException("Заявка не найдена"));
        updateStatus(statement, ApplicationStatus.PREAPPROVAL);
        statement.setAppliedOffer(dto);
        statementRepository.save(statement);
    }

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

        Credit credit = creditMapper.toEntity(creditDto);
        credit.setCreditStatus(CreditStatus.CALCULATED);
        creditRepository.save(credit);

        statement.setCredit(credit);
        updateStatus(statement, ApplicationStatus.APPROVED);
        statementRepository.save(statement);
    }

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
