package com.neoproject.deal.model.dto;

import com.neoproject.deal.model.enums.Gender;
import com.neoproject.deal.model.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "DTO с данными для завершения регистрации")
public class FinishRegistrationRequestDto {

    @NotNull(message = "Пол обязателен")
    @Schema(description = "Пол", example = "MALE")
    private Gender gender;

    @NotNull(message = "Данные о браке обязательны")
    @Schema(description = "Данные о браке", example = "MARRIED")
    private MaritalStatus maritalStatus;

    @NotNull(message = "Количество иждивенцев обязательно")
    @Schema(description = "Количество иждивенцев", example = "2")
    private Integer dependentAmount;

    @NotNull(message = "Дата выдачи паспорта обязательна")
    @Schema(description = "Дата выдачи паспорта", example = "2010-06-20")
    private LocalDate passportIssueDate;

    @NotNull(message = "Отделение, выдавшее паспорт, обязательно")
    @Schema(description = "Отделение, выдавшее паспорт", example = "УФМС России по г. Москва")
    private String passportIssueBranch;

    @NotNull(message = "Данные о работе обязательны")
    @Schema(description = "Данные о работе")
    private EmploymentDto employment;

    @NotNull(message = "Номер аккаунта обязателен")
    @Schema(description = "Номер аккаунта", example = "40817810001234567890")
    private String accountNumber;
}
