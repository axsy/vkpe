package com.alekseyorlov.vkdump.parameters.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.alekseyorlov.vkdump.parameters.ApplicationParameters;
import com.alekseyorlov.vkdump.parameters.annotation.Scope;

public final class ScopeUtils {

    public static Collection<String> getActiveScopes(ApplicationParameters parameters) {
        Set<String> scopes = new HashSet<>();

        Field[] fields = parameters.getClass().getDeclaredFields();
        for (Field field : fields) {
            Scope scopeAnnotation = field.getAnnotation(Scope.class);
            if (scopeAnnotation != null) {
                field.setAccessible(true);
                try {
                    Boolean value = (Boolean) field.get(parameters);
                    if (value != null && value.equals(Boolean.TRUE)) {
                        scopes.add(scopeAnnotation.value());
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    
                    // This code is unreachable because we don't operate with any "alien" data
                    // and we know what and when we pass to reflection framework
                    assert false;
                }
            }
        }

        return scopes;
    }
}
