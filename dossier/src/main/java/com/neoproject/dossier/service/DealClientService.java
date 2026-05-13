package com.neoproject.dossier.service;

import com.neoproject.dossier.exception.DossierExternalServiceException;
import com.neoproject.dossier.model.dto.DocumentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealClientService {

    private final RestClient restClient;

    public DocumentDto getDocumentData(UUID statementId) {
        DocumentDto documentDto;

        try {
            documentDto = restClient.get()
                    .uri("/document/data/" + statementId)
                    .retrieve()
                    .body(DocumentDto.class);
        } catch (Exception e) {
            log.error("Произошла ошибка в методе getDocumentData: ", e);
            throw new DossierExternalServiceException("Ошибка при запросе в сервис сделки");
        }
        if (documentDto == null) {
            throw new DossierExternalServiceException("Данные о заявке равны null");
        }
        log.debug("Получены данные о заявке {} от сервиса сделки", documentDto);

        return documentDto;
    }
}
