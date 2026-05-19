package org.neoproject.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.neoproject.gateway.exception.GatewayExternalServiceException;
import org.neoproject.gateway.model.dto.FinishRegistrationRequestDto;
import org.neoproject.gateway.model.dto.StatementDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

/**
 * Сервис для вызова методов микросервиса сделки (deal)
 */
@Slf4j
@Service
public class DealClientService {

    private final RestClient restClient;

    public DealClientService(@Qualifier("dealRestClient") RestClient dealRestClient){
        this.restClient = dealRestClient;
    }

    /**
     * Метод для запроса к сервису deal для завершения регистрации заявки
     *
     * @param statementId id сделки
     * @param dto специальный объект с данными для завершения регистрации заявки
     */
    public void finishRegistration(String statementId, FinishRegistrationRequestDto dto){
        try {
            restClient.post()
                    .uri("/calculate/{statementId}", statementId)
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientResponseException e) {
            log.error("Ошибка от сервиса deal в методе finishRegistration: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис deal",
                    HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (RestClientException e) {
            log.error("Ошибка соединения с сервисом deal: ", e);
            throw new GatewayExternalServiceException("Сервис deal недоступен", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка в методе finishRegistration: ", e);
            throw new GatewayExternalServiceException("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.debug("Данные о предложении успешно отправлены на сервис deal");
    }

    /**
     * Метод для запроса к сервису deal для отмены заявки по id
     *
     * @param statementId id сделки
     */
    public void statementDenied(String statementId){
        try {
            restClient.post()
                    .uri("/document/reject/" + statementId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientResponseException e) {
            log.error("Ошибка от сервиса deal в методе statementDenied: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис deal",
                    HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (RestClientException e) {
            log.error("Ошибка соединения с сервисом deal: ", e);
            throw new GatewayExternalServiceException("Сервис deal недоступен", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка в методе statementDenied: ", e);
            throw new GatewayExternalServiceException("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.debug("Заявка успешно отклонена");
    }

    /**
     * Метод для запроса к сервису deal для получения заявки по id
     *
     * @param statementId id сделки
     */
    public StatementDto findById(String statementId){
        StatementDto statementDto;
        try {
            statementDto = restClient.get()
                    .uri("/admin/statement/{statementId}", statementId)
                    .retrieve()
                    .body(StatementDto.class);
        } catch (RestClientResponseException e) {
            log.error("Ошибка от сервиса deal в методе findById: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис deal",
                    HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (RestClientException e) {
            log.error("Ошибка соединения с сервисом deal: ", e);
            throw new GatewayExternalServiceException("Сервис deal недоступен", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка в методе findById: ", e);
            throw new GatewayExternalServiceException("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.debug("Заявка успешно получена");

        return statementDto;
    }

    /**
     * Метод для запроса к сервису deal для получения всех заявок
     */
    public List<StatementDto> findAll(){
        List<StatementDto> statementDtoList;
        try {
            statementDtoList = restClient.get()
                    .uri("/admin/statement")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientResponseException e) {
            log.error("Ошибка от сервиса deal в методе findAll: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис deal",
                    HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (RestClientException e) {
            log.error("Ошибка соединения с сервисом deal: ", e);
            throw new GatewayExternalServiceException("Сервис deal недоступен", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка в методе findAll: ", e);
            throw new GatewayExternalServiceException("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.debug("Заявки успешно получены");

        return statementDtoList;
    }

    /**
     * Метод для запроса к сервису deal для обновления данных заявки и передачи данных микросервису dossier
     * для дальнейшей отправки на почту пользователя
     *
     * @param statementId id сделки
     */
    public void sendDocuments(String statementId) {
        try {
            restClient.get()
                    .uri("/document/{statementId}/send", statementId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientResponseException e) {
            log.error("Ошибка от сервиса deal в методе sendDocuments: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис deal",
                    HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (RestClientException e) {
            log.error("Ошибка соединения с сервисом deal: ", e);
            throw new GatewayExternalServiceException("Сервис deal недоступен", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка в методе sendDocuments: ", e);
            throw new GatewayExternalServiceException("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.debug("Запрос на отправку документов в сервис deal успешно прошел");
    }

    /**
     * Метод для запроса к сервису deal для обновления данных заявки и передачи данных микросервису dossier
     * для запроса подписания документов
     *
     * @param statementId id сделки
     */
    public void requestForSignDocuments(String statementId) {
        try {
            restClient.get()
                    .uri("/document/{statementId}/sign", statementId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientResponseException e) {
            log.error("Ошибка от сервиса deal в методе requestForSignDocuments: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис deal",
                    HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (RestClientException e) {
            log.error("Ошибка соединения с сервисом deal: ", e);
            throw new GatewayExternalServiceException("Сервис deal недоступен", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка в методе requestForSignDocuments: ", e);
            throw new GatewayExternalServiceException("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.debug("Запрос на подписание в сервис deal успешно прошел");
    }

    /**
     * Метод для запроса к сервису deal для обновления данных заявки и передачи данных микросервису dossier
     * для дальнейшего подписания документов и выдачи кредита
     *
     * @param statementId id сделки
     * @param code        ses код для подтверждения пользователя
     */
    public void signDocuments(String statementId, String code) {
        try {
            restClient.get()
                    .uri("/document/{id}/code?code={code}", statementId, code)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientResponseException e) {
            log.error("Ошибка от сервиса deal в методе signDocuments: ", e);
            throw new GatewayExternalServiceException("Ошибка при запросе в сервис deal",
                    HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (RestClientException e) {
            log.error("Ошибка соединения с сервисом deal: ", e);
            throw new GatewayExternalServiceException("Сервис deal недоступен", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка в методе signDocuments: ", e);
            throw new GatewayExternalServiceException("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.debug("Запрос на подтверждение подписания в сервис deal успешно прошел");
    }
}
