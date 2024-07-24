package com.example.demo.validation;

import com.example.demo.validation.constraints.ValidDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class DecimalValidator implements ConstraintValidator<ValidDecimal, BigDecimal> {
    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int decPoint = 0;
        String[] parts = String.valueOf(value).split("\\.");
        if (parts.length == 2) {
            return parts[1].length() <= decPoint;
        }
        return true;
    }
}
