package com.example.payment;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^(LT|LV|EE)\\d{2}[A-Z0-9]{13,31}$", message = "IBAN must be a valid Baltic country IBAN")
@ReportAsSingleViolation
public @interface BalticIban {
    String message() default "Invalid Baltic IBAN";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
