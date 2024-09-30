package br.com.easyorm.impl.deserializer;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import br.com.easyorm.core.IEntityDeserializer;
import br.com.easyorm.entity.EntityField;
import br.com.easyorm.entity.EntityMetadata;
import br.com.easyorm.exception.DeserializationException;
import br.com.easyorm.exception.EntityCreationException;
import br.com.easyorm.impl.context.QueryProcessContext;

public final class JavaTypeEntityDeserializer implements IEntityDeserializer {

    private Map<UUID, EntityField[]> cachedQueryFields;

    private boolean oldImpl = false;
    
    public JavaTypeEntityDeserializer() {
        cachedQueryFields = new ConcurrentHashMap<UUID, EntityField[]>();
    }
    
    @Override
    public <T> T deserialize(QueryProcessContext queryProcessContext, int rowCount, EntityMetadata entityMetadata) 
            throws DeserializationException, EntityCreationException {
        
        if (oldImpl)
            return deserializeImplVersion2(queryProcessContext, rowCount, entityMetadata);
        else
            return deserializeImplVersion1(queryProcessContext, rowCount, entityMetadata);
    
    }
    
    /* NOVA IMPLEMENTAÇÃO */
    public <T> T deserializeImplVersion1(QueryProcessContext queryProcessContext, int rowCount, EntityMetadata entityMetadata) 
            throws DeserializationException, EntityCreationException {

        final T deserializedEntity = entityMetadata.newInstance();
        final EntityField[] entityFieldArray = entityMetadata.getShortcutCaching().getEntityFields();
        final ResultSetMetaData rsMetaData = queryProcessContext.getResultSetMetaData();
        
        try {
            EntityField[] serializableEntityFields = cachedQueryFields.get(queryProcessContext.getQueryId());
            
            if (serializableEntityFields == null) {
                List<EntityField> tempAvaiableFields = new ArrayList<EntityField>();
                
                for (EntityField entityField : entityFieldArray) {
                    for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                        if (entityField.getFieldName().toLowerCase().equals(rsMetaData.getColumnName(i).toLowerCase())) {
                            tempAvaiableFields.add(entityField);
                            break;
                        }
                    }
                }    
                serializableEntityFields = tempAvaiableFields.toArray(new EntityField[tempAvaiableFields.size()]);
                cachedQueryFields.put(queryProcessContext.getQueryId(), serializableEntityFields);
            }
            
            for (EntityField entityField : serializableEntityFields) {
                Object valueOfColumn = queryProcessContext.getResultSet().getObject(entityField.getFieldName());
                entityField.getWrapperedField().set(deserializedEntity, valueOfColumn);
            }            
            
        } catch (Exception e) {
            throw new DeserializationException("Failed to deserialize entity " + 
                    entityMetadata.getEntityClassType().getName(), e);
        }

        return deserializedEntity;
    }
    
    /* IMPLEMENTAÇÃO ANTIGA */
    public <T> T deserializeImplVersion2(QueryProcessContext queryProcessContext, int rowCount, EntityMetadata entityMetadata) 
            throws DeserializationException, EntityCreationException {
        
        final T deserializedEntity = entityMetadata.newInstance();
        final EntityField[] entityFieldArray = entityMetadata.getShortcutCaching().getEntityFields();
        final ResultSetMetaData rsMetaData = queryProcessContext.getResultSetMetaData();
        
        try {
            for (EntityField entityField : entityFieldArray) {
                for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                    if (entityField.getFieldName().toLowerCase().equals(rsMetaData.getColumnName(i).toLowerCase())) {
                        Object valueOfColumn = queryProcessContext.getResultSet().getObject(entityField.getFieldName());
                        entityField.getWrapperedField().set(deserializedEntity, valueOfColumn);
                        break;
                    }
                }
            }    
            
        } catch (Exception e) {
            throw new DeserializationException("Failed to deserialize entity " + 
                    entityMetadata.getEntityClassType().getName(), e);
        }

        return deserializedEntity;
    }

    @Override
    public void clearQueryCaching(UUID queryId) {
        cachedQueryFields.remove(queryId);
    }
    
}
