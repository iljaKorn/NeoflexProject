package com.example.calculator.service;

import com.example.calculator.model.DTO.LoanStatementRequestDto;
import com.example.calculator.model.DTO.ScoringDataDto;
import com.example.calculator.model.DTO.enums.EmploymentStatus;
import com.example.calculator.model.DTO.enums.Gender;
import com.example.calculator.model.DTO.enums.MaritalStatus;
import com.example.calculator.model.DTO.enums.Position;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Service
public class ScoringService {

    private final Integer ADULT_AGE = 18;

    public Integer scoring(ScoringDataDto dto) {
        int scoreRate = 0;

        EmploymentStatus employmentStatus = dto.getEmployment().getEmploymentStatus();
        if (employmentStatus == EmploymentStatus.UNEMPLOYED) {
            return 0;
        }
        scoreRate += scoringByEmployment(employmentStatus);

        Position position = dto.getEmployment().getPosition();
        scoreRate += scoringByPosition(position);

        // если сумма заёма больше, чем 24 зарплаты, то отказ
        BigDecimal amount = dto.getAmount();
        BigDecimal loanCeiling = dto.getEmployment().getSalary().multiply(BigDecimal.valueOf(24));
        if (amount.compareTo(loanCeiling) == 1) {
            return 0;
        }

        MaritalStatus maritalStatus = dto.getMaritalStatus();
        scoreRate += scoringByMaritalStatus(maritalStatus);

        int age = LocalDate.now().getYear() - dto.getBirthdate().getYear();
        if (age < 20 || age > 65) {
            return 0;
        }

        Gender gender = dto.getGender();
        scoreRate += scoringByGenderAndAge(gender, age);

        int workExperienceTotal = dto.getEmployment().getWorkExperienceTotal();
        int workExperienceCurrent = dto.getEmployment().getWorkExperienceCurrent();
        if (workExperienceTotal < 18 || workExperienceCurrent < 3) {
            return 0;
        }

        return scoreRate;
    }

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

    private Integer scoringByMaritalStatus(MaritalStatus maritalStatus) {
        return switch (maritalStatus) {
            case MARRIED -> -3;
            case DIVORCED -> -1;
            default -> 0;
        };
    }

    private Integer scoringByPosition(Position position) {
        return switch (position) {
            case MIDDLE_MANAGER -> -2;
            case TOP_MANAGER -> -3;
            default -> 0;
        };
    }

    private Integer scoringByEmployment(EmploymentStatus employmentStatus) {
        return switch (employmentStatus) {
            case EMPLOYED -> -1; // новое добавленное правило
            case SELF_EMPLOYED -> 2;
            case BUSINESS_OWNER -> 1;
            default -> 0;
        };
    }

    public boolean preScoring(LoanStatementRequestDto dto) {
        LocalDate today = LocalDate.now();
        LocalDate birthDate = dto.getBirthdate();

        int age = Period.between(birthDate, today).getYears();

        return age >= ADULT_AGE;
    }
}
