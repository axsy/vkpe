package com.alekseyorlov.vkdump.parameters.annotation;

import java.lang.annotation.Target;

import com.alekseyorlov.vkdump.authorization.AuthorizationScope;

import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;

@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface Scope {

    AuthorizationScope value();

}
