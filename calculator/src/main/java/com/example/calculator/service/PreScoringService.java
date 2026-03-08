package com.example.calculator.service;

import com.example.calculator.model.DTO.LoanStatementRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class PreScoringService {

    private final Integer ADULT_AGE = 18;

    public boolean preScoring(LoanStatementRequestDto dto) {
        LocalDate today = LocalDate.now();
        LocalDate birthDate = dto.getBirthdate();

        int age = Period.between(birthDate, today).getYears();

        return age >= ADULT_AGE;
    }
}
