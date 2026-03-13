package com.example.calculator.controller;

import com.example.calculator.model.DTO.CreditDto;
import com.example.calculator.model.DTO.LoanOfferDto;
import com.example.calculator.model.DTO.LoanStatementRequestDto;
import com.example.calculator.model.DTO.ScoringDataDto;
import com.example.calculator.service.CalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Контроллер калькулятора", description = "Контроллер для обработки запросов для модуля калькулятор")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Ошибка в передаваемых параметрах")})
public class CalculatorController {

    private final CalculatorService calculatorService;

    @PostMapping("/offers")
    @Operation(summary = "Расчёт возможных условий кредита")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Предложения успешно сформированы и представлены")})
    public List<LoanOfferDto> getOffers(@Valid @RequestBody LoanStatementRequestDto dto) {
        return calculatorService.getOffers(dto);
    }

    @PostMapping("/calc")
    @Operation(summary = "Полный расчет параметров кредита")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Условия кредита успешно посчитаны")})
    public CreditDto calculateCredit(@Valid @RequestBody ScoringDataDto dto) {
        return calculatorService.calculateCredit(dto);
    }
}
