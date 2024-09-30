package br.com.easyorm.entity;

import java.lang.reflect.Field;

import br.com.easyorm.core.Strategy;

public final class PkEntityField extends EntityField {

    private final Strategy strategy;
    
    public PkEntityField(
            Field field, 
            EntityFieldType fieldType, 
            String fieldName, 
            Strategy strategy) 
    {
        super(field, fieldType, fieldName);
        this.strategy = strategy;
    }
    
    public Strategy getStrategy() {
        return strategy;
    }

    @Override
    public String toString() {
        return "PkEntityField [strategy=" + strategy + ", isPrimaryKey()=" + isPrimaryKey() + ", getWrapperedField()="
                + getWrapperedField() + ", getFieldType()=" + getFieldType() + ", getFieldName()=" + getFieldName()
                + "]";
    }
    
}
