package com.alekseyorlov.vkdump.parameters;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.alekseyorlov.vkdump.parameters.annotation.ValidScope;

public class ParametersValidator implements ConstraintValidator<ValidScope, Parameters> {

    @Override
    public void initialize(ValidScope constraintAnnotation) {
    }

    @Override
    public boolean isValid(Parameters parameters, ConstraintValidatorContext context) {
        ScopeGenerator generator = new ScopeGenerator(parameters);

        return generator.generateScope() != 0;
    }

}
