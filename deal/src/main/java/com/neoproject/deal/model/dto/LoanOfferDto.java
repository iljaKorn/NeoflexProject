package com.neoproject.deal.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "DTO с данными о предложении по кредиту")
public class LoanOfferDto {

    @Schema(description = "id предложения")
    private UUID statementId;

    @Schema(description = "Запрашиваемая сумма кредита")
    private BigDecimal requestAmount;

    @Schema(description = "Итоговый долг")
    private BigDecimal totalAmount;

    @Schema(description = "Срок кредитования")
    private Integer term;

    @Schema(description = "Ежемесячная выплата")
    private BigDecimal monthlyPayment;

    @Schema(description = "Ставка по кредиту")
    private BigDecimal rate;

    @Schema(description = "НАличие страховки")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Является ли клиент зарплатным")
    private Boolean isSalaryClient;
}
