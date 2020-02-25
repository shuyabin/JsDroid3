package com.jsdroid.ipc.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout {
    int value() default 10000;
}
