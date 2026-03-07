package com.example.calculator.service;

import com.example.calculator.exception.PreScoringException;
import com.example.calculator.model.DTO.LoanOfferDto;
import com.example.calculator.model.DTO.LoanStatementRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final PreScoringService preScoringService;

    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto) {
        if (!preScoringService.preScoring(dto)){
            throw new PreScoringException("Данные не прошли прескоринг");
        }
        return null;
    }
}
