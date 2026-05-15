package org.neoproject.gateway.service;

import lombok.RequiredArgsConstructor;
import org.neoproject.gateway.model.dto.FinishRegistrationRequestDto;
import org.neoproject.gateway.model.dto.LoanOfferDto;
import org.neoproject.gateway.model.dto.LoanStatementRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GatewayService {

    private final DealClientService dealClientService;
    private final StatementClientService statementClientService;

    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto){
        return statementClientService.getOffers(dto);
    }

    public void selectOffer(LoanOfferDto dto){
        statementClientService.selectOffer(dto);
    }

    public void finishRegistration(String statementId, FinishRegistrationRequestDto dto){
        dealClientService.finishRegistration(statementId, dto);
    }

    public void statementDenied(String statementId){
        dealClientService.statementDenied(statementId);
    }
}
