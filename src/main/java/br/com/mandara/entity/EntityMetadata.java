package br.com.mandara.entity;

import static br.com.mandara.exception.StaticEntityException.IdOutOfBoundsStateException;
import static br.com.mandara.exception.StaticEntityException.noPkDefinedStateException;
import static br.com.mandara.exception.StaticEntityException.unpermittedTypeStateException;
import static br.com.mandara.util.Utilities.coalesceBlank;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.mandara.annotation.Column;
import br.com.mandara.annotation.Compound;
import br.com.mandara.annotation.Entity;
import br.com.mandara.annotation.Id;
import br.com.mandara.core.Strategy;
import br.com.mandara.exception.EntityCreationException;
import br.com.mandara.util.Checks;
import br.com.mandara.util.Reflections;
import br.com.mandara.util.Utilities;

public final class EntityMetadata {
    
    public static final Object[] EMPTY_ARGS = new Object[0];
    
    public final class EntityIdentification 
    {
        private final EntityField[] entityFields;
        private final String rootFieldName;

        private EntityFieldType entityFieldType;
        private Class<?> compoundClassType;
        private Field compoundedIdentificationField;
        private Constructor<?> compoundDefaultConstructor;

        private Strategy strategy = Strategy.NONE;
        
        public EntityIdentification(Field entityIdField) {
            final Column columnAnnotation = entityIdField.getDeclaredAnnotation(Column.class);
            final Class<?> identificationClassType = entityIdField.getType();

            this.rootFieldName = getDefinedColumnName(entityIdField, columnAnnotation);
            
            List<EntityField> entityFieldsList;
            
            if (!Reflections.isPrimitiveOrWrapperType(identificationClassType)) {
                compoundedIdentificationField = entityIdField;
                entityFieldsList = extractCompoundFields(identificationClassType);
                
            } else
                entityFieldsList = 
                    extractSingleField(entityIdField, columnAnnotation);
               
            entityFields = entityFieldsList.toArray(
                    new EntityField[entityFieldsList.size()]);
        }

        private List<EntityField> extractCompoundFields(Class<?> identificationClassType) {
            this.compoundClassType = identificationClassType;
            
            this.compoundDefaultConstructor = Reflections
                    .extractDefaultConstrutor(identificationClassType);
            
            List<EntityField> entityFields = new ArrayList<EntityField>();
            
            if (!identificationClassType.isAnnotationPresent(Compound.class))
                throw new NullPointerException(Compound.class.getName() + " is not present in " 
                        + identificationClassType.getName());

            for (Field compoundField : identificationClassType.getDeclaredFields()) {
                final Column columnAnnotation = compoundField.getDeclaredAnnotation(Column.class);
                String compoundFieldName = getDefinedColumnName(compoundField, columnAnnotation);
                entityFields.add(new EntityField(
                        compoundField, 
                        (entityFieldType = EntityFieldType.COMPOUND),  
                        compoundFieldName));
            }

            if (entityFields.isEmpty())
                throw noPkDefinedStateException(identificationClassType);
            
            return entityFields;
        }

        private List<EntityField> extractSingleField(Field entityIdField, Column columnAnnotation) {
            this.strategy = entityIdField
                    .getDeclaredAnnotation(Id.class)
                    .strategy();
            return Arrays.asList(new EntityField(
                    entityIdField, 
                    (entityFieldType = EntityFieldType.SINGLE), 
                    getDefinedColumnName(entityIdField, columnAnnotation)));
        }
        
        public Object createCompoundedIdentification() {
            try {
                return compoundDefaultConstructor.newInstance(Reflections.EMPTY_ARGS);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
            }
            return null;
        }

        public Field getCompoundedIdentificationField() {
            return compoundedIdentificationField;
        }
        
        public boolean isIdentificationCompounded() {
            return compoundClassType != null;
        }
        
        public EntityField[] getIdentificationEntityFields() {
            return entityFields;
        }

        public EntityFieldType getEntityFieldType() {
            return entityFieldType;
        }

        public Class<?> getCompoundClassType() {
            return compoundClassType;
        }

        public Constructor<?> getCompoundDefaultConstructor() {
            return compoundDefaultConstructor;
        }

        public Strategy getStrategy() {
            return strategy;
        }

        public String getRootFieldName() {
            return rootFieldName;
        }
        
    }
    
    private final EntityField[] commonEntityFields;
    private final EntityField[] allEntityFields;
    private final Class<?> entityClassType;
    private final Constructor<?> entityDefaultConstructor;

    private final String schemaName;
    private final String tableName;
    
    private EntityIdentification entityIdentification;

    
    public EntityMetadata(Class<?> entityClassType) {
        Checks.state((entityClassType == null), 
                "EntityMetadata#entityClassType must not be null.");
        
        this.entityClassType = entityClassType;
        
        final Entity entityAnnotation = 
                entityClassType.getDeclaredAnnotation(Entity.class);

        Checks.state((entityAnnotation == null), Entity.class.getName() + 
                " is not present in " + entityClassType.getName());
        
        this.entityDefaultConstructor = extractDefaultConstructor();
        
        this.schemaName = entityAnnotation.schemaName();
        this.tableName = coalesceBlank(entityAnnotation.tableName(), 
                entityClassType.getSimpleName());
        
        List<EntityField> entityFieldsList = new ArrayList<EntityField>();
        
        for (Field field : entityClassType.getDeclaredFields()) {
            final Column columnAnnotation = field.getDeclaredAnnotation(Column.class);
            final String definedColumnName = getDefinedColumnName(field, columnAnnotation);
            
            if (field.isAnnotationPresent(Id.class)) {
                if (entityIdentification == null) {
                    entityIdentification = new EntityIdentification(field);
                    continue;
                }
                throw IdOutOfBoundsStateException(entityClassType);
            }

            if (!Reflections.isPrimitiveOrWrapperType(field.getType()))
                throw unpermittedTypeStateException(field);
            
            entityFieldsList.add(new EntityField(
                    field, 
                    EntityFieldType.NONE, 
                    definedColumnName));
        }
        
        commonEntityFields = entityFieldsList.toArray(
                new EntityField[entityFieldsList.size()]);

        List<EntityField> allEntityFieldsList = new ArrayList<EntityField>();
        allEntityFieldsList.addAll(Arrays.asList(commonEntityFields));
        allEntityFieldsList.addAll(Arrays.asList(entityIdentification.getIdentificationEntityFields()));
        allEntityFields = allEntityFieldsList.toArray(new EntityField[0]);
        
  }

    private String getDefinedColumnName(Field field, Column columnAnnotation) {
        return (columnAnnotation != null)
                ? coalesceBlank(columnAnnotation.columnName(), field.getName())
                : field.getName();
    }
    
   
    public String getSchemaTable(String specifiedSchema) {
        return (!Utilities.isStrFullyEmpty(specifiedSchema)) 
                ? String.format("%s.%s", specifiedSchema, tableName) 
                : tableName;
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

    @SuppressWarnings("unchecked")
    public <T> T newInstance() throws EntityCreationException {
        return (T) newInstanceInternal();
    }
    
    public int getAllFieldCount() {
        return allEntityFields.length;
    }
    
    private Constructor<?> extractDefaultConstructor() {
        return Reflections.extractDefaultConstrutor(entityClassType);
    }

    public EntityField[] getAllEntityFields() {
        return allEntityFields;
    }
    
    public EntityField[] getCommonEntityFields() {
        return commonEntityFields;
    }
    
    public String getSchemaTable() {
        return getSchemaTable(schemaName);
    }
    
    public Constructor<?> getDefaultConstructor() {
        return entityDefaultConstructor;
    }
    
    public EntityIdentification getEntityIdentification() {
        return entityIdentification;
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
