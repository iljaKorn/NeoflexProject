package com.neoproject.deal.model.dto;

import com.neoproject.deal.model.enums.Gender;
import com.neoproject.deal.model.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "DTO с данными для завершения регистрации")
public class FinishRegistrationRequestDto {

    @Schema(description = "Пол", example = "MALE")
    private Gender gender;

    @Schema(description = "Данные о браке", example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Schema(description = "Количество иждивенцев", example = "2")
    private Integer dependentAmount;

    @Schema(description = "Дата выдачи паспорта", example = "2010-06-20")
    private LocalDate passportIssueDate;

    @Schema(description = "Отделение, выдавшее паспорт", example = "УФМС России по г. Москва")
    private String passportIssueBranch;

    @Schema(description = "Данные о работе")
    private EmploymentDto employment;

    @Schema(description = "Номер аккаунта", example = "40817810001234567890")
    private String accountNumber;
}
