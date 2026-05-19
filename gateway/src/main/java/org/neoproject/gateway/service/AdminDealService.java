package org.neoproject.gateway.service;

import lombok.RequiredArgsConstructor;
import org.neoproject.gateway.model.dto.StatementDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для вызова админских методов микросервиса deal
 */
@Service
@RequiredArgsConstructor
public class AdminDealService {

    private final DealClientService dealClientService;

    /**
     * Метод для получения заявки по id
     *
     * @param statementId id сделки
     */
    public StatementDto getById(String statementId) {
        return dealClientService.findById(statementId);
    }

    /**
     * Метод для получения всех заявок
     */
    public List<StatementDto> getAllStatements() {
        return dealClientService.findAll();
    }
}
