package com.neoproject.deal.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "DTO с данными об одном платеже")
public class PaymentScheduleElementDto {

    @Schema(description = "Порядковый номер платежа")
    private Integer number;

    @Schema(description = "Дата платежа")
    private LocalDate date;

    @Schema(description = "Общая сумма платежа")
    private BigDecimal totalPayment;

    @Schema(description = "Часть платежа, идущая на погашение процентов")
    private BigDecimal interestPayment;

    @Schema(description = "Часть платежа, идущая на погашение основного долга")
    private BigDecimal debtPayment;

    @Schema(description = "Остаток долга после платежа")
    private BigDecimal remainingDebt;
}
