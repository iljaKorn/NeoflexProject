package com.example.calculator.service;

import com.example.calculator.model.DTO.LoanStatementRequestDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class PreScoringService {

    public boolean preScoring(LoanStatementRequestDto dto) {
        LocalDate today = LocalDate.now();
        LocalDate birthDate = dto.getBirthdate();

        int age = Period.between(birthDate, today).getYears();

        return age >= 18;
    }
}
