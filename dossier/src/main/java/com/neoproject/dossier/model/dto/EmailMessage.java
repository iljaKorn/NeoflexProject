package com.neoproject.dossier.model.dto;

import com.neoproject.dossier.model.enums.Theme;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "DTO с данными для отправки сообщения на почту")
public class EmailMessage {

    @Schema(description = "Адрес почты")
    private String address;

    @Schema(description = "Тема сообщения")
    private Theme theme;

    @Schema(description = "Id заявки")
    private UUID statementId;

    @Schema(description = "Текст сообщения")
    private String text;
}
