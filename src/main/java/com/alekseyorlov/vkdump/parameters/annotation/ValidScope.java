package com.alekseyorlov.vkdump.parameters.annotation;

import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.alekseyorlov.vkdump.parameters.ParametersValidator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { ParametersValidator.class })
@Documented
public @interface ValidScope {

    String message() default "Wrong media set to be dumped";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
