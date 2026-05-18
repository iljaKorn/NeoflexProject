package org.neoproject.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neoproject.gateway.model.dto.StatementDto;
import org.neoproject.gateway.service.AdminDealService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/deal")
@Validated
@Tag(name = "Контроллер сервиса gateway для админских запросов",
        description = "Контроллер для обработки запросов от админа")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Ошибка в передаваемых параметрах")})
public class AdminDealController {

    private final AdminDealService adminDealService;

    @GetMapping("/admin/statement/{statementId}")
    @Operation(summary = "Получение заявки по statementId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные успешно получены")})
    public StatementDto getById(@PathVariable String statementId) {
        log.info("Пришел statementId: {}", statementId);
        StatementDto dto = adminDealService.getById(statementId);
        log.info("Получена заявка с statementId: {}", statementId);
        return dto;
    }

    @GetMapping("/admin/statement")
    @Operation(summary = "Получение всех заявок")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные успешно получены")})
    public List<StatementDto> getAllStatements() {
        log.info("Пришел запрос на получение всех заявок");
        List<StatementDto> statementList = adminDealService.getAllStatements();
        log.info("Заявки получены");
        return statementList;
    }
}
