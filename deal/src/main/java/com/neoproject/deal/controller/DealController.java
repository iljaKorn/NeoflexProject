package com.neoproject.deal.controller;

import com.neoproject.deal.model.dto.*;
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
    public List<LoanOfferDto> getOffers(@Valid @RequestBody LoanStatementRequestDto dto) {
        log.info("Пришли данные для расчета условий кредита: {}", dto);
        List<LoanOfferDto> offers = dealService.getOffers(dto);
        log.info("Рассчитаны различные условия кредита: {}", offers);
        return offers;
    }

    @PostMapping("/offer/select")
    @Operation(summary = "Выбор одного из предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Предложение успешно выбрано")})
    public void selectOffer(@Valid @RequestBody LoanOfferDto dto) {
        log.info("Пришло предложение для подтверждения: {}", dto);
        dealService.selectOffer(dto);
        log.info("Предложение принято: {}", dto);
    }

    @PostMapping("/calculate/{statementId}")
    @Operation(summary = "Передача оставшихся данных и завершение регистрации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Регистрация завершена")})
    public void finishRegistration(@PathVariable String statementId,
                                   @Valid @RequestBody FinishRegistrationRequestDto dto) {
        log.info("Пришли данные для завершения регистрации statementId: {}, dto: {}", statementId, dto);
        dealService.finishRegistration(statementId, dto);
        log.info("Регистрация пользователя завершена с statementId: {}", statementId);
    }

    @PostMapping("/document/reject/{statementId}")
    @Operation(summary = "Отмена оформления заявки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отмена заявки")})
    public void statementDenied(@PathVariable String statementId) {
        log.info("Пришли данные для отмены заявки со statementId: {}", statementId);
        dealService.rejectStatement(statementId);
        log.info("Заявка со statementId: {} отменена", statementId);
    }

    @GetMapping("/document/{statementId}/send")
    @Operation(summary = "Отправка документов пользователю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Документы успешно отправлены")})
    public void sendDocument(@PathVariable String statementId) {
        log.info("Пришли данные для отправки документов с id: {}", statementId);
        dealService.sendDocuments(statementId);
        log.info("Документы отправлены с id: {}", statementId);
    }

    @GetMapping("/document/{statementId}/sign")
    @Operation(summary = "Запрос на подписание документов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос на подписание документов выполнен успешно")})
    public void requestForSignDocument(@PathVariable String statementId) {
        log.info("Пришли данные для запроса подписания документов с id: {}", statementId);
        dealService.requestForSignDocuments(statementId);
        log.info("Код для подписания отправлен для заявки с id: {}", statementId);
    }

    @GetMapping("/document/{statementId}/code")
    @Operation(summary = "Подпись документов и выдача кредита")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Документы успешно подписаны")})
    public void signDocument(@PathVariable String statementId, @RequestParam String code) {
        log.info("Пришли данные для подписания документов с id: {}", statementId);
        dealService.signDocuments(statementId, code);
        log.info("Документы подписаны для заявки с id: {}", statementId);
    }

    @GetMapping("/document/data/{statementId}")
    @Operation(summary = "Получение данных для формирования документа")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные успешно получены")})
    public DocumentDto getDocumentData(@PathVariable String statementId){
        log.info("Пришел statementId: {}", statementId);
        DocumentDto documentDto = dealService.getDocumentData(statementId);
        log.info("Данные для документов с statementId: {} получены", statementId);
        return documentDto;
    }

    @GetMapping("/admin/statement/{statementId}")
    @Operation(summary = "Получение заявки по statementId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные успешно получены")})
    public StatementDto getById(@PathVariable String statementId){
        log.info("Пришел statementId: {}", statementId);
        StatementDto dto = dealService.getById(statementId);
        log.info("Получена заявка с statementId: {}", statementId);
        return dto;
    }

    @GetMapping("/admin/statement")
    public List<StatementDto> getAllStatements(){
        log.info("Пришел запрос на получение всех заявок");
        List<StatementDto> statementList = dealService.getAllStatements();
        log.info("Заявки получены");
        return statementList;
    }
}
