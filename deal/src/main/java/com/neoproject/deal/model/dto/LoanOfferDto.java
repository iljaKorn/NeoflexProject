package com.neoproject.deal.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "DTO с данными о предложении по кредиту")
public class LoanOfferDto {

    @NotNull(message = "Id заявки обязателен")
    @Schema(description = "id предложения", example = "e481611a-66a5-44f4-9405-07141549dab2")
    private UUID statementId;

    @NotNull(message = "Сумма кредита обязательна")
    @Schema(description = "Запрашиваемая сумма кредита", example = "300000")
    private BigDecimal requestAmount;

    @NotNull(message = "Итоговая сумма выплат обязательна")
    @Schema(description = "Итоговый долг", example = "324929.88")
    private BigDecimal totalAmount;

    @NotNull(message = "Срок кредита обязателен")
    @Min(value = 6, message = "Срок кредита должен быть не меньше 6 месяцев")
    @Schema(description = "Срок кредитования", example = "12")
    private Integer term;

    @NotNull(message = "Ежемесячная плата обязательна")
    @Schema(description = "Ежемесячная выплата", example = "27077.49")
    private BigDecimal monthlyPayment;

    @NotNull(message = "Ставка обязательна")
    @Schema(description = "Ставка по кредиту", example = "15")
    private BigDecimal rate;

    @NotNull(message = "Данные о страховке обязательны")
    @Schema(description = "Наличие страховки", example = "false")
    private Boolean isInsuranceEnabled;

    @NotNull(message = "Данные о том, является ли заемщик клиентом банка, обязательны")
    @Schema(description = "Является ли клиент зарплатным", example = "false")
    private Boolean isSalaryClient;
}
