package com.neoproject.calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoproject.calculator.model.dto.*;
import com.neoproject.calculator.model.dto.enums.EmploymentStatus;
import com.neoproject.calculator.model.dto.enums.Gender;
import com.neoproject.calculator.model.dto.enums.MaritalStatus;
import com.neoproject.calculator.model.dto.enums.Position;
import com.neoproject.calculator.service.CalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CalculatorController.class)
@DisplayName("Тесты контроллера CalculatorController")
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CalculatorService calculatorService;

    private LoanStatementRequestDto validRequestForOffers;
    private ScoringDataDto validRequestForCredit;
    private List<LoanOfferDto> validResponseForOffers;
    private CreditDto validResponseForCredit;

    @BeforeEach
    void setUp() {
        validRequestForOffers = new LoanStatementRequestDto();
        validRequestForOffers.setAmount(new BigDecimal("300000"));
        validRequestForOffers.setTerm(12);
        validRequestForOffers.setFirstName("Ivan");
        validRequestForOffers.setLastName("Petrov");
        validRequestForOffers.setMiddleName("Sergeevich");
        validRequestForOffers.setEmail("ivan@example.com");
        validRequestForOffers.setBirthdate(LocalDate.of(1990, 1, 1));
        validRequestForOffers.setPassportSeries("1234");
        validRequestForOffers.setPassportNumber("567890");

        validRequestForCredit = new ScoringDataDto();
        validRequestForCredit.setAmount(new BigDecimal("300000"));
        validRequestForCredit.setTerm(12);
        validRequestForCredit.setFirstName("Ivan");
        validRequestForCredit.setLastName("Petrov");
        validRequestForCredit.setMiddleName("Sergeevich");
        validRequestForCredit.setGender(Gender.MALE);
        validRequestForCredit.setBirthdate(LocalDate.of(1990, 5, 15));
        validRequestForCredit.setPassportSeries("1234");
        validRequestForCredit.setPassportNumber("567890");
        validRequestForCredit.setPassportIssueDate(LocalDate.of(2010, 6, 20));
        validRequestForCredit.setPassportIssueBranch("УФМС России по г. Москва");
        validRequestForCredit.setMaritalStatus(MaritalStatus.MARRIED);
        validRequestForCredit.setDependentAmount(2);

        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setEmployerINN("770123456789");
        employment.setSalary(new BigDecimal("150000.00"));
        employment.setPosition(Position.WORKER);
        employment.setWorkExperienceTotal(60);
        employment.setWorkExperienceCurrent(24);
        validRequestForCredit.setEmployment(employment);

        validRequestForCredit.setAccountNumber("40817810001234567890");
        validRequestForCredit.setIsInsuranceEnabled(false);
        validRequestForCredit.setIsSalaryClient(false);

        LoanOfferDto offer1 = new LoanOfferDto();
        offer1.setStatementId(UUID.randomUUID());
        offer1.setRequestAmount(new BigDecimal("300000"));
        offer1.setTotalAmount(new BigDecimal("313260.84"));
        offer1.setTerm(6);
        offer1.setMonthlyPayment(new BigDecimal("52210.14"));
        offer1.setRate(new BigDecimal("15"));
        offer1.setIsInsuranceEnabled(false);
        offer1.setIsSalaryClient(false);

        LoanOfferDto offer2 = new LoanOfferDto();
        offer2.setStatementId(UUID.randomUUID());
        offer2.setRequestAmount(new BigDecimal("300000"));
        offer2.setTotalAmount(new BigDecimal("312368.40"));
        offer2.setTerm(6);
        offer2.setMonthlyPayment(new BigDecimal("52061.40"));
        offer2.setRate(new BigDecimal("14.0"));
        offer2.setIsInsuranceEnabled(false);
        offer2.setIsSalaryClient(true);

        LoanOfferDto offer3 = new LoanOfferDto();
        offer3.setStatementId(UUID.randomUUID());
        offer3.setRequestAmount(new BigDecimal("300000"));
        offer3.setTotalAmount(new BigDecimal("414116.10"));
        offer3.setTerm(6);
        offer3.setMonthlyPayment(new BigDecimal("69019.35"));
        offer3.setRate(new BigDecimal("12.0"));
        offer3.setIsInsuranceEnabled(true);
        offer3.setIsSalaryClient(false);

        LoanOfferDto offer4 = new LoanOfferDto();
        offer4.setStatementId(UUID.randomUUID());
        offer4.setRequestAmount(new BigDecimal("300000"));
        offer4.setTotalAmount(new BigDecimal("412930.92"));
        offer4.setTerm(6);
        offer4.setMonthlyPayment(new BigDecimal("68821.82"));
        offer4.setRate(new BigDecimal("11.0"));
        offer4.setIsInsuranceEnabled(true);
        offer4.setIsSalaryClient(true);

        List<LoanOfferDto> offers = new ArrayList<>();
        offers.add(offer1);
        offers.add(offer2);
        offers.add(offer3);
        offers.add(offer4);
        validResponseForOffers = offers;

        validResponseForCredit = new CreditDto();
        validResponseForCredit.setAmount(new BigDecimal("300000"));
        validResponseForCredit.setTerm(12);
        validResponseForCredit.setMonthlyPayment(new BigDecimal("26096.53"));
        validResponseForCredit.setRate(new BigDecimal("8"));
        validResponseForCredit.setPsk(new BigDecimal("313158.36"));
        validResponseForCredit.setIsInsuranceEnabled(false);
        validResponseForCredit.setIsSalaryClient(false);
    }

    @Test
    @DisplayName("POST /calculator/offers - успешный расчет предложений")
    void getOffersCorrectly() throws Exception {
        // Подготовка
        when(calculatorService.getOffers(any(LoanStatementRequestDto.class)))
                .thenReturn(validResponseForOffers);

        // Действие и Проверка
        mockMvc.perform(post("/calculator/offers")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestForOffers)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(calculatorService, times(1)).getOffers(any(LoanStatementRequestDto.class));
    }

    @Test
    @DisplayName("POST /calculator/offers - ошибки валидации")
    void getOffersWithValidationErrors() throws Exception {
        // Подготовка
        LoanStatementRequestDto invalidRequest = new LoanStatementRequestDto();
        invalidRequest.setAmount(new BigDecimal("-100"));
        invalidRequest.setTerm(-12);
        invalidRequest.setFirstName("");
        invalidRequest.setBirthdate(LocalDate.now().plusDays(1));

        // Действие и Проверка
        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(calculatorService, never()).getOffers(any());
    }

    @Test
    @DisplayName("POST /calculator/calc - успешный расчет кредита")
    void calculateCreditCorrectly() throws Exception {
        // Подготовка
        when(calculatorService.calculateCredit(any(ScoringDataDto.class)))
                .thenReturn(validResponseForCredit);

        // Действие и Проверка
        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestForCredit)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(calculatorService, times(1)).calculateCredit(any(ScoringDataDto.class));
    }

    @Test
    @DisplayName("POST /calculator/calc - ошибки валидации")
    void calculateCreditWithValidationErrors() throws Exception {
        // Подготовка
        ScoringDataDto invalidScoring = new ScoringDataDto();
        invalidScoring.setAmount(new BigDecimal("-300000"));
        invalidScoring.setTerm(-24);
        invalidScoring.setFirstName("");
        invalidScoring.setBirthdate(LocalDate.now().plusYears(1));

        // Действие и Проверка
        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidScoring)))
                .andExpect(status().isBadRequest());

        verify(calculatorService, never()).calculateCredit(any());
    }
}