package com.jsdroid.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MethodDoc {
    String value() default "";

    String classify() default "默认";
}
