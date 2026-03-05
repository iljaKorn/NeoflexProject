package com.example.calculator.model.DTO;

import com.example.calculator.model.DTO.enums.EmploymentStatus;
import com.example.calculator.model.DTO.enums.Position;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmploymentDto {
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
