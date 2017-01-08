package com.alekseyorlov.vkdump.parameters.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.alekseyorlov.vkdump.parameters.ApplicationParameters;
import com.alekseyorlov.vkdump.parameters.annotation.ValidScope;
import com.alekseyorlov.vkdump.parameters.util.ScopeUtils;

public class ApplicationParametersValidator implements ConstraintValidator<ValidScope, ApplicationParameters> {

    @Override
    public void initialize(ValidScope constraintAnnotation) {
    }

    @Override
    public boolean isValid(ApplicationParameters parameters, ConstraintValidatorContext context) {
        
        return !ScopeUtils.getActiveScopes(parameters).isEmpty();
    }

}
