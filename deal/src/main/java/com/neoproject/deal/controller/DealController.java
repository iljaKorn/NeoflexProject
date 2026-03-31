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
        return dealService.getOffers(dto);
    }

    @PostMapping("/offer/select")
    @Operation(summary = "Выбор одного из предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Предложение успешно выбрано")})
    public void selectOffer(@RequestBody LoanOfferDto dto){
        dealService.selectOffer(dto);
    }

    @PostMapping("/calculate/{statementId}")
    public void finishRegistration(@PathVariable String statementId,
                                   @RequestBody FinishRegistrationRequestDto dto){
        dealService.finishRegistration(statementId, dto);
    }
}
