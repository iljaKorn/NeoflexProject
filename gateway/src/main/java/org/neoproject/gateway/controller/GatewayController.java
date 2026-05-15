package org.neoproject.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neoproject.gateway.model.dto.FinishRegistrationRequestDto;
import org.neoproject.gateway.model.dto.LoanOfferDto;
import org.neoproject.gateway.model.dto.LoanStatementRequestDto;
import org.neoproject.gateway.service.GatewayService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/gateway")
@Validated
@Tag(name = "Контроллер сервиса gateway", description = "Контроллер для обработки запросов от пользователя")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Ошибка в передаваемых параметрах")})
public class GatewayController {

    private final GatewayService gatewayService;

    @PostMapping("/statement")
    @Operation(summary = "Расчёт возможных условий кредита")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Предложения успешно сформированы и представлены")})
    public List<LoanOfferDto> getOffers(@Valid @RequestBody LoanStatementRequestDto dto) {
        log.info("Пришли данные для расчета условий кредита: {}", dto);
        List<LoanOfferDto> offers = gatewayService.getOffers(dto);
        log.info("Рассчитаны различные условия кредита: {}", offers);
        return offers;
    }

    @PostMapping("/statement/offer")
    @Operation(summary = "Выбор одного из предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Предложение успешно выбрано")})
    public void selectOffer(@Valid @RequestBody LoanOfferDto dto) {
        log.info("Пришло предложение для подтверждения: {}", dto);
        gatewayService.selectOffer(dto);
        log.info("Предложение принято: {}", dto);
    }

    @PostMapping("/calculate/{statementId}")
    @Operation(summary = "Передача оставшихся данных и завершение регистрации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Регистрация завершена")})
    public void finishRegistration(@Valid @RequestBody FinishRegistrationRequestDto dto,
                                   @PathVariable String statementId) {
        log.info("Пришли данные для завершения регистрации statementId: {}, dto: {}", statementId, dto);
        gatewayService.finishRegistration(statementId, dto);
        log.info("Регистрация пользователя завершена с statementId: {}", statementId);
    }

    @PostMapping("/reject/{statementId}")
    @Operation(summary = "Отмена оформления заявки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отмена заявки")})
    public void statementDenied(@PathVariable String statementId){
        log.info("Пришли данные для отмены заявки со statementId: {}", statementId);
        gatewayService.statementDenied(statementId);
        log.info("Заявка со statementId: {} отменена", statementId);
    }
}
