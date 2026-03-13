package com.example.calculator.service;

import com.example.calculator.model.DTO.LoanStatementRequestDto;
import com.example.calculator.model.DTO.ScoringDataDto;
import com.example.calculator.model.DTO.enums.EmploymentStatus;
import com.example.calculator.model.DTO.enums.Gender;
import com.example.calculator.model.DTO.enums.MaritalStatus;
import com.example.calculator.model.DTO.enums.Position;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

/**
 * Сервис скоринга
 */
@Slf4j
@Service
public class ScoringService {

    private final Integer ADULT_AGE = 18;

    /**
     * Метод для скоринга данных
     * @param dto специальный объект со всеми входными данными для скоринга
     */
    public Integer scoring(ScoringDataDto dto) {
        int scoreRate = 0;

        EmploymentStatus employmentStatus = dto.getEmployment().getEmploymentStatus();
        if (employmentStatus == EmploymentStatus.UNEMPLOYED) {
            log.debug("Данные не прошли скоринг по статусу на работе");
            return 0;
        }
        scoreRate += scoringByEmploymentStatus(employmentStatus);

        Position position = dto.getEmployment().getPosition();
        scoreRate += scoringByPosition(position);

        // если сумма заёма больше, чем 24 зарплаты, то отказ
        BigDecimal amount = dto.getAmount();
        BigDecimal loanCeiling = dto.getEmployment().getSalary().multiply(BigDecimal.valueOf(24));
        if (amount.compareTo(loanCeiling) == 1) {
            log.debug("Данные не прошли скоринг по величине заёма");
            return 0;
        }

        MaritalStatus maritalStatus = dto.getMaritalStatus();
        scoreRate += scoringByMaritalStatus(maritalStatus);

        int age = LocalDate.now().getYear() - dto.getBirthdate().getYear();
        if (age < 20 || age > 65) {
            log.debug("Данные не прошли скоринг по возрасту");
            return 0;
        }

        Gender gender = dto.getGender();
        scoreRate += scoringByGenderAndAge(gender, age);

        int workExperienceTotal = dto.getEmployment().getWorkExperienceTotal();
        int workExperienceCurrent = dto.getEmployment().getWorkExperienceCurrent();
        if (workExperienceTotal < 18 || workExperienceCurrent < 3) {
            log.debug("Данные не прошли скоринг по опыту работы");
            return 0;
        }

        return scoreRate;
    }

    /**
     * Вспомогательный метод для скоринга данных по полу и возрасту
     * @param gender пол
     * @param age возраст
     */
    private Integer scoringByGenderAndAge(Gender gender, int age) {
        int rate = 0;
        if (gender == Gender.FEMALE && age > 32 && age < 60) {
            rate = -3;
        }
        if (gender == Gender.MALE && age > 32 && age < 55) {
            rate = -3;
        }
        if (gender == Gender.NON_BINARY) {
            rate = 7;
        }
        return rate;
    }

    /**
     * Вспомогательный метод для скоринга данных по семейному положению
     * @param maritalStatus семейное положение
     */
    private Integer scoringByMaritalStatus(MaritalStatus maritalStatus) {
        return switch (maritalStatus) {
            case MARRIED -> -3;
            case DIVORCED -> -1;
            default -> 0;
        };
    }

    /**
     * Вспомогательный метод для скоринга данных по позиции на работе
     * @param position позиция на работе
     */
    private Integer scoringByPosition(Position position) {
        return switch (position) {
            case MIDDLE_MANAGER -> -2;
            case TOP_MANAGER -> -3;
            default -> 0;
        };
    }

    /**
     * Вспомогательный метод для скоринга данных по статусу на работе
     * @param employmentStatus статус на работе
     */
    private Integer scoringByEmploymentStatus(EmploymentStatus employmentStatus) {
        return switch (employmentStatus) {
            case EMPLOYED -> -1; // новое добавленное правило
            case SELF_EMPLOYED -> 2;
            case BUSINESS_OWNER -> 1;
            default -> 0;
        };
    }

    /**
     * Метод для прескоринга данных
     * @param dto специальный объект с данными для прескоринга
     */
    public boolean preScoring(LoanStatementRequestDto dto) {
        LocalDate today = LocalDate.now();
        LocalDate birthDate = dto.getBirthdate();

        int age = Period.between(birthDate, today).getYears();

        return age >= ADULT_AGE;
    }
}
