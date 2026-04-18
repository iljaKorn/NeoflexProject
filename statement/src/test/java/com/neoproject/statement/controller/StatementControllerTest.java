package com.neoproject.statement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.neoproject.statement.model.dto.LoanOfferDto;
import com.neoproject.statement.model.dto.LoanStatementRequestDto;
import com.neoproject.statement.service.StatementService;
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

@WebMvcTest(controllers = StatementController.class)
class StatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatementService dealService;

    private final ObjectMapper objectMapper;

    StatementControllerTest(){
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("POST /statement - успешный расчет предложений и сохранение в базу данных")
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
        mockMvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(dealService, times(1)).getOffers(any(LoanStatementRequestDto.class));
    }

    @Test
    @DisplayName("POST /statement - ошибки валидации")
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
        mockMvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(dealService, never()).getOffers(any());
    }

    @Test
    @DisplayName("POST /statement/offer - выбор предложения и сохранение в базу данных")
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
        mockMvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(dealService, times(1)).selectOffer(any());
    }

    @Test
    @DisplayName("POST /statement/offer - ошибки валидации")
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
        mockMvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(dealService, never()).selectOffer(any());
    }
}