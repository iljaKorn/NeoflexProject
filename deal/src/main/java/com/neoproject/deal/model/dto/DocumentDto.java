package com.neoproject.deal.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO с данными о для оформления документов")
public class DocumentDto {

    @Schema(description = "Имя клиента")
    private String firstName;

    @Schema(description = "Фамилия клиента")
    private String lastName;

    @Schema(description = "Серия паспорта")
    private String passportSeries;

    @Schema(description = "Номер паспорта")
    private String passportNumber;

    @Schema(description = "Сумма кредита")
    private BigDecimal amount;

    @Schema(description = "Срок кредита")
    private Integer term;

    @Schema(description = "Ежемесячный платеж")
    private BigDecimal monthlyPayment;

    @Schema(description = "Ставка")
    private BigDecimal rate;
}
