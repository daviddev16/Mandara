package br.com.mandara.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import br.com.mandara.core.Strategy;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Id {
    
    Strategy strategy() default Strategy.AUTO;
    
}
