package br.com.easyorm.entity;

import java.lang.reflect.Field;

public final class EntityMetadataShortcutCaching {

    private EntityField[] entityFieldsArray;
    private EntityField[] primaryKeysArray;
    
    private Field compoundKeyWrapperedField;
    
    EntityMetadataShortcutCaching() {}
    
    public EntityField[] getEntityFields() {
        return entityFieldsArray;
    }

    void setCompoundKeyWrapperedField(Field compoundKeyWrapperedField) {
        this.compoundKeyWrapperedField = compoundKeyWrapperedField;
    }
    
    public Field getCompoundKeyWrapperedField() {
        return compoundKeyWrapperedField;
    }
    
    void setEntityFields(EntityField[] entityFieldsArray) {
        this.entityFieldsArray = entityFieldsArray;
    }
    
    public EntityField[] getPrimaryKeys() {
        return primaryKeysArray;
    }
    
    void setPrimaryKeys(EntityField[] primaryKeysArray) {
        this.primaryKeysArray = primaryKeysArray;
    }
    
}
