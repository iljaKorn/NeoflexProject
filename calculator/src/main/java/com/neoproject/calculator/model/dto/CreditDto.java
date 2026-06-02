package com.neoproject.calculator.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "DTO с данными о кредите")
public class CreditDto {

    @Schema(description = "Сумма кредита")
    private BigDecimal amount;

    @Schema(description = "Срок кредитования")
    private Integer term;

    @Schema(description = "Ежемесячная выплата")
    private BigDecimal monthlyPayment;

    @Schema(description = "Ставка по кредиту")
    private BigDecimal rate;

    @Schema(description = "Полная стоимость кредита")
    private BigDecimal psk;

    @Schema(description = "Наличие страховки")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Является ли клиент зарплатным")
    private Boolean isSalaryClient;

    @Schema(description = "График платежей")
    private List<PaymentScheduleElementDto> paymentSchedule;
}
