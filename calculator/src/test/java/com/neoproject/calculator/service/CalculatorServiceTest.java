package com.neoproject.calculator.service;

import com.neoproject.calculator.model.dto.*;
import com.neoproject.calculator.model.dto.enums.EmploymentStatus;
import com.neoproject.calculator.model.dto.enums.Gender;
import com.neoproject.calculator.model.dto.enums.MaritalStatus;
import com.neoproject.calculator.model.dto.enums.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Тесты для сервиса калькулятора")
@ExtendWith(MockitoExtension.class)
class CalculatorServiceTest {

    private static final BigDecimal START_RATE = new BigDecimal("15.0");
    private static final BigDecimal INSURANCE_PRICE = new BigDecimal("100000");
    private static final BigDecimal INSURANCE_DISCOUNT = new BigDecimal("3.0");
    private static final BigDecimal SALARY_DISCOUNT = new BigDecimal("1.0");

    @InjectMocks
    private CalculatorService calculatorService;

    @Mock
    private ScoringService scoringService;

    private LoanStatementRequestDto validRequestForOffers;
    private ScoringDataDto validRequestForCredit;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(calculatorService, "startRate", START_RATE);
        ReflectionTestUtils.setField(calculatorService, "insurancePrice", INSURANCE_PRICE);
        ReflectionTestUtils.setField(calculatorService, "insuranceDiscount", INSURANCE_DISCOUNT);
        ReflectionTestUtils.setField(calculatorService, "salaryDiscount", SALARY_DISCOUNT);

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
    }

    @Test
    void shouldReturnListWithOffers() {
        // Действие
        List<LoanOfferDto> offers = calculatorService.getOffers(validRequestForOffers);

        // Проверка
        assertThat(offers).hasSize(4);

        assertThat(offers)
                .extracting(LoanOfferDto::getRate)
                .isSortedAccordingTo((r1, r2) -> r2.compareTo(r1));

        for (LoanOfferDto offer : offers) {
            assertThat(offer.getStatementId()).isNotNull();
            assertThat(offer.getRequestAmount()).isEqualByComparingTo(new BigDecimal("300000"));
            assertThat(offer.getTerm()).isEqualTo(12);
            assertThat(offer.getMonthlyPayment()).isNotNull();

            assertThat(offer.getTotalAmount()).isEqualByComparingTo(offer.getMonthlyPayment().multiply(BigDecimal.valueOf(12))
                    .setScale(2, RoundingMode.HALF_UP));

            if (offer.getIsInsuranceEnabled() && offer.getIsSalaryClient()) {
                assertThat(offer.getRate()).isEqualByComparingTo(new BigDecimal("11"));
            }
            if (!offer.getIsInsuranceEnabled() && offer.getIsSalaryClient()) {
                assertThat(offer.getRate()).isEqualByComparingTo(new BigDecimal("14"));
            }
            if (offer.getIsInsuranceEnabled() && !offer.getIsSalaryClient()) {
                assertThat(offer.getRate()).isEqualByComparingTo(new BigDecimal("12"));
            }
            if (!offer.getIsInsuranceEnabled() && !offer.getIsSalaryClient()) {
                assertThat(offer.getRate()).isEqualByComparingTo(new BigDecimal("15"));
            }
        }
    }

    @Test
    void shouldCalculateMonthlyPaymentCorrectly() {
        // Действие
        List<LoanOfferDto> offers = calculatorService.getOffers(validRequestForOffers);

        // Проверка
        for (LoanOfferDto offer : offers) {
            if (offer.getIsInsuranceEnabled() && offer.getIsSalaryClient()) {
                assertThat(offer.getMonthlyPayment()).isEqualByComparingTo(new BigDecimal("35352.66"));
            }
            if (!offer.getIsInsuranceEnabled() && offer.getIsSalaryClient()) {
                assertThat(offer.getMonthlyPayment()).isEqualByComparingTo(new BigDecimal("26936.14"));
            }
            if (offer.getIsInsuranceEnabled() && !offer.getIsSalaryClient()) {
                assertThat(offer.getMonthlyPayment()).isEqualByComparingTo(new BigDecimal("35539.52"));
            }
            if (!offer.getIsInsuranceEnabled() && !offer.getIsSalaryClient()) {
                assertThat(offer.getMonthlyPayment()).isEqualByComparingTo(new BigDecimal("27077.49"));
            }
        }
    }

    @Test
    void shouldReturnCreditDto() {
        // Подготовка
        when(scoringService.scoring(any(ScoringDataDto.class))).thenReturn(-7);

        // Действие
        CreditDto creditDto = calculatorService.calculateCredit(validRequestForCredit);

        // Проверка
        assertThat(creditDto.getAmount()).isEqualByComparingTo(new BigDecimal("300000"));
        assertThat(creditDto.getTerm()).isEqualTo(12);
        assertThat(creditDto.getMonthlyPayment()).isNotNull();
        assertThat(creditDto.getRate()).isEqualTo(START_RATE.add(new BigDecimal("-7")));

        assertThat(creditDto.getPsk()).isEqualByComparingTo(creditDto.getMonthlyPayment().multiply(BigDecimal.valueOf(12))
                .setScale(2, RoundingMode.HALF_UP));

        assertThat(creditDto.getPaymentSchedule()).isNotNull();
        assertThat(creditDto.getPaymentSchedule()).hasSize(12);
    }

    @Test
    void shouldReturnCreditDtoForPersonWithInsurance() {
        // Подготовка
        when(scoringService.scoring(any(ScoringDataDto.class))).thenReturn(-7);
        validRequestForCredit.setIsInsuranceEnabled(true);

        // Действие
        CreditDto creditDto = calculatorService.calculateCredit(validRequestForCredit);

        // Проверка
        assertThat(creditDto.getAmount()).isEqualByComparingTo(new BigDecimal("300000"));
        assertThat(creditDto.getTerm()).isEqualTo(12);
        assertThat(creditDto.getMonthlyPayment()).isNotNull();
        assertThat(creditDto.getRate()).isEqualTo(START_RATE.add(new BigDecimal("-10")));

        assertThat(creditDto.getPsk()).isEqualByComparingTo(creditDto.getMonthlyPayment().multiply(BigDecimal.valueOf(12))
                .setScale(2, RoundingMode.HALF_UP));

        assertThat(creditDto.getPaymentSchedule()).isNotNull();
        assertThat(creditDto.getPaymentSchedule()).hasSize(12);
    }

    @Test
    void shouldCalculatePaymentScheduleCorrectly(){
        // Подготовка
        when(scoringService.scoring(any(ScoringDataDto.class))).thenReturn(-7);

        // Действие
        CreditDto creditDto = calculatorService.calculateCredit(validRequestForCredit);
        List<PaymentScheduleElementDto> paymentSchedule = creditDto.getPaymentSchedule();

        // Проверка
        for(int i = 0; i < paymentSchedule.size(); i++){
            // последний платеж немного отличается от остальных
            if (i == paymentSchedule.size() - 1){
                assertThat(paymentSchedule.get(i).getTotalPayment()).isEqualTo(new BigDecimal("26096.50"));
            } else {
                assertThat(paymentSchedule.get(i).getTotalPayment()).isEqualTo(new BigDecimal("26096.53"));
            }
            assertThat(paymentSchedule.get(i).getNumber()).isEqualTo(i + 1);
            assertThat(paymentSchedule.get(i).getInterestPayment().add(paymentSchedule.get(i).getDebtPayment()))
                    .isEqualTo(paymentSchedule.get(i).getTotalPayment());

        }
    }
}