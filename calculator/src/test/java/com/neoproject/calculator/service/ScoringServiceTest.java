package com.neoproject.calculator.service;

import com.neoproject.calculator.exception.ScoringException;
import com.neoproject.calculator.model.dto.EmploymentDto;
import com.neoproject.calculator.model.dto.ScoringDataDto;
import com.neoproject.calculator.model.dto.enums.EmploymentStatus;
import com.neoproject.calculator.model.dto.enums.Gender;
import com.neoproject.calculator.model.dto.enums.MaritalStatus;
import com.neoproject.calculator.model.dto.enums.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Тесты для сервиса скоринга")
@ExtendWith(MockitoExtension.class)
class ScoringServiceTest {

    private ScoringService scoringService;

    private ScoringDataDto validRequestForCredit;

    @BeforeEach
    void setUp() {
        scoringService = new ScoringService();

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
        employment.setSalary(new BigDecimal("15000.00"));
        employment.setPosition(Position.MIDDLE_MANAGER);
        employment.setWorkExperienceTotal(60);
        employment.setWorkExperienceCurrent(24);
        validRequestForCredit.setEmployment(employment);

        validRequestForCredit.setAccountNumber("40817810001234567890");
        validRequestForCredit.setIsInsuranceEnabled(false);
        validRequestForCredit.setIsSalaryClient(false);
    }

    @Test
    void shouldCalculateRateCorrectly() {
        // Действие
        Integer actualValue = scoringService.scoring(validRequestForCredit);

        // Проверка
        assertThat(actualValue).isEqualTo(-9);
    }

    @Test
    void shouldCalculateRateCorrectlyWithFemaleGenderAndSelfEmployedStatus() {
        // Подготовка
        validRequestForCredit.setGender(Gender.FEMALE);
        validRequestForCredit.getEmployment().setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);

        // Действие
        Integer actualValue = scoringService.scoring(validRequestForCredit);

        // Проверка
        assertThat(actualValue).isEqualTo(-6);
    }

    @Test
    void shouldCalculateRateCorrectlyWithNonBinaryGenderAndBusinessOwnerStatus() {
        // Подготовка
        validRequestForCredit.setGender(Gender.NON_BINARY);
        validRequestForCredit.getEmployment().setEmploymentStatus(EmploymentStatus.BUSINESS_OWNER);

        // Действие
        Integer actualValue = scoringService.scoring(validRequestForCredit);

        // Проверка
        assertThat(actualValue).isEqualTo(3);
    }

    @Test
    void shouldCalculateRateCorrectlyWithDivorcedAndTopManagerStatus() {
        // Подготовка
        validRequestForCredit.setMaritalStatus(MaritalStatus.DIVORCED);
        validRequestForCredit.getEmployment().setPosition(Position.TOP_MANAGER);

        // Действие
        Integer actualValue = scoringService.scoring(validRequestForCredit);

        // Проверка
        assertThat(actualValue).isEqualTo(-8);
    }

    @Test
    void shouldThrowScoringExceptionWithUnemploymentStatus() {
        // Подготовка
        validRequestForCredit.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);

        // Действие и Проверка
        assertThatThrownBy(() -> scoringService.scoring(validRequestForCredit))
                .isInstanceOf(ScoringException.class)
                .hasMessageContaining("Клиент безработный");
    }

    @Test
    void shouldThrowScoringExceptionWithTooBigAmount() {
        // Подготовка
        validRequestForCredit.setAmount(new BigDecimal(1000000));

        // Действие и Проверка
        assertThatThrownBy(() -> scoringService.scoring(validRequestForCredit))
                .isInstanceOf(ScoringException.class)
                .hasMessageContaining("Сумма кредита превышает 24 зарплаты");
    }

    @Test
    void shouldThrowScoringExceptionWithNotValidAge() {
        // Подготовка
        validRequestForCredit.setBirthdate(LocalDate.of(1955, 1, 1));

        // Действие и Проверка
        assertThatThrownBy(() -> scoringService.scoring(validRequestForCredit))
                .isInstanceOf(ScoringException.class)
                .hasMessageContaining("Клиент не подходит по возрасту");
    }

    @Test
    void shouldThrowScoringExceptionWithNotValidWorkExperience() {
        // Подготовка
        validRequestForCredit.getEmployment().setWorkExperienceCurrent(1);

        // Действие и Проверка
        assertThatThrownBy(() -> scoringService.scoring(validRequestForCredit))
                .isInstanceOf(ScoringException.class)
                .hasMessageContaining("Клиент не подходит по опыту работы");
    }
}