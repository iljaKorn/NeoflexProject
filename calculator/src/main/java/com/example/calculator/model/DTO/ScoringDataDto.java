package com.example.calculator.model.DTO;

import com.example.calculator.model.DTO.enums.Gender;
import com.example.calculator.model.DTO.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "DTO с данными для скоринга и расчёта всех необходимых параметров кредита")
public class ScoringDataDto {

    @NotNull(message = "Сумма кредита обязательна")
    @DecimalMin(value = "20000.0", message = "Сумма кредита должна быть не меньше 20000")
    @Schema(description = "Сумма кредита", example = "300000")
    private BigDecimal amount;

    @NotNull(message = "Срок кредита обязателен")
    @Min(value = 6, message = "Срок кредита должен быть не меньше 6 месяцев")
    @Schema(description = "Срок кредитования", example = "12")
    private Integer term;

    @NotNull(message = "Имя обязательно")
    @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Имя должно содержать от 2 до 30 символов")
    @Schema(description = "Имя заёмщика", example = "Ivan")
    private String firstName;

    @NotNull(message = "Фамилия обязательна")
    @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Фамилия должна содержать от 2 до 30 символов")
    @Schema(description = "Фамилия заёмщика", example = "Petrov")
    private String lastName;

    @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Отчество должно содержать от 2 до 30 символов")
    @Schema(description = "Отчество заёмщика", example = "Sergeevich")
    private String middleName;

    @NotNull(message = "Пол обязателен")
    @Schema(description = "Пол", example = "MALE")
    private Gender gender;

    @NotNull(message = "Дата рождения обязательна")
    @Schema(description = "Дата рождения", example = "1990-05-15")
    private LocalDate birthdate;

    @NotNull(message = "Серия паспорта обязательна")
    @Pattern(regexp = "^\\d{4}$", message = "Серия паспорта должна содержать 4 символа")
    @Schema(description = "Серия паспорта", example = "1234")
    private String passportSeries;

    @NotNull(message = "Номер паспорта обязателен")
    @Pattern(regexp = "^\\d{6}$", message = "Номер паспорта должен содержать 6 символов")
    @Schema(description = "Номер паспорта", example = "567890")
    private String passportNumber;

    @Schema(description = "Дата выдачи паспорта", example = "2010-06-20")
    private LocalDate passportIssueDate;

    @Schema(description = "Кем выдан паспорт", example = "УФМС России по г. Москва")
    private String passportIssueBranch;

    @NotNull(message = "Семейное положение обязательно")
    @Schema(description = "Семейное положение", example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Schema(description = "Количество иждивенцев", example = "2")
    private Integer dependentAmount;

    @NotNull(message = "Сведения о работе обязательны")
    @Schema(description = "Сведения о работе")
    private EmploymentDto employment;

    @Schema(description = "Номер аккаунта", example = "40817810001234567890")
    private String accountNumber;

    @NotNull(message = "Сведения о страховке обязательны")
    @Schema(description = "Наличие страховки", example = "false")
    private Boolean isInsuranceEnabled;

    @NotNull(message = "Сведения о том, является ли человек клиентом банка, обязательны")
    @Schema(description = "Является ли клиент зарплатным", example = "false")
    private Boolean isSalaryClient;
}
