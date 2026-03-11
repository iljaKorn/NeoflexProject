package com.example.calculator.model.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "DTO с данными для запроса различных условий кредита")
public class LoanStatementRequestDto {

    @NotNull(message = "Сумма кредита обязательна")
    @DecimalMin(value = "20000.0", message = "Сумма кредита должна быть не меньше 20000")
    @Schema(description = "Сумма кредита", example = "300000")
    private BigDecimal amount;

    @NotNull(message = "Срок кредита обязателен")
    @Min(value = 6, message = "Срок кредита должен быть не меньше 6 месяцев")
    @Schema(description = "Срок кредитования", example = "6")
    private Integer term;

    @NotNull(message = "Имя обязательно")
    @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Имя должно содержать от 2 до 30 символов")
    @Schema(description = "Имя заемщика", example = "Petr")
    private String firstName;

    @NotNull(message = "Фамилия обязательна")
    @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Фамилия должна содержать от 2 до 30 символов")
    @Schema(description = "Фамилия заемщика", example = "Ivanov")
    private String lastName;

    @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Отчество должно содержать от 2 до 30 символов")
    @Schema(description = "Отчество заемщика", example = "Alekseev")
    private String middleName;

    @NotNull(message = "Email обязателен")
    @Pattern(regexp = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$",
            message = "Неверный формат email")
    @Schema(description = "Электронная почта", example = "petr_iv@mail.ru")
    private String email;

    @NotNull(message = "Дата рождения обязательна")
    @Schema(description = "Дата рождения", example = "1996-03-21")
    private LocalDate birthdate;

    @NotNull(message = "Серия паспорта обязательна")
    @Pattern(regexp = "^\\d{4}$", message = "Серия паспорта должна содержать 4 символа")
    @Schema(description = "Серия паспорта", example = "2020")
    private String passportSeries;

    @NotNull(message = "Номер паспорта обязателен")
    @Pattern(regexp = "^\\d{6}$", message = "Номер паспорта должен содержать 6 символов")
    @Schema(description = "Номер паспорта", example = "345267")
    private String passportNumber;
}
