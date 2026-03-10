package com.example.calculator.controller;

import com.example.calculator.model.DTO.CreditDto;
import com.example.calculator.model.DTO.LoanOfferDto;
import com.example.calculator.model.DTO.LoanStatementRequestDto;
import com.example.calculator.model.DTO.ScoringDataDto;
import com.example.calculator.service.CalculatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
@Validated
public class CalculatorController {

    private final CalculatorService calculatorService;

    @PostMapping("/offers")
    public List<LoanOfferDto> getOffers(@Valid @RequestBody LoanStatementRequestDto dto) {
        return calculatorService.getOffers(dto);
    }

    @PostMapping("/calc")
    public CreditDto calculateCredit(@Valid @RequestBody ScoringDataDto dto) {
        return calculatorService.calculateCredit(dto);
    }
}
