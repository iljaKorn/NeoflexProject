package com.neoproject.deal.model.dto;

import com.neoproject.deal.model.enums.EmploymentStatus;
import com.neoproject.deal.model.enums.Position;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO с данными о работе")
public class EmploymentDto {

    @Schema(description = "Статус работы", example = "EMPLOYED")
    private EmploymentStatus employmentStatus;

    @Schema(description = "ИНН", example = "770123456789")
    private String employerINN;

    @Schema(description = "Зарплата", example = "150000.00")
    private BigDecimal salary;

    @Schema(description = "Позиция на работе", example = "WORKER")
    private Position position;

    @Schema(description = "Общий опыт работы", example = "60")
    private Integer workExperienceTotal;

    @Schema(description = "Опыт работы на текущем месте", example = "24")
    private Integer workExperienceCurrent;
}
