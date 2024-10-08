package br.com.mandara.util;

import br.com.mandara.entity.EntityField;

public final class Utilities {

    private static final String PARAMETER_PREFIX = "param";
    
    public static String createParam(EntityField entityField, boolean truncateColon) {
        return String.format("%s%s%s", 
                (truncateColon ? "" : ":"), 
                PARAMETER_PREFIX, 
                entityField.getFieldName());
    }
    
    public static void closeQuietly(AutoCloseable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (Exception e) {/* ignore */}
    }
    
    public static boolean isStrFullyEmpty(String emptyStr) {
        return (emptyStr == null) || (emptyStr.trim().isEmpty());
    }
    
    public static String coalesceBlank(String... strings) {
        for (String string : strings) 
            if (!isStrFullyEmpty(string)) 
                return string;
        return null;
    }
    
}
