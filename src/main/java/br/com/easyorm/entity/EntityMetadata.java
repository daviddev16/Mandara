package br.com.easyorm.entity;

import static br.com.easyorm.exception.StaticEntityException.noFieldDefinedStateException;
import static br.com.easyorm.exception.StaticEntityException.noPkDefinedStateException;
import static br.com.easyorm.exception.StaticEntityException.recursiveIdStateException;
import static br.com.easyorm.exception.StaticEntityException.unpermittedTypeStateException;
import static br.com.easyorm.util.Utilities.coalesceBlank;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.easyorm.annotation.Column;
import br.com.easyorm.annotation.Compound;
import br.com.easyorm.annotation.Entity;
import br.com.easyorm.annotation.Id;
import br.com.easyorm.exception.EntityCreationException;
import br.com.easyorm.util.Checks;
import br.com.easyorm.util.Utilities;

public final class EntityMetadata {
    
    public static final Object[] EMPTY_ARGS = new Object[0];

    private final Class<?> entityClassType;
    
    private final Constructor<?> defaultConstructor;

    private final String schemaName;
    private final String tableName;
    
    private final Map<String, EntityField> entityFieldMapping;
    private final EntityMetadataShortcutCaching entityMetadataShortcutCaching;
    
    public EntityMetadata(Class<?> entityClassType) {
        Checks.state((entityClassType == null), 
                "EntityMetadata#entityClassType must not be null.");
        
        this.entityClassType = entityClassType;
        
        final Entity entityAnnotation = 
                entityClassType.getDeclaredAnnotation(Entity.class);

        Checks.state((entityAnnotation == null), Entity.class.getName() + 
                " is not present in " + entityClassType.getName());
        
        this.defaultConstructor = extractDefaultConstructor();
        
        this.entityFieldMapping = new HashMap<String, EntityField>();
        this.entityMetadataShortcutCaching = new EntityMetadataShortcutCaching();
        
        this.schemaName = entityAnnotation.schemaName();
        
        this.tableName  = coalesceBlank(
                    entityAnnotation.tableName(), 
                    entityClassType.getSimpleName());
        
        extractEntityFields(entityClassType);
        
        // Validar se existe fields definidas e pelo menos uma chave primária
        validateEntityFields();

        // Gerar caching em arrays dos entityFields
        
        generateContiguousEntityFieldsCache();
    }

    private void extractEntityFields(Class<?> extractableClassType){
        extractEntityFields(extractableClassType, true, EntityFieldType.NONE);
    }
    
    private void extractEntityFields(Class<?> extractableClassType, 
            boolean rootEntityClassType, EntityFieldType rootEntityFieldType) {
        
        for (Field field : extractableClassType.getDeclaredFields()){

            final EntityFieldType fieldType = (rootEntityClassType) 
                    ? EntityFieldType.NONE 
                    : rootEntityFieldType;
            
            extractField(field, rootEntityClassType, fieldType);
        }
    }
    
    private void extractField(Field field, 
            boolean rootScan, EntityFieldType rootEntityFieldType) {

        final Class<?> fieldClassType   = field.getType();
        final Column   columnAnnotation = field.getDeclaredAnnotation(Column.class);
        final Id       idAnnotation     = field.getDeclaredAnnotation(Id.class);
        
        final boolean isAnnotatedWithId = idAnnotation != null;
        
        if (!rootScan && isAnnotatedWithId)
            throw recursiveIdStateException(field);
        
        final String definedColumnName = 
                getDefinedColumnName(field, columnAnnotation);
        
        // Se a PK não foi definida nesse Field, apenas registar como um field
        // de tipo EntityFieldType.NONE ou herdar do rootEntityFieldType.
        
        if (!isAnnotatedWithId) {
            EntityFieldType subFieldType = (rootEntityFieldType != null) 
                    ? rootEntityFieldType 
                    : EntityFieldType.NONE;
            
            if (subFieldType == EntityFieldType.NONE) {
                registerEntityField(new EntityField(
                        field, 
                        subFieldType,
                        definedColumnName));
            } else {
                registerEntityField(new PkEntityField(
                        field, 
                        subFieldType,
                        definedColumnName,
                        idAnnotation.strategy()));
            }
            
            return;
        }
        
        // PK composta definida
        
        if (fieldClassType.isAnnotationPresent(Compound.class)) {
            extractEntityFields(fieldClassType, false, EntityFieldType.COMPOUND);
            return;
        }
        
        if (!Utilities.isAllowedType(fieldClassType))
            throw unpermittedTypeStateException(field);

        // PK definida apenas com um primitivo
        
        registerEntityField(new PkEntityField(
                field, 
                EntityFieldType.SINGLE, 
                definedColumnName,
                idAnnotation.strategy()));
    }
    
    private void generateContiguousEntityFieldsCache() {   
        final Collection<EntityField> entityFields = 
                entityFieldMapping.values();
        
        entityMetadataShortcutCaching.setEntityFields(
                entityFields.toArray(new EntityField[entityFields.size()]));
        
        List<EntityField> primaryKeysList = new ArrayList<EntityField>();

        for (EntityField entityField : entityFieldMapping.values()) {
            if (entityField.isPrimaryKey())
                primaryKeysList.add(entityField);
        }
        
        entityMetadataShortcutCaching.setPrimaryKeys(
                primaryKeysList.toArray(new EntityField[primaryKeysList.size()]));
    }
    
    private void validateEntityFields() {
        if (entityFieldMapping.isEmpty())
            throw noFieldDefinedStateException(entityClassType);

        boolean hasPrimaryKey = false;
        
        for (EntityField entityField : entityFieldMapping.values()) {
            if (entityField.isPrimaryKey()) {
                hasPrimaryKey = true;
                break;
            }
        }
        
        if (!hasPrimaryKey)
            throw noPkDefinedStateException(entityClassType);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T newInstance() throws EntityCreationException {
        return (T) newInstanceInternal();
    }
    
    private Object newInstanceInternal() throws EntityCreationException {
        try {
            return getDefaultConstructor().newInstance(EMPTY_ARGS);
        } catch (InstantiationException   | 
                 IllegalAccessException   | 
                 IllegalArgumentException | 
                 InvocationTargetException e) {
            throw new EntityCreationException("Failed to create a"
                    + " new instance of " + entityClassType.getName(), e);
        }
    }

    private String getDefinedColumnName(Field field, Column columnAnnotation) {
        return (columnAnnotation != null)
                ? coalesceBlank(columnAnnotation.columnName(), field.getName())
                : field.getName();
    }
    
    private EntityField registerEntityField(EntityField entityField) {
        return entityFieldMapping.put(entityField.getFieldName(), entityField);
    }
   
    public String getSchemaTable(String specifiedSchema) {
        return (!Utilities.isStrFullyEmpty(specifiedSchema)) 
                ? String.format("%s.%s", specifiedSchema, tableName) 
                : tableName;
    }
     
    private Constructor<?> extractDefaultConstructor() {
        return Utilities.extractDefaultConstrutor(entityClassType);
    }

    public EntityMetadataShortcutCaching getShortcutCaching() {
        return entityMetadataShortcutCaching;
    }
    
    public String getSchemaTable() {
        return getSchemaTable(schemaName);
    }
    
    public Constructor<?> getDefaultConstructor() {
        return defaultConstructor;
    }
    
    public Class<?> getEntityClassType() {
        return entityClassType;
    }

    public String getSchemaName() {
        return schemaName;
    }
    
    public String getTableName() {
        return tableName;
    }

}
