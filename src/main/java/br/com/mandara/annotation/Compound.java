package br.com.mandara.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import br.com.mandara.entity.EntityFieldType;

@SuppressWarnings("unused")
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface Compound {}
