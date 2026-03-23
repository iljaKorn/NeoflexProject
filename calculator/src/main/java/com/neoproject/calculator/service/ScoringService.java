package com.neoproject.calculator.service;

import com.neoproject.calculator.exception.ScoringException;
import com.neoproject.calculator.model.dto.LoanStatementRequestDto;
import com.neoproject.calculator.model.dto.ScoringDataDto;
import com.neoproject.calculator.model.dto.enums.EmploymentStatus;
import com.neoproject.calculator.model.dto.enums.Gender;
import com.neoproject.calculator.model.dto.enums.MaritalStatus;
import com.neoproject.calculator.model.dto.enums.Position;
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

    /**
     * Метод для скоринга данных
     * @param dto специальный объект со всеми входными данными для скоринга
     */
    public Integer scoring(ScoringDataDto dto) {
        if (dto.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            log.debug("Данные не прошли скоринг по статусу на работе");
            throw new ScoringException("Клиент безработный");
        }

        // если сумма заёма больше, чем 24 зарплаты, то отказ
        if (dto.getAmount().compareTo(dto.getEmployment().getSalary().multiply(BigDecimal.valueOf(24))) == 1) {
            log.debug("Данные не прошли скоринг по величине заёма");
            throw new ScoringException("Сумма кредита превышает 24 зарплаты");
        }

        int age = LocalDate.now().getYear() - dto.getBirthdate().getYear();
        if (age < 20 || age > 65) {
            log.debug("Данные не прошли скоринг по возрасту");
            throw new ScoringException("Клиент не подходит по возрасту");
        }

        if (dto.getEmployment().getWorkExperienceTotal() < 18 || dto.getEmployment().getWorkExperienceCurrent() < 3) {
            log.debug("Данные не прошли скоринг по опыту работы");
            throw new ScoringException("Клиент не подходит по опыту работы");
        }

        int scoreRate = 0;
        scoreRate += scoringByEmploymentStatus(dto.getEmployment().getEmploymentStatus());
        scoreRate += scoringByPosition(dto.getEmployment().getPosition());
        scoreRate += scoringByMaritalStatus(dto.getMaritalStatus());
        scoreRate += scoringByGenderAndAge(dto.getGender(), age);

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
}
