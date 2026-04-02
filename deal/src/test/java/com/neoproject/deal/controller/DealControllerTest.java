package com.neoproject.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.neoproject.deal.model.dto.EmploymentDto;
import com.neoproject.deal.model.dto.FinishRegistrationRequestDto;
import com.neoproject.deal.model.dto.LoanOfferDto;
import com.neoproject.deal.model.dto.LoanStatementRequestDto;
import com.neoproject.deal.model.enums.EmploymentStatus;
import com.neoproject.deal.model.enums.Gender;
import com.neoproject.deal.model.enums.MaritalStatus;
import com.neoproject.deal.model.enums.Position;
import com.neoproject.deal.service.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DealController.class)
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DealService dealService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("POST /deal/statement - успешный расчет предложений и сохранение в базу данных")
    void getOffersCorrectly() throws Exception {
        // Подготовка
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(new BigDecimal("300000"));
        request.setTerm(12);
        request.setFirstName("Ivan");
        request.setLastName("Petrov");
        request.setMiddleName("Sergeevich");
        request.setEmail("ivan@example.com");
        request.setBirthdate(LocalDate.of(1990, 1, 1));
        request.setPassportSeries("1234");
        request.setPassportNumber("567890");

        when(dealService.getOffers(any())).thenReturn(List.of());

        // Действие и Проверка
        mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(dealService, times(1)).getOffers(any(LoanStatementRequestDto.class));
    }

    @Test
    @DisplayName("POST /deal/statement - ошибки валидации")
    void shouldThrowValidationException() throws Exception {
        // Подготовка
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(new BigDecimal("-300000"));
        request.setTerm(-12);
        request.setFirstName("Ivan");
        request.setEmail("ivan@example.com");
        request.setBirthdate(LocalDate.of(2025, 1, 1));

        when(dealService.getOffers(any())).thenReturn(List.of());

        // Действие и Проверка
        mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(dealService, never()).getOffers(any());
    }

    @Test
    @DisplayName("POST /deal/offer/select - выбор предложения и сохранение в базу данных")
    void selectOfferCorrectly() throws Exception {
        // Подготовка
        LoanOfferDto request = new LoanOfferDto();
        request.setStatementId(UUID.randomUUID());
        request.setRequestAmount(new BigDecimal("300000"));
        request.setTotalAmount(new BigDecimal("324929.88"));
        request.setTerm(12);
        request.setMonthlyPayment(new BigDecimal("27077.49"));
        request.setRate(new BigDecimal("15"));
        request.setIsInsuranceEnabled(false);
        request.setIsSalaryClient(false);

        doNothing().when(dealService).selectOffer(any());

        // Действие и Проверка
        mockMvc.perform(post("/deal/offer/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(dealService, times(1)).selectOffer(any());
    }

    @Test
    @DisplayName("POST /deal/offer/select - ошибки валидации")
    void shouldThrowValidationExceptionForSelectOffer() throws Exception {
        // Подготовка
        LoanOfferDto request = new LoanOfferDto();
        request.setRequestAmount(new BigDecimal("-300000"));
        request.setTotalAmount(new BigDecimal("-324929.88"));
        request.setTerm(-12);
        request.setMonthlyPayment(new BigDecimal("27077.49"));
        request.setRate(new BigDecimal("-15"));
        request.setIsInsuranceEnabled(false);
        request.setIsSalaryClient(false);

        doNothing().when(dealService).selectOffer(any());

        // Действие и Проверка
        mockMvc.perform(post("/deal/offer/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(dealService, never()).selectOffer(any());
    }

    @Test
    @DisplayName("POST /deal/calculate/{statementId} - завершение регистрации и сохранение в базу данных")
    void finishRegistrationCorrectly() throws Exception {
        // Подготовка
        FinishRegistrationRequestDto request = new FinishRegistrationRequestDto();
        request.setGender(Gender.MALE);
        request.setMaritalStatus(MaritalStatus.MARRIED);
        request.setDependentAmount(2);
        request.setPassportIssueDate(LocalDate.of(2010, 6, 20));
        request.setPassportIssueBranch("УФМС России по г. Москва");
        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setEmployerINN("123456789012");
        employment.setSalary(BigDecimal.valueOf(85000));
        employment.setPosition(Position.MID_MANAGER);
        employment.setWorkExperienceTotal(60);
        employment.setWorkExperienceCurrent(36);
        request.setEmployment(employment);
        request.setAccountNumber("40817810001234567890");

        doNothing().when(dealService).finishRegistration(any(), any());

        // Действие и Проверка
        mockMvc.perform(post("/deal/calculate/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(dealService, times(1)).finishRegistration(any(), any());
    }

    @Test
    @DisplayName("POST /deal/calculate/{statementId} - завершение регистрации и сохранение в базу данных")
    void shouldThrowValidationExceptionForFinishRegistration() throws Exception {
        // Подготовка
        FinishRegistrationRequestDto request = new FinishRegistrationRequestDto();
        request.setGender(Gender.MALE);
        request.setAccountNumber("40817810001234567890");

        doNothing().when(dealService).finishRegistration(any(), any());

        // Действие и Проверка
        mockMvc.perform(post("/deal/calculate/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(dealService, never()).finishRegistration(any(), any());
    }
}