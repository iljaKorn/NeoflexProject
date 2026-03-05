package com.example.calculator.model.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LoanOfferDto {
    private UUID statementId;
    private BigDecimal requestAmount;
    private BigDecimal totalAmount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal BigDecimal;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
}
