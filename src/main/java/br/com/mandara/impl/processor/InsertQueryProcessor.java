package br.com.mandara.impl.processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import br.com.mandara.annotation.internal.AutoGenerateSQL;
import br.com.mandara.core.AbstractQueryProcessor;
import br.com.mandara.core.IEntityDeserializer;
import br.com.mandara.core.IEntityQueryExecutor;
import br.com.mandara.core.IParameterized;
import br.com.mandara.core.IStatementWrapper;
import br.com.mandara.core.StatementType;
import br.com.mandara.core.Strategy;
import br.com.mandara.core.internal.Returning;
import br.com.mandara.core.queries.IDMLQuery;
import br.com.mandara.entity.EntityField;
import br.com.mandara.entity.EntityMetadata;
import br.com.mandara.exception.DeserializationException;
import br.com.mandara.exception.EntityCreationException;
import br.com.mandara.exception.QueryProcessorException;
import br.com.mandara.impl.context.QueryProcessContext;
import br.com.mandara.impl.wrapper.StatementWrapperFactory;

public @AutoGenerateSQL class InsertQueryProcessor<T> 
    extends AbstractQueryProcessor<T> 
    implements IDMLQuery<T> {
    
    private final Object entityObject;
    
    private Map<EntityField, Integer> fieldIndexes;
    
    private T returningEntityObject;

    IStatementWrapper statementWrapper = null;

    public InsertQueryProcessor(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata, 
            StatementType statementType,
            Object entityObject) throws SQLException {
        
        super(entityQueryExecutor, entityMetadata, statementType);
        
        this.entityObject = entityObject;

        
        if (fieldIndexes == null) 
        {
            fieldIndexes = new LinkedHashMap<EntityField, Integer>();

            int j = 1;
            
            for (int i = 0; i < entityMetadata.getAllFieldCount(); i++) {
                EntityField entityField = entityMetadata.getAllEntityFields()[i];
                if (shouldSkipField(entityMetadata, entityField)) 
                    continue;
                fieldIndexes.put(entityField, j++);
            }
        }
        
    }
    
    private boolean shouldSkipField(EntityMetadata entityMetadata, EntityField entityField) {
        return (entityField.isPrimaryKey()) && 
                entityMetadata.getEntityIdentification().getStrategy() == Strategy.AUTO;
    }

    @Override
    public void execute() throws QueryProcessorException {
        executeImpl(Returning.VOID, null);
    }
    
    @Override
    public T executeReturning(IEntityDeserializer deserializer) 
            throws QueryProcessorException {
        
        if (returningEntityObject != null)
            return returningEntityObject;
        
        deserializer = (deserializer == null)
                ? getEntityQueryExecutor().getEntityDeserializer()
                : deserializer;
        
        return (returningEntityObject = 
                executeImpl(Returning.ENTITY, deserializer));
    }
    
    private T executeImpl(Returning returning, IEntityDeserializer deserializer) {
        final Connection connection = getEntityQueryExecutor().getConnection();

        final String generatedSQL = generateInsertSQL(fieldIndexes.keySet(), returning);
        updateSQLQuery(generatedSQL);
        System.out.println(generatedSQL);
        
        try {
            statementWrapper = StatementWrapperFactory.get(connection, generatedSQL, StatementType.PREPARED);
        } catch (SQLException e) {
            e.printStackTrace();
        }    

        try {
            for (Map.Entry<EntityField, Integer> fieldEntry : fieldIndexes.entrySet()){
                try {
                    Object entityFieldValue = fieldEntry.getKey().getWrapperedField().get(entityObject);
                    
                    ((IParameterized)statementWrapper).setParameterObject(fieldEntry.getValue(), entityFieldValue);
                } catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
                    e.printStackTrace();
                }
            }

            if (returning == Returning.ENTITY) {
                final ResultSet resultSet = statementWrapper.executeQuery();
                final QueryProcessContext queryProcessContext = createQueryResultContext(resultSet);

                while (resultSet.next()) 
                {
                    T deserializedEntity = deserializer.deserialize(queryProcessContext, 0, entityMetadata);    
                    return deserializedEntity;
                }
            }
            else if (returning == Returning.VOID) {
                statementWrapper.executeStatement(generatedSQL);
            }

        } catch (SQLException | DeserializationException | EntityCreationException e) {
            e.printStackTrace();
        } finally {
            if (statementWrapper != null)
                statementWrapper.closeQuietly();
        }

        return null;
    }
    
    @Override
    public void processQuery() throws QueryProcessorException {
        throw new UnsupportedOperationException("Not supported. Instead use  "
                + "execute() or executeReturning().");
    }
    
    public String generateInsertSQL(Collection<EntityField> entityFields, Returning returning) {
        StringBuilder sql = new StringBuilder();
        
        sql.append("INSERT INTO ");
        
        sql.append(entityMetadata.getSchemaTable());
        
        sql.append(" (");
        
        StringJoiner joiner = new StringJoiner(", ");
        StringJoiner paramJoiner = new StringJoiner(", ");
        
        for (EntityField entityField : entityFields)
        {
            joiner.add(entityField.getFieldName());
            paramJoiner.add("?");
        }

        sql.append(joiner.toString());
        
        joiner = null;
        
        sql.append(") VALUES (");

        sql.append(paramJoiner.toString());
        
        sql.append(") ");
        
        paramJoiner = null;
        
        if (returning == Returning.ENTITY)
            sql.append("RETURNING *");
            
        sql.append(";");
        
        return sql.toString();
    }

    @Override
    public IParameterized getParameterized() {
        throw new UnsupportedOperationException("Not supported. Instead use  "
                + "execute() or executeReturning().");
    }
    
}
