package com.neoproject.deal.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "DTO с данными о предложении по кредиту")
public class LoanOfferDto {

    @Schema(description = "id предложения", example = "e481611a-66a5-44f4-9405-07141549dab2")
    private UUID statementId;

    @Schema(description = "Запрашиваемая сумма кредита", example = "300000")
    private BigDecimal requestAmount;

    @Schema(description = "Итоговый долг", example = "324929.88")
    private BigDecimal totalAmount;

    @Schema(description = "Срок кредитования", example = "12")
    private Integer term;

    @Schema(description = "Ежемесячная выплата", example = "27077.49")
    private BigDecimal monthlyPayment;

    @Schema(description = "Ставка по кредиту", example = "15")
    private BigDecimal rate;

    @Schema(description = "Наличие страховки", example = "false")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Является ли клиент зарплатным", example = "false")
    private Boolean isSalaryClient;
}
