package com.neoproject.deal.model.dto;

import com.neoproject.deal.model.enums.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "DTO с данными о заявке")
public class StatementDto {

    @Schema(description = "Id заявки")
    private UUID statementId;

    @Schema(description = "Текущий статус")
    private ApplicationStatus status;

    @Schema(description = "Дата создания")
    private LocalDateTime creationDate;

    @Schema(description = "Принятое предложение")
    private LoanOfferDto appliedOffer;

    @Schema(description = "Дата подписания")
    private LocalDateTime signDate;

    @Schema(description = "Код для подписания")
    private String sesCode;

    @Schema(description = "История статусов")
    private List<StatementStatusHistoryDto> statusHistory;
}
