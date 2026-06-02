package com.neoproject.deal.service;

import com.neoproject.deal.exception.DealExternalServiceException;
import com.neoproject.deal.model.dto.*;
import com.neoproject.deal.model.enums.EmploymentStatus;
import com.neoproject.deal.model.enums.Gender;
import com.neoproject.deal.model.enums.MaritalStatus;
import com.neoproject.deal.model.enums.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Тесты для сервиса, который обращается к модулю калькулятора")
@ExtendWith(MockitoExtension.class)
class CalculatorClientServiceTest {

    @InjectMocks
    private CalculatorClientService calculatorClientService;

    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    private LoanStatementRequestDto validRequestForGetOffers;
    private ScoringDataDto validRequestForCalculateCredit;

    @BeforeEach
    void setUp() {
        validRequestForGetOffers = new LoanStatementRequestDto();
        validRequestForGetOffers.setAmount(new BigDecimal("300000"));
        validRequestForGetOffers.setTerm(12);
        validRequestForGetOffers.setFirstName("Ivan");
        validRequestForGetOffers.setLastName("Petrov");
        validRequestForGetOffers.setMiddleName("Sergeevich");
        validRequestForGetOffers.setEmail("ivan@example.com");
        validRequestForGetOffers.setBirthdate(LocalDate.of(1990, 1, 1));
        validRequestForGetOffers.setPassportSeries("1234");
        validRequestForGetOffers.setPassportNumber("567890");

        validRequestForCalculateCredit = new ScoringDataDto();
        validRequestForCalculateCredit.setAmount(new BigDecimal("300000"));
        validRequestForCalculateCredit.setTerm(12);
        validRequestForCalculateCredit.setFirstName("Ivan");
        validRequestForCalculateCredit.setLastName("Petrov");
        validRequestForCalculateCredit.setMiddleName("Sergeevich");
        validRequestForCalculateCredit.setGender(Gender.MALE);
        validRequestForCalculateCredit.setBirthdate(LocalDate.parse("1990-05-15"));
        validRequestForCalculateCredit.setPassportSeries("1234");
        validRequestForCalculateCredit.setPassportNumber("567890");
        validRequestForCalculateCredit.setPassportIssueDate(LocalDate.parse("2010-06-20"));
        validRequestForCalculateCredit.setPassportIssueBranch("УФМС России по г. Москва");
        validRequestForCalculateCredit.setMaritalStatus(MaritalStatus.MARRIED);
        validRequestForCalculateCredit.setDependentAmount(2);
        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setEmployerINN("770123456789");
        employment.setSalary(new BigDecimal("150000.00"));
        employment.setPosition(Position.WORKER);
        employment.setWorkExperienceTotal(60);
        employment.setWorkExperienceCurrent(24);
        validRequestForCalculateCredit.setEmployment(employment);
        validRequestForCalculateCredit.setAccountNumber("40817810001234567890");
        validRequestForCalculateCredit.setIsInsuranceEnabled(false);
        validRequestForCalculateCredit.setIsSalaryClient(false);
    }

    @Test
    void shouldGetOffersCorrectly() {
        // Подготовка
        List<LoanOfferDto> expectedOffers = new ArrayList<>();

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/offers")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(validRequestForGetOffers)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expectedOffers);

        // Действие
        calculatorClientService.getOffers(validRequestForGetOffers);

        // Проверка
        verify(restClient, times(1)).post();
    }

    @Test
    void shouldThrowDealExternalServiceExceptionWithBadRequest() {
        // Подготовка
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/offers")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(validRequestForGetOffers)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // Действие и Проверка
        assertThatThrownBy(() -> calculatorClientService.getOffers(validRequestForGetOffers))
                .isInstanceOf(DealExternalServiceException.class)
                .hasMessageContaining("Ошибка при запросе в сервис калькулятора");
    }

    @Test
    void shouldThrowDealExternalServiceExceptionWithNullResponse() {
        // Подготовка
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/offers")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(validRequestForGetOffers)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);

        // Действие и Проверка
        assertThatThrownBy(() -> calculatorClientService.getOffers(validRequestForGetOffers))
                .isInstanceOf(DealExternalServiceException.class)
                .hasMessageContaining("Данные о предложениях по кредиту равны null");
    }

    @Test
    void shouldCalculateCreditCorrectly() {
        // Подготовка
        CreditDto creditDto = new CreditDto();

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/calc")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(validRequestForCalculateCredit)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CreditDto.class)).thenReturn(creditDto);

        // Действие
        calculatorClientService.calculateCredit(validRequestForCalculateCredit);

        // Проверка
        verify(restClient, times(1)).post();
    }

    @Test
    void shouldThrowDealExternalServiceExceptionWithBadRequestForCalcCredit() {
        // Подготовка
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/calc")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(validRequestForCalculateCredit)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CreditDto.class))
                .thenThrow(new RuntimeException("Connection refused"));

        // Действие и Проверка
        assertThatThrownBy(() -> calculatorClientService.calculateCredit(validRequestForCalculateCredit))
                .isInstanceOf(DealExternalServiceException.class)
                .hasMessageContaining("Ошибка при запросе в сервис калькулятора");
    }

    @Test
    void shouldThrowDealExternalServiceExceptionWithNullResponseForCalcCredit() {
        // Подготовка
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/calc")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(validRequestForCalculateCredit)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CreditDto.class)).thenReturn(null);

        // Действие и Проверка
        assertThatThrownBy(() -> calculatorClientService.calculateCredit(validRequestForCalculateCredit))
                .isInstanceOf(DealExternalServiceException.class)
                .hasMessageContaining("Данные о кредите равны null");
    }
}