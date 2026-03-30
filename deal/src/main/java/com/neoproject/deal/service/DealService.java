package com.neoproject.deal.service;

import com.neoproject.deal.model.dto.LoanOfferDto;
import com.neoproject.deal.model.dto.LoanStatementRequestDto;
import com.neoproject.deal.model.entity.Client;
import com.neoproject.deal.model.entity.Passport;
import com.neoproject.deal.model.entity.Statement;
import com.neoproject.deal.repository.ClientRepository;
import com.neoproject.deal.repository.StatementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DealService {

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final RestClient restClient;

    @Transactional
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto) {

        Client newClient = new Client();
        newClient.setFirstName(dto.getFirstName());
        newClient.setLastName(dto.getLastName());
        newClient.setMiddleName(dto.getMiddleName());
        newClient.setBirthdate(dto.getBirthdate());
        newClient.setEmail(dto.getEmail());
        Passport passport = new Passport();
        passport.setNumber(dto.getPassportNumber());
        passport.setSeries(dto.getPassportSeries());
        newClient.setPassport(passport);

        Client saveClient = clientRepository.save(newClient);

        Statement newStatement = new Statement();
        newStatement.setClient(saveClient);
        newStatement.setCreationDate(LocalDateTime.now());
        Statement saveStatement = statementRepository.save(newStatement);

        ResponseEntity<List<LoanOfferDto>> response = restClient.post()
                .uri("/offers")
                .body(dto)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        List<LoanOfferDto> offers = response.getBody();

        for(LoanOfferDto offer : offers){
            offer.setStatementId(saveStatement.getStatementId());
        }

        return offers;
    }
}
