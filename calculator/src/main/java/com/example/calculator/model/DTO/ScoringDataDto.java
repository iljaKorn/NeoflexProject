package com.example.calculator.model.DTO;

import com.example.calculator.model.DTO.enums.Gender;
import com.example.calculator.model.DTO.enums.MaritalStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScoringDataDto {

    @NotNull(message = "Сумма кредита обязательна")
    @DecimalMin(value = "20000.0", message = "Сумма кредита должна быть не меньше 20000")
    private BigDecimal amount;

    @NotNull(message = "Срок кредита обязателен")
    @Min(value = 6, message = "Срок кредита должен быть не меньше 6 месяцев")
    private Integer term;

    @NotNull(message = "Имя обязательно")
    @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Имя должно содержать от 2 до 30 символов")
    private String firstName;

    @NotNull(message = "Фамилия обязательна")
    @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Фамилия должна содержать от 2 до 30 символов")
    private String lastName;

    @Pattern(regexp = "^[A-Za-z]{2,30}$", message = "Отчество должно содержать от 2 до 30 символов")
    private String middleName;

    @NotNull(message = "Пол обязателен")
    private Gender gender;

    @NotNull(message = "Дата рождения обязательна")
    private LocalDate birthdate;

    @NotNull(message = "Серия паспорта обязательна")
    @Pattern(regexp = "^\\d{4}$", message = "Серия паспорта должна содержать 4 символа")
    private String passportSeries;

    @NotNull(message = "Номер паспорта обязателен")
    @Pattern(regexp = "^\\d{6}$", message = "Номер паспорта должен содержать 6 символов")
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;

    @NotNull(message = "Семейное положение обязательно")
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;

    @NotNull(message = "Сведения о работе обязательны")
    private EmploymentDto employment;
    private String accountNumber;

    @NotNull(message = "Сведения о страховке обязательны")
    private Boolean isInsuranceEnabled;

    @NotNull(message = "Сведения о том, является ли человек клиентом банка, обязательны")
    private Boolean isSalaryClient;
}
