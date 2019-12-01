package net.ali4j.restlimit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestLimit {

    public enum Type{IP, HEADER}

    Type type() default Type.HEADER;
    String value() default "token";
    int max() default 60;
    String duration() default "60s";
}
