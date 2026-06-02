package com.neoproject.deal.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AgeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {

    String message() default "Возраст должен быть больше 18";

    int min() default 18;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
