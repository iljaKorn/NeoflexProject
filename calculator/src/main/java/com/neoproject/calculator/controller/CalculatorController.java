package com.neoproject.calculator.controller;

import com.neoproject.calculator.model.dto.CreditDto;
import com.neoproject.calculator.model.dto.LoanOfferDto;
import com.neoproject.calculator.model.dto.LoanStatementRequestDto;
import com.neoproject.calculator.model.dto.ScoringDataDto;
import com.neoproject.calculator.service.CalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
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
        log.info("Пришли данные для расчета условий кредита: {}", dto);
        List<LoanOfferDto> offers = calculatorService.getOffers(dto);
        log.info("Рассчитаны различные условия кредита: {}", offers);
        return offers;
    }

    @PostMapping("/calc")
    @Operation(summary = "Полный расчет параметров кредита")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Условия кредита успешно посчитаны")})
    public CreditDto calculateCredit(@Valid @RequestBody ScoringDataDto dto) {
        log.info("Пришли данные для расчета параметров кредита: {}", dto);
        CreditDto creditDto = calculatorService.calculateCredit(dto);
        log.info("Рассчитаны параметры кредита: {}", creditDto);
        return creditDto;
    }
}
