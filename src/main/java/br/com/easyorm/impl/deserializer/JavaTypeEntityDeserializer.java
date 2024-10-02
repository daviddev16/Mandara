package br.com.easyorm.impl.deserializer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import br.com.easyorm.core.ICacheableEntityDeserializer;
import br.com.easyorm.core.Stopwatch;
import br.com.easyorm.entity.EntityField;
import br.com.easyorm.entity.EntityMetadata;
import br.com.easyorm.exception.DeserializationException;
import br.com.easyorm.exception.EntityCreationException;
import br.com.easyorm.impl.context.QueryProcessContext;
import br.com.easyorm.util.Reflections;

/**
 * Entity field query cacheable EntityDeserializer
 **/
public final class JavaTypeEntityDeserializer implements ICacheableEntityDeserializer {

    public static final class ResultSetColumnWrapper {

        private final int columnIndex;
        private final EntityField entityField;
        
        public ResultSetColumnWrapper(int columnIndex, EntityField entityField) {
            this.columnIndex = columnIndex;
            this.entityField = entityField;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        public EntityField getEntityField() {
            return entityField;
        }
    }
    
    private final Map<UUID, ResultSetColumnWrapper[]> cachedQueryColumnWrappers;
    
    private boolean oldImpl = false;
    
    public JavaTypeEntityDeserializer() 
    {
        // HashMap é um pouco mais rapido mas o ConcurrentHashMap é necessário aqui
        cachedQueryColumnWrappers = new ConcurrentHashMap<UUID, ResultSetColumnWrapper[]>();
    }
    
    @Override
    public <T> T deserialize(
            QueryProcessContext queryProcessContext, 
            int rowCount, EntityMetadata entityMetadata) 
                    throws DeserializationException, EntityCreationException {
        if (oldImpl)
            return deserializeImplVersion2(queryProcessContext, rowCount, entityMetadata);
        else
            return deserializeEntityImplv3(queryProcessContext, rowCount, entityMetadata);
    }
    
    /* NOVA IMPLEMENTAÇÃO */
    public <T> T deserializeEntityImplv3(
            QueryProcessContext queryProcessContext, int rowCount, EntityMetadata entityMetadata) 
            throws DeserializationException, EntityCreationException {

        Stopwatch.beginStopwatch("PreparandoDados");
        
        final T deserializedEntity = entityMetadata.newInstance();
        final ResultSet resultSet  = queryProcessContext.getResultSet();
        
        final ResultSetMetaData rsMetaData = queryProcessContext.getResultSetMetaData();
        
        final boolean hasCompoundKey = entityMetadata.getCompoundKeyClassType() != null;
        
        final Object compoundKeyObject = (hasCompoundKey) 
                    ? createCompoundKeyObject(entityMetadata)
                    : null;
        
        Stopwatch.endStopwatch("PreparandoDados");
        
        Stopwatch.beginStopwatch("ColetandoCachingColumnWrapper");
        
        ResultSetColumnWrapper[] cachedColumnWrappers = 
                cachedQueryColumnWrappers.get(queryProcessContext.getQueryId());
        
        try {
            if (cachedColumnWrappers == null) {
                cachedColumnWrappers = getColumnWrappersFromQuery(entityMetadata, rsMetaData);
                cachedQueryColumnWrappers.put(
                        queryProcessContext.getQueryId(), cachedColumnWrappers);
            }

            Stopwatch.endStopwatch("ColetandoCachingColumnWrapper");
            
            Stopwatch.beginStopwatch("InicioFieldInjection");
            
            for (ResultSetColumnWrapper chColumnWrapper : cachedColumnWrappers) {

                final Field wrapperedField    = chColumnWrapper.getEntityField().getWrapperedField();
                final Object fieldValue       = resultSet.getObject(chColumnWrapper.getColumnIndex());
                final EntityField entityField = chColumnWrapper.getEntityField();

                final Object targetObject = (hasCompoundKey && entityField.isPrimaryKey())

                        // Se for chave composta e esse EntityField for chave primária
                        // Injetar valor na field do objeto da classe da chave primária
                        ? compoundKeyObject 

                        // Se for apenas uma field e não for chave composta
                        // Injetar valor na field do objeto da classe da entidade
                        : deserializedEntity;

                performFieldInjection(wrapperedField, fieldValue, targetObject);
            }

            Stopwatch.endStopwatch("InicioFieldInjection");

            Stopwatch.beginStopwatch("Miscs");
            
            // Injetar valor da chave primária composta na entidade deserializada

            if (hasCompoundKey) {
                final Field compoundKeyField = entityMetadata
                        .getShortcutCaching().getCompoundKeyWrapperedField();
                
                performFieldInjection(compoundKeyField, compoundKeyObject, deserializedEntity);
            }

            Stopwatch.endStopwatch("Miscs");
            
        } catch (Exception e) {
            throw new DeserializationException("Failed to deserialize entity " + 
                    entityMetadata.getEntityClassType().getName(), e);
        }
        
        Stopwatch.summary();
        
        return deserializedEntity;
    }
    
    private Object createCompoundKeyObject(EntityMetadata entityMetadata) throws EntityCreationException {
        try {
            return entityMetadata.getCompoundKeyDefaultConstructor().newInstance(Reflections.EMPTY_ARGS);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
        }
        return null;
    }
    
    private void performFieldInjection(Field wrapperedField, Object fieldValue, Object targetObject) {
        try {
            wrapperedField.set(targetObject, fieldValue);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    private ResultSetColumnWrapper[] getColumnWrappersFromQuery(
            EntityMetadata entityMetadata, ResultSetMetaData resultSetMetaData) throws SQLException {
        
        final List<ResultSetColumnWrapper> wrappers = 
                new ArrayList<ResultSetColumnWrapper>(entityMetadata.getFieldCount());
        
        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++)
            for (EntityField entityField : entityMetadata.getShortcutCaching().getEntityFields())
                if (resultSetMetaData.getColumnLabel(i).equalsIgnoreCase(entityField.getFieldName()))
                    wrappers.add(new ResultSetColumnWrapper(i, entityField));
        
        return wrappers.toArray(new ResultSetColumnWrapper[0]);
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
        cachedQueryColumnWrappers.remove(queryId);
    }
    
}
