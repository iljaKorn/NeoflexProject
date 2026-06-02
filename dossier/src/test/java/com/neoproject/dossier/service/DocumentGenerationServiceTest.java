package com.neoproject.dossier.service;

import com.neoproject.dossier.model.dto.DocumentDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentGenerationServiceTest {

    @Mock
    private DealClientService dealClient;

    @InjectMocks
    private DocumentGenerationService documentGenerationService;

    @Test
    void shouldCreateFileAndCallClient() throws IOException {
        // Подготовка
        UUID statementId = UUID.randomUUID();
        DocumentDto mockDto = new DocumentDto();
        mockDto.setLastName("Иванов");
        mockDto.setFirstName("Иван");
        mockDto.setPassportSeries("4500");
        mockDto.setPassportNumber("123456");
        mockDto.setAmount(BigDecimal.valueOf(100000.50));
        mockDto.setTerm(12);
        mockDto.setRate(BigDecimal.valueOf(15.5));
        mockDto.setMonthlyPayment(BigDecimal.valueOf(8792.10));

        when(dealClient.getDocumentData(statementId)).thenReturn(mockDto);

        // Действие
        File result = documentGenerationService.generateDocument(statementId);

        // Проверка
        assertThat(result)
                .isNotNull()
                .exists()
                .hasExtension("pdf");

        verify(dealClient).getDocumentData(statementId);

        result.deleteOnExit();
    }
}