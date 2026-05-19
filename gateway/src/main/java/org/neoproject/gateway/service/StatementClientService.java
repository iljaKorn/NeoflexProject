package org.neoproject.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.neoproject.gateway.exception.GatewayExternalServiceException;
import org.neoproject.gateway.model.dto.LoanOfferDto;
import org.neoproject.gateway.model.dto.LoanStatementRequestDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.awt.*;
import java.util.List;

/**
 * Сервис для вызова методов микросервиса заявки (statement)
 */
@Slf4j
@Service
public class StatementClientService {

    private final RestClient restClient;

    public StatementClientService(@Qualifier("statementRestClient") RestClient statementRestClient){
        this.restClient = statementRestClient;
    }

    /**
     * Метод для получения предложений по кредиту
     *
     * @param dto специальный объект с данными для получения предложений
     */
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto){
        List<LoanOfferDto> offers;

        try {
            offers = restClient.post()
                    .uri("")
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientResponseException e) {
            log.error("Ошибка от сервиса deal в методе getOffers: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис statement",
                    HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (RestClientException e) {
            log.error("Ошибка соединения с сервисом statement: ", e);
            throw new GatewayExternalServiceException("Сервис statement недоступен", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка в методе getOffers: ", e);
            throw new GatewayExternalServiceException("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (offers == null) {
            throw new GatewayExternalServiceException("Данные о предложениях по кредиту равны null", HttpStatus.NOT_FOUND);
        }
        log.debug("Получены данные о предложениях {} от сервиса statement", offers);

        return offers;
    }

    /**
     * Метод для выбора конкретного предложения
     *
     * @param dto специальный объект с данными по конкретному предложению
     */
    public void selectOffer(LoanOfferDto dto){
        try {
            restClient.post()
                    .uri("/offer")
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientResponseException e) {
            log.error("Ошибка от сервиса deal в методе selectOffer: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис statement",
                    HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (RestClientException e) {
            log.error("Ошибка соединения с сервисом statement: ", e);
            throw new GatewayExternalServiceException("Сервис statement недоступен", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка в методе selectOffer: ", e);
            throw new GatewayExternalServiceException("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.debug("Данные о предложении успешно отправлены на сервис statement");
    }
}
