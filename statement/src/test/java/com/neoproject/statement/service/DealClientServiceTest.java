package com.neoproject.statement.service;

import com.neoproject.statement.exception.StatementExternalServiceException;
import com.neoproject.statement.model.dto.LoanOfferDto;
import com.neoproject.statement.model.dto.LoanStatementRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Тесты для сервиса, который обращается к модулю сделки")
@ExtendWith(MockitoExtension.class)
class DealClientServiceTest {

    @InjectMocks
    private DealClientService dealClientService;

    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Test
    void shouldGetOffersCorrectly() {
        // Подготовка
        LoanStatementRequestDto dto = new LoanStatementRequestDto();
        List<LoanOfferDto> expectedOffers = new ArrayList<>();

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/statement")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(dto)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expectedOffers);

        // Действие
        dealClientService.getOffers(dto);

        // Проверка
        verify(restClient, times(1)).post();
    }

    @Test
    void shouldThrowStatementExternalServiceExceptionWithBadRequest() {
        // Подготовка
        LoanStatementRequestDto dto = new LoanStatementRequestDto();

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/statement")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(dto)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // Действие и Проверка
        assertThatThrownBy(() -> dealClientService.getOffers(dto))
                .isInstanceOf(StatementExternalServiceException.class)
                .hasMessageContaining("Ошибка при запросе в сервис сделки");
    }

    @Test
    void shouldThrowDealExternalServiceExceptionWithNullResponse() {
        // Подготовка
        LoanStatementRequestDto dto = new LoanStatementRequestDto();

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/statement")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(dto)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);

        // Действие и Проверка
        assertThatThrownBy(() -> dealClientService.getOffers(dto))
                .isInstanceOf(StatementExternalServiceException.class)
                .hasMessageContaining("Данные о предложениях по кредиту равны null");
    }

    @Test
    void shouldSelectOfferCorrectly() {
        // Подготовка
        LoanOfferDto dto = new LoanOfferDto();
        List<LoanOfferDto> expectedOffers = new ArrayList<>();

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/offer/select")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(dto)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expectedOffers);

        // Действие
        dealClientService.selectOffer(dto);

        // Проверка
        verify(restClient, times(1)).post();
    }

    @Test
    void shouldThrowStatementExternalServiceExceptionWithBadRequestForSelectOffer() {
        // Подготовка
        LoanOfferDto dto = new LoanOfferDto();

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/offer/select")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(dto)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // Действие и Проверка
        assertThatThrownBy(() -> dealClientService.selectOffer(dto))
                .isInstanceOf(StatementExternalServiceException.class)
                .hasMessageContaining("Ошибка при запросе в сервис сделки");
    }
}