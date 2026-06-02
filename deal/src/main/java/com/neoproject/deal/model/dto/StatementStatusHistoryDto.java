package com.neoproject.deal.model.dto;

import com.neoproject.deal.model.enums.ChangeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "DTO с данными истории изменения статуса заявки")
public class StatementStatusHistoryDto {

    @Schema(description = "Статус заявки")
    private String status;

    @Schema(description = "Время изменения")
    private LocalDateTime time;

    @Schema(description = "Тип изменения")
    private ChangeType changeType;
}
