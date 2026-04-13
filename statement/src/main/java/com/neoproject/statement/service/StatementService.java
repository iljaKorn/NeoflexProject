package com.neoproject.statement.service;

import com.neoproject.statement.model.dto.LoanOfferDto;
import com.neoproject.statement.model.dto.LoanStatementRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final DealClientService dealClientService;

    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto){
        return dealClientService.getOffers(dto);
    }

    public void selectOffer(LoanOfferDto dto){
        dealClientService.selectOffer(dto);
    }
}
