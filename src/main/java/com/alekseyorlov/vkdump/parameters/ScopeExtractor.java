package com.alekseyorlov.vkdump.parameters;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import com.alekseyorlov.vkdump.parameters.annotation.Scope;

public class ScopeExtractor {

    private ApplicationParameters parameters;

    public ScopeExtractor(ApplicationParameters parameters) {
        this.parameters = parameters;
    }

    public Set<String> extract() {
        Set<String> scopes = new HashSet<>();

        Field[] fields = parameters.getClass().getDeclaredFields();
        for (Field field : fields) {
            Scope scopeAnnotation = field.getAnnotation(Scope.class);
            if (scopeAnnotation != null) {
                if (!field.getType().equals(Boolean.class)) {
                    throw new ScopeWrongTypeException();
                }
                field.setAccessible(true);
                try {
                    Boolean value = (Boolean) field.get(parameters);
                    if (value != null && value.equals(Boolean.TRUE)) {
                        scopes.add(scopeAnnotation.value());
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return scopes;
    }
}
