package org.neoproject.gateway.service;

import lombok.RequiredArgsConstructor;
import org.neoproject.gateway.model.dto.FinishRegistrationRequestDto;
import org.springframework.stereotype.Service;

/**
 * Сервис для вызова методов микросервиса deal
 */
@Service
@RequiredArgsConstructor
public class DealService {

    private final DealClientService dealClientService;

    /**
     * Метод для завершения регистрации заявки
     *
     * @param statementId id сделки
     * @param dto         специальный объект с данными для завершения регистрации заявки
     */
    public void finishRegistration(String statementId, FinishRegistrationRequestDto dto) {
        dealClientService.finishRegistration(statementId, dto);
    }

    /**
     * Метод для отмены заявки по id
     *
     * @param statementId id сделки
     */
    public void statementDenied(String statementId) {
        dealClientService.statementDenied(statementId);
    }

    /**
     * Метод для обновления данных заявки и передачи данных микросервису dossier
     * для дальнейшей отправки на почту пользователя
     *
     * @param statementId id сделки
     */
    public void sendDocuments(String statementId) {
        dealClientService.sendDocuments(statementId);
    }

    /**
     * Метод для обновления данных заявки и передачи данных микросервису dossier
     * для запроса подписания документов
     *
     * @param statementId id сделки
     */
    public void requestForSignDocuments(String statementId) {
        dealClientService.requestForSignDocuments(statementId);
    }

    /**
     * Метод для обновления данных заявки и передачи данных микросервису dossier
     * для дальнейшего подписания документов и выдачи кредита
     *
     * @param statementId id сделки
     * @param code        ses код для подтверждения пользователя
     */
    public void signDocuments(String statementId, String code) {
        dealClientService.signDocuments(statementId, code);
    }
}
