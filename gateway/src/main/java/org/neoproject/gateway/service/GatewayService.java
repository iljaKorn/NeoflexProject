package org.neoproject.gateway.service;

import lombok.RequiredArgsConstructor;
import org.neoproject.gateway.model.dto.FinishRegistrationRequestDto;
import org.neoproject.gateway.model.dto.LoanOfferDto;
import org.neoproject.gateway.model.dto.LoanStatementRequestDto;
import org.neoproject.gateway.model.dto.StatementDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для вызова методов из других микросервисов
 */
@Service
@RequiredArgsConstructor
public class GatewayService {

    private final DealClientService dealClientService;
    private final StatementClientService statementClientService;

    /**
     * Метод для получения предложений по кредиту
     *
     * @param dto специальный объект с данными для получения предложений
     */
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto){
        return statementClientService.getOffers(dto);
    }

    /**
     * Метод для выбора конкретного предложения
     *
     * @param dto специальный объект с данными по конкретному предложению
     */
    public void selectOffer(LoanOfferDto dto){
        statementClientService.selectOffer(dto);
    }

    /**
     * Метод для завершения регистрации заявки
     *
     * @param statementId id сделки
     * @param dto специальный объект с данными для завершения регистрации заявки
     */
    public void finishRegistration(String statementId, FinishRegistrationRequestDto dto){
        dealClientService.finishRegistration(statementId, dto);
    }

    /**
     * Метод для отмены заявки по id
     *
     * @param statementId id сделки
     */
    public void statementDenied(String statementId){
        dealClientService.statementDenied(statementId);
    }

    /**
     * Метод для получения заявки по id
     *
     * @param statementId id сделки
     */
    public StatementDto getById(String statementId){
        return dealClientService.findById(statementId);
    }

    /**
     * Метод для получения всех заявок
     */
    public List<StatementDto> getAllStatements(){
        return dealClientService.findAll();
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
