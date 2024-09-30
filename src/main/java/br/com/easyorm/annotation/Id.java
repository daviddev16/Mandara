package br.com.easyorm.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import br.com.easyorm.core.Strategy;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Id {
    
    Strategy strategy() default Strategy.AUTO;
    
}
