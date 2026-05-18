package org.neoproject.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neoproject.gateway.model.dto.FinishRegistrationRequestDto;
import org.neoproject.gateway.service.DealService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/deal")
@Validated
@Tag(name = "Контроллер сервиса gateway с запросами к сервису Deal",
        description = "Контроллер для обработки запросов от пользователя, идущих в сервис Deal")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Ошибка в передаваемых параметрах")})
public class DealController {

    private final DealService dealService;

    @PostMapping("/calculate/{statementId}")
    @Operation(summary = "Передача оставшихся данных и завершение регистрации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Регистрация завершена")})
    public void finishRegistration(@Valid @RequestBody FinishRegistrationRequestDto dto,
                                   @PathVariable String statementId) {
        log.info("Пришли данные для завершения регистрации statementId: {}, dto: {}", statementId, dto);
        dealService.finishRegistration(statementId, dto);
        log.info("Регистрация пользователя завершена с statementId: {}", statementId);
    }

    @PostMapping("/reject/{statementId}")
    @Operation(summary = "Отмена оформления заявки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отмена заявки")})
    public void statementDenied(@PathVariable String statementId) {
        log.info("Пришли данные для отмены заявки со statementId: {}", statementId);
        dealService.statementDenied(statementId);
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
}
