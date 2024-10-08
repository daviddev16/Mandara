package br.com.mandara.impl.processor;

import java.sql.Connection;
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
import br.com.mandara.core.queries.IDMLQuery;
import br.com.mandara.entity.EntityField;
import br.com.mandara.entity.EntityFieldType;
import br.com.mandara.entity.EntityMetadata;
import br.com.mandara.entity.EntityMetadata.EntityIdentification;
import br.com.mandara.exception.QueryProcessorException;
import br.com.mandara.impl.wrapper.StatementWrapperFactory;

public @AutoGenerateSQL class DeleteQueryProcessor<T> 
    extends AbstractQueryProcessor<T> 
    implements IDMLQuery<T> {

    private final Object entityObject;
    private final IStatementWrapper statementWrapper;
    
    private final Map<EntityField, Integer> primaryKeyfieldIndexes;
    
    public DeleteQueryProcessor(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata, 
            StatementType statementType,
            Object entityObject) throws SQLException {
        
        super(entityQueryExecutor, entityMetadata, statementType);
        
        this.entityObject = entityObject;
        
        primaryKeyfieldIndexes = new LinkedHashMap<EntityField, Integer>();
        cacheFieldIndexes();
        
        final String generatedSQL = generateDeleteSQL(primaryKeyfieldIndexes.keySet());
        
        updateSQLQuery(generatedSQL);

        System.out.println(generatedSQL);
        
        final Connection connection = entityQueryExecutor.getConnection();
        
        statementWrapper = StatementWrapperFactory.get(connection, generatedSQL, statementType);        
    }
    
    private void cacheFieldIndexes() {
        int j = 1;
        EntityIdentification entityIdentification = entityMetadata.getEntityIdentification();
        for (int i = 0; i < entityIdentification.getIdentificationEntityFields().length; i++) {
            EntityField entityField = entityIdentification.getIdentificationEntityFields()[i];
            primaryKeyfieldIndexes.put(entityField, j++);
        }
    }
    

    @Override
    public void execute() throws QueryProcessorException {
        processQuery();
    }

    @Override
    public T executeReturning() throws QueryProcessorException {
        return null;
    }
    
    @Override
    public void processQuery() throws QueryProcessorException {
        
        for (Map.Entry<EntityField, Integer> fieldEntry : primaryKeyfieldIndexes.entrySet()){
            try {
                EntityField entityField = fieldEntry.getKey();

                if (entityField.getFieldType() == EntityFieldType.COMPOUND) {

                    Object primaryKeyCompoundObejct = entityMetadata.getEntityIdentification()
                            .getCompoundedIdentificationField().get(entityObject);

                    Object entityFieldValue = entityField.getWrapperedField().get(primaryKeyCompoundObejct);
                    ((IParameterized)statementWrapper).setParameterObject(fieldEntry.getValue(), entityFieldValue);

                } else if (entityField.getFieldType() == EntityFieldType.SINGLE) {
                    Object entityFieldValue = entityField.getWrapperedField().get(entityObject);
                    ((IParameterized)statementWrapper).setParameterObject(fieldEntry.getValue(), entityFieldValue);
                }

            } catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            statementWrapper.executeStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            primaryKeyfieldIndexes.clear();
        }
    }
    
    public String generateDeleteSQL(Collection<EntityField> entityFields) {
        StringBuilder sql = new StringBuilder();
        
        sql.append("DELETE FROM ");
        
        sql.append(entityMetadata.getSchemaTable());
        
        
        StringJoiner joiner = new StringJoiner(" AND ");
        
        for (EntityField entityField : entityFields)
        {
            if (entityField.isPrimaryKey())
            joiner.add(String.format("(%s = %s)", entityField.getFieldName(), "?"));
        }

        sql.append(" WHERE ");
        
        sql.append(joiner.toString());

        sql.append(";");
        
        return sql.toString();
    }

    public IStatementWrapper getStatementWrapper() {
        return statementWrapper;
    }

    @Override
    public IParameterized getParameterized() {
        return statementWrapper;
    }

    @Override
    public T executeReturning(IEntityDeserializer deserializer) throws QueryProcessorException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
