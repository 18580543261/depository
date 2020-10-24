package com.sramar.myapplication.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ABeanFieldDesc {
    EBeanField eField() default EBeanField.STRING;
    EBeanFieldDesc[] desc() default {EBeanFieldDesc.NORMAL};
    //位数，默认100位
    int bit() default 100;
    String value() default "";
}
