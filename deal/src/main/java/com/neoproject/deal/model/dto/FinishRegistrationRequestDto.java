package com.neoproject.deal.model.dto;

import com.neoproject.deal.model.enums.Gender;
import com.neoproject.deal.model.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "DTO с данными для завершения регистрации")
public class FinishRegistrationRequestDto {

    @Schema(description = "Пол")
    private Gender gender;

    @Schema(description = "Данные о браке")
    private MaritalStatus maritalStatus;

    @Schema(description = "Количество иждивенцев")
    private Integer dependentAmount;

    @Schema(description = "Дата выдачи паспорта")
    private LocalDate passportIssueDate;

    @Schema(description = "Отделение, выдавшее паспорт")
    private String passportIssueBranch;

    @Schema(description = "Данные о работе")
    private EmploymentDto employment;

    @Schema(description = "Номер аккаунта")
    private String accountNumber;
}
