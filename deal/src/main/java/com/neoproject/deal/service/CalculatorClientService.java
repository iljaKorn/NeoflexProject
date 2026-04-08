package com.neoproject.deal.service;

import com.neoproject.deal.exception.DealExternalServiceException;
import com.neoproject.deal.model.dto.CreditDto;
import com.neoproject.deal.model.dto.LoanOfferDto;
import com.neoproject.deal.model.dto.LoanStatementRequestDto;
import com.neoproject.deal.model.dto.ScoringDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Сервис для отправки запросов на модуль калькулятора
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalculatorClientService {

    private final RestClient restClient;

    /**
     * Метод для получения всех возможных условий кредита от сервиса калькулятора
     *
     * @param dto специальный объект со всеми входными данными для составления различных условий кредита
     */
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto) {
        List<LoanOfferDto> offers;

        try {
            offers = restClient.post()
                    .uri("/offers")
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error("Произошла ошибка в методе getOffers: {}", e.getMessage());
            throw new DealExternalServiceException("Ошибка при запросе в сервис калькулятора");
        }
        if (offers == null) {
            throw new DealExternalServiceException("Данные о предложениях по кредиту равны null");
        }
        log.debug("Получены данные о предложениях {} от сервиса калькулятора", offers);

        return offers;
    }

    /**
     * Метод для получения всех рассчитанных параметров кредита от модуля калькулятора
     *
     * @param dto специальный объект со всеми входными данными по одному из предложений
     */
    public CreditDto calculateCredit(ScoringDataDto dto) {
        CreditDto creditDto;
        try {
            creditDto = restClient.post()
                    .uri("/calc")
                    .body(dto)
                    .retrieve()
                    .body(CreditDto.class);
        } catch (Exception e) {
            log.error("Произошла ошибка в методе calculateCredit: {}", e.getMessage());
            throw new DealExternalServiceException("Ошибка при запросе в сервис калькулятора");
        }
        if (creditDto == null) {
            throw new DealExternalServiceException("Данные о кредите равны null");
        }
        log.debug("Получены данные о кредите {} от сервиса калькулятора", creditDto);

        return creditDto;
    }
}
