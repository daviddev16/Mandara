package br.com.easyorm.entity;

public final class EntityMetadataShortcutCaching {

    private EntityField[] entityFieldsArray;
    private EntityField[] primaryKeysArray;
    
    EntityMetadataShortcutCaching() {}
    
    public EntityField[] getEntityFields() {
        return entityFieldsArray;
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
