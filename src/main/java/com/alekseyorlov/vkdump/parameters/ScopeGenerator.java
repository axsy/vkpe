package com.alekseyorlov.vkdump.parameters;

import java.lang.reflect.Field;

import com.alekseyorlov.vkdump.parameters.annotation.Scope;

public class ScopeGenerator {

    private Parameters parameters;

    public ScopeGenerator(Parameters parameters) {
        this.parameters = parameters;
    }

    public int generateScope() {
        int scope = 0;

        Field[] fields = parameters.getClass().getDeclaredFields();
        for (Field field : fields) {
            Scope scopeBitMaskAnnotation = field.getAnnotation(Scope.class);
            if (scopeBitMaskAnnotation != null) {
                if (!field.getType().equals(Boolean.class)) {
                    throw new ScopeWrongTypeException();
                }
                field.setAccessible(true);
                try {
                    Boolean value = (Boolean) field.get(parameters);
                    if (value != null && value.equals(Boolean.TRUE)) {
                        scope += scopeBitMaskAnnotation.mask();
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return scope;
    }
}
