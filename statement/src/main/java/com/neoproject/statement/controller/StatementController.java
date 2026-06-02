package com.neoproject.statement.controller;

import com.neoproject.statement.model.dto.LoanOfferDto;
import com.neoproject.statement.model.dto.LoanStatementRequestDto;
import com.neoproject.statement.service.StatementService;
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
@RequestMapping("/statement")
@RequiredArgsConstructor
@Validated
@Tag(name = "Контроллер сервиса заявок", description = "Контроллер для обработки запросов для модуля заявок (statement)")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Ошибка в передаваемых параметрах")})
public class StatementController {

    private final StatementService statementService;

    @PostMapping()
    @Operation(summary = "Расчёт возможных условий кредита")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Предложения успешно сформированы и представлены")})

    public List<LoanOfferDto> getOffers(@Valid @RequestBody LoanStatementRequestDto dto){
        log.info("Пришли данные для расчета условий кредита: {}", dto);
        List<LoanOfferDto> offers = statementService.getOffers(dto);
        log.info("Рассчитаны различные условия кредита: {}", offers);
        return offers;
    }

    @PostMapping("/offer")
    @Operation(summary = "Выбор одного из предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Предложение успешно выбрано")})
    public void selectOffer(@Valid @RequestBody LoanOfferDto dto){
        log.info("Пришло предложение для подтверждения: {}", dto);
        statementService.selectOffer(dto);
        log.info("Предложение принято: {}", dto);
    }
}
