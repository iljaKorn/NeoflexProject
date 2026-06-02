package com.neoproject.statement.service;

import com.neoproject.statement.exception.StatementExternalServiceException;
import com.neoproject.statement.model.dto.LoanOfferDto;
import com.neoproject.statement.model.dto.LoanStatementRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Сервис для отправки запросов на модуль сделок
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DealClientService {

    private final RestClient restClient;

    /**
     * Метод для получения всех возможных условий кредита от модуля сделок
     *
     * @param dto специальный объект со всеми входными данными для составления различных условий кредита
     */
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto){
        List<LoanOfferDto> offers;

        try {
            offers = restClient.post()
                    .uri("/statement")
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error("Произошла ошибка в методе getOffers: ", e);
            throw new StatementExternalServiceException("Ошибка при запросе в сервис сделки");
        }
        if (offers == null) {
            throw new StatementExternalServiceException("Данные о предложениях по кредиту равны null");
        }
        log.debug("Получены данные о предложениях {} от сервиса сделки", offers);

        return offers;
    }

    /**
     * Метод для выбора одного из предложений по кредиту
     *
     * @param dto специальный объект со всеми входными данными по одному из предложений
     */
    public void selectOffer(LoanOfferDto dto){
        try {
            restClient.post()
                    .uri("/offer/select")
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error("Произошла ошибка в методе selectOffer: ", e);
            throw new StatementExternalServiceException("Ошибка при запросе в сервис сделки");
        }
        log.debug("Данные о предложении успешно отправлены на сервис сделки");
    }
}
