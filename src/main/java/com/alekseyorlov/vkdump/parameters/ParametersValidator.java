package com.alekseyorlov.vkdump.parameters;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.alekseyorlov.vkdump.parameters.annotation.ValidScope;

public class ParametersValidator implements ConstraintValidator<ValidScope, ApplicationParameters> {

    @Override
    public void initialize(ValidScope constraintAnnotation) {
    }

    @Override
    public boolean isValid(ApplicationParameters parameters, ConstraintValidatorContext context) {
        ScopeExtractor scopeExtractor = new ScopeExtractor(parameters);

        return !scopeExtractor.extract().isEmpty();
    }

}
