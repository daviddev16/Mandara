package br.com.easyorm.entity;

import java.lang.reflect.Field;

import br.com.easyorm.util.Checks;

public class EntityField {

    private final String fieldName;
    private final Field wrapperedField;
    private final EntityFieldType fieldType;
    
    public EntityField(Field field, String fieldName) 
    {
        this(field, EntityFieldType.NONE, fieldName);
    }
    
    public EntityField(Field field, EntityFieldType fieldType, String fieldName) 
    {
        Checks.state((field == null), 
                "EntityField#field must not be null.");
        
        Checks.state((fieldType == null), 
                "EntityField#fieldType must not be null.");
        
        Checks.state((fieldName == null), 
                "EntityField#fieldName must not be null.");

        this.wrapperedField = field;
        this.fieldType = fieldType;
        this.fieldName = fieldName;

        changeFieldAccessibility(field);
    }
    
    private void changeFieldAccessibility(Field field)
    {
        field.setAccessible(true);
    }

    public boolean isPrimaryKey() {
        return (fieldType != EntityFieldType.NONE);
    }
    
    public Field getWrapperedField() {
        return wrapperedField;
    }
    
    public EntityFieldType getFieldType() {
        return fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return "EntityField [fieldName=" + fieldName + ", wrapperedField=" + wrapperedField + ", fieldType=" + fieldType
                + "]";
    }

}
