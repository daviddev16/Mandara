package br.com.mandara.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public final class Reflections {

    public static final Object[] EMPTY_ARGS = new Object[0];
    
    public static String fieldToString(Field field) {
        return field.getType().getName() + "@" + field.getName();
    }
    
    public static Constructor<?> extractDefaultConstrutor(Class<?> classType) {
        Constructor<?> defaultConstructor = null;
        
        for (Constructor<?> constructor : classType.getDeclaredConstructors())
            if (constructor.getParameterCount() == 0) {
                defaultConstructor = constructor;
                break;
            }
        
        Checks.state((defaultConstructor == null), "Could not found a "
                + "default constructor for " + classType.getName());
        
        return defaultConstructor;
    }
    
    public static boolean isPrimitiveOrWrapperType(Class<?> type) {
        return 
            type.isPrimitive()      ||
            type == Double.class    || 
            type == Float.class     || 
            type == Long.class      ||
            type == Integer.class   || 
            type == Short.class     || 
            type == Character.class ||
            type == Byte.class      || 
            type == Boolean.class   || 
            type == String.class;
    }
    
}
