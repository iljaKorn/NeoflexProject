package com.neoproject.deal.controller;

import com.neoproject.deal.model.dto.FinishRegistrationRequestDto;
import com.neoproject.deal.model.dto.LoanOfferDto;
import com.neoproject.deal.model.dto.LoanStatementRequestDto;
import com.neoproject.deal.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
@Validated
@Tag(name = "Контроллер сервиса сделок", description = "Контроллер для обработки запросов для модуля сделок")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Ошибка в передаваемых параметрах")})
public class DealController {

    private final DealService dealService;

    @PostMapping("/statement")
    @Operation(summary = "Расчёт возможных условий кредита")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Предложения успешно сформированы и представлены")})
    public List<LoanOfferDto> getOffers(@Valid @RequestBody LoanStatementRequestDto dto){
        log.info("Пришли данные для расчета условий кредита: {}", dto);
        List<LoanOfferDto> offers = dealService.getOffers(dto);
        log.info("Рассчитаны различные условия кредита: {}", offers);
        return offers;
    }

    @PostMapping("/offer/select")
    @Operation(summary = "Выбор одного из предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Предложение успешно выбрано")})
    public void selectOffer(@RequestBody LoanOfferDto dto){
        log.info("Пришло предложение для подтверждения: {}", dto);
        dealService.selectOffer(dto);
        log.info("Предложение принято: {}", dto);
    }

    @PostMapping("/calculate/{statementId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Регистрация завершена")})
    public void finishRegistration(@PathVariable String statementId,
                                   @RequestBody FinishRegistrationRequestDto dto){
        log.info("Пришли данные для завершения регистрации statementId: {}, dto: {}", statementId, dto);
        dealService.finishRegistration(statementId, dto);
        log.info("Регистрация пользователя завершена с statementId: {}", statementId);
    }
}
