package dev.ngb.base_stack.application.event.annonation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Topic {
    String value();
}
