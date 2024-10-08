package br.com.mandara.impl.processor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

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
import br.com.mandara.exception.QueryProcessorException;
import br.com.mandara.impl.wrapper.StatementWrapperFactory;

public class UpdateQueryProcessor<T> extends AbstractQueryProcessor<T> implements IDMLQuery<T> {

    private final Object entityObject;
    private final IStatementWrapper statementWrapper;
    
    private final Map<EntityField, String> fieldIndexes;
    
    public UpdateQueryProcessor(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata, 
            StatementType statementType,
            Object entityObject) throws SQLException {
        
        super(entityQueryExecutor, entityMetadata, statementType);
        
        this.entityObject = entityObject;
        

        ///////////////////////////////////////////////////////////////////////////////////////

        fieldIndexes = new LinkedHashMap<EntityField, String>();
        
        for (EntityField entityField : entityMetadata.getAllEntityFields()) 
            fieldIndexes.put(entityField, 
                    String.format("%s%s", "p", entityField.getFieldName()));
        
        ///////////////////////////////////////////////////////////////////////////////////////
        
        final String generatedSQL = generateUpdateSQL();
        
        updateSQLQuery(generatedSQL);

        System.out.println(generatedSQL);
        
        final Connection connection = entityQueryExecutor.getConnection();
        
        statementWrapper = StatementWrapperFactory.get(connection, generatedSQL, statementType);        
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
        try {
            for (Map.Entry<EntityField, String> fieldEntry : fieldIndexes.entrySet()) {
                EntityField entityField = fieldEntry.getKey();
                
                if (!entityField.isPrimaryKey()) {
                    Object entityFieldValue = fieldEntry.getKey().getWrapperedField().get(entityObject);
                    
                    System.out.println("index: " + fieldEntry.getValue() + " name: " + 
                            fieldEntry.getKey().getFieldName() + " accessedValue: " + entityFieldValue);
                    
                    ((IParameterized)statementWrapper).setParameterObject(fieldEntry.getValue(), entityFieldValue);
                    continue;
                }

                if (entityField.getFieldType() == EntityFieldType.COMPOUND) {
                    Object primaryKeyCompoundObejct = entityMetadata.getEntityIdentification()
                            .getCompoundedIdentificationField().get(entityObject);
                    
                    Object entityFieldValue = entityField.getWrapperedField().get(primaryKeyCompoundObejct);

                    System.out.println("index: " + fieldEntry.getValue() + " name: " + 
                            entityField.getFieldName() + " accessedValue: " + entityFieldValue);
                    
                    ((IParameterized)statementWrapper).setParameterObject(fieldEntry.getValue(), entityFieldValue);
                
                } else {
                    Object entityFieldValue = fieldEntry.getKey().getWrapperedField().get(entityObject);
                    System.out.println("index: " + fieldEntry.getValue() + " name: " + 
                            entityField.getFieldName() + " accessedValue: " + entityFieldValue);
                    
                    ((IParameterized)statementWrapper).setParameterObject(fieldEntry.getValue(), entityFieldValue);
                }
                
            }

            statementWrapper.executeStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String generateUpdateSQL() {
        
        StringBuilder sql = new StringBuilder();
        
        sql.append("UPDATE ");
        
        sql.append(entityMetadata.getSchemaTable());
        
        sql.append(" SET ");
        
        StringJoiner joiner = new StringJoiner(", ");
        StringJoiner joiner1 = new StringJoiner(" AND ");
               
        for (Map.Entry<EntityField, String> entityField : fieldIndexes.entrySet())
        {
            if (entityField.getKey().isPrimaryKey())
                joiner1.add(String.format("(%s = :%s)", entityField.getKey().getFieldName(), entityField.getValue()));
            else
                joiner.add(String.format("%s = :%s", entityField.getKey().getFieldName(), entityField.getValue()));
        }
        
        sql.append(joiner.toString());

        sql.append(" WHERE ");

        sql.append(joiner1);
        
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
