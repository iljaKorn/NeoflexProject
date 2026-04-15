package com.neoproject.statement.service;

import com.neoproject.statement.model.dto.LoanOfferDto;
import com.neoproject.statement.model.dto.LoanStatementRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис заявок
 */
@Service
@RequiredArgsConstructor
public class StatementService {

    private final DealClientService dealClientService;

    /**
     * Метод для получения всех возможных условий кредита и сохранения этих данных в базу
     *
     * @param dto специальный объект со всеми входными данными для составления различных условий кредита
     */
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto){
        return dealClientService.getOffers(dto);
    }

    /**
     * Метод для подтверждения выбора одного из предложений по кредиту
     *
     * @param dto специальный объект со всеми входными данными по одному из предложений
     */
    public void selectOffer(LoanOfferDto dto){
        dealClientService.selectOffer(dto);
    }
}
