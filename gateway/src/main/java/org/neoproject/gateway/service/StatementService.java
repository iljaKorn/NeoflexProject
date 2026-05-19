package org.neoproject.gateway.service;

import lombok.RequiredArgsConstructor;
import org.neoproject.gateway.model.dto.LoanOfferDto;
import org.neoproject.gateway.model.dto.LoanStatementRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для вызова методов микросервиса statement
 */
@Service
@RequiredArgsConstructor
public class StatementService {

    private final StatementClientService statementClientService;

    /**
     * Метод для получения предложений по кредиту
     *
     * @param dto специальный объект с данными для получения предложений
     */
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto) {
        return statementClientService.getOffers(dto);
    }

    /**
     * Метод для выбора конкретного предложения
     *
     * @param dto специальный объект с данными по конкретному предложению
     */
    public void selectOffer(LoanOfferDto dto) {
        statementClientService.selectOffer(dto);
    }
}
