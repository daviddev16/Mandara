package br.com.easyorm.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import br.com.easyorm.entity.EntityField;

public final class Utilities {

    private static final String PARAMETER_PREFIX = "param";
    
    public static String createParam(EntityField entityField, boolean truncateColon)
    {
        return String.format("%s%s%s", 
                (truncateColon ? "" : ":"), 
                PARAMETER_PREFIX, 
                entityField.getFieldName());
    }
    
    public static void closeQuietly(AutoCloseable closeable) 
    {
        try {
            if (closeable != null)
                closeable.close();
        } catch (Exception e) {/* ignore */}
    }
    
    public static boolean isStrFullyEmpty(String emptyStr) 
    {
        return (emptyStr == null) || (emptyStr.trim().isEmpty());
    }
    
    public static String coalesceBlank(String... strings)   
    {
        for (String string : strings) 
        {
            if (!isStrFullyEmpty(string)) 
                return string;
        }
        return null;
    }
    
    public static String fieldToString(Field field)
    {
        return field.getType().getName() + "@" + field.getName();
    }
    
    public static Constructor<?> extractDefaultConstrutor(Class<?> classType)
    {
        Constructor<?> defaultConstructor = null;
        
        for (Constructor<?> constructor : classType.getDeclaredConstructors())
        {
            if (constructor.getParameterCount() == 0)
            {
                defaultConstructor = constructor;
                break;
            }
                
        }
        
        Checks.state((defaultConstructor == null), "Could not found a "
                + "default constructor for " + classType.getName());
        
        return defaultConstructor;
    }
    
    public static boolean isAllowedType(Class<?> type) 
    {
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
