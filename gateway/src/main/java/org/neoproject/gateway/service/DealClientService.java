package org.neoproject.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.neoproject.gateway.exception.GatewayExternalServiceException;
import org.neoproject.gateway.model.dto.FinishRegistrationRequestDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class DealClientService {

    private final RestClient restClient;

    public DealClientService(@Qualifier("dealRestClient") RestClient dealRestClient){
        this.restClient = dealRestClient;
    }

    public void finishRegistration(String statementId, FinishRegistrationRequestDto dto){
        try {
            restClient.post()
                    .uri("/calculate/" + statementId)
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error("Произошла ошибка в методе finishRegistration: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис заявки");
        }
        log.debug("Данные о предложении успешно отправлены на сервис заявки");
    }

    public void statementDenied(String statementId){
        try {
            restClient.post()
                    .uri("/document/reject/" + statementId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error("Произошла ошибка в методе statementDenied: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис заявки");
        }
        log.debug("Заявка успешно отклонена");
    }
}
