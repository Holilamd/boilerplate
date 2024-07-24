package com.example.demo.validation.constraints;

import com.example.demo.validation.DecimalValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DecimalValidator.class)
public @interface ValidDecimal {
    String message() default "invalid decimal";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
