package org.neoproject.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.neoproject.gateway.exception.GatewayExternalServiceException;
import org.neoproject.gateway.model.dto.LoanOfferDto;
import org.neoproject.gateway.model.dto.LoanStatementRequestDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
public class StatementClientService {

    private final RestClient restClient;

    public StatementClientService(@Qualifier("statementRestClient") RestClient statementRestClient){
        this.restClient = statementRestClient;
    }

    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto){
        List<LoanOfferDto> offers;

        try {
            offers = restClient.post()
                    .uri("")
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error("Произошла ошибка в методе getOffers: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис заявки");
        }
        if (offers == null) {
            throw new GatewayExternalServiceException("Данные о предложениях по кредиту равны null");
        }
        log.debug("Получены данные о предложениях {} от сервиса заявки", offers);

        return offers;
    }

    public void selectOffer(LoanOfferDto dto){
        try {
            restClient.post()
                    .uri("/offer")
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error("Произошла ошибка в методе selectOffer: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис заявки");
        }
        log.debug("Данные о предложении успешно отправлены на сервис заявки");
    }
}
