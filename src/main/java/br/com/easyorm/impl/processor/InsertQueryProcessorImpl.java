package br.com.easyorm.impl.processor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import br.com.easyorm.core.AbstractQueryProcessor;
import br.com.easyorm.core.IEntityQueryExecutor;
import br.com.easyorm.core.IParameterized;
import br.com.easyorm.core.IStatementWrapper;
import br.com.easyorm.core.StatementType;
import br.com.easyorm.core.Strategy;
import br.com.easyorm.core.queries.IDMLQuery;
import br.com.easyorm.entity.EntityField;
import br.com.easyorm.entity.EntityMetadata;
import br.com.easyorm.entity.PkEntityField;
import br.com.easyorm.exception.QueryProcessorException;
import br.com.easyorm.impl.wrapper.StatementWrapperFactory;

public class InsertQueryProcessorImpl<T> extends AbstractQueryProcessor<T> implements IDMLQuery<T> {

    private final Object entityObject;
    private final IStatementWrapper statementWrapper;
    
    private final Map<EntityField, Integer> fieldIndexes;
    
    public InsertQueryProcessorImpl(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata, 
            StatementType statementType,
            Object entityObject) throws SQLException {
        
        super(entityQueryExecutor, entityMetadata, statementType);
        
        this.entityObject = entityObject;
        
        fieldIndexes = new LinkedHashMap<EntityField, Integer>();
        cacheFieldIndexes();
        
        final String generatedSQL = entityQueryExecutor.getDialect()
                .generateInsertSQL(entityMetadata, null /* schemaName */, fieldIndexes.keySet());
        
        updateSQLQuery(generatedSQL);

        System.out.println(generatedSQL);
        
        final Connection connection = entityQueryExecutor.getConnection();
        
        statementWrapper = StatementWrapperFactory.get(connection, generatedSQL, statementType);        
    }
    
    private void cacheFieldIndexes() {
        int j = 1;
        for (int i = 0; i < entityMetadata.getShortcutCaching().getEntityFields().length; i++) {
            EntityField entityField = entityMetadata.getShortcutCaching().getEntityFields()[i];
            if (shouldSkipField(entityField)) 
                continue;
            fieldIndexes.put(entityField, j++);
        }
    }
    
    private boolean shouldSkipField(EntityField entityField) {
        return (entityField instanceof PkEntityField) && 
                (((PkEntityField)entityField).getStrategy() == Strategy.AUTO);
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
        for (Map.Entry<EntityField, Integer> fieldEntry : fieldIndexes.entrySet()){
            try {
                Object entityFieldValue = fieldEntry.getKey().getWrapperedField().get(entityObject);
                ((IParameterized)statementWrapper).setParameterObject(fieldEntry.getValue(), entityFieldValue);
            } catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            statementWrapper.executeStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            fieldIndexes.clear();
        }
    }

    public IStatementWrapper getStatementWrapper() {
        return statementWrapper;
    }

    @Override
    public IParameterized getParameterized() {
        return statementWrapper;
    }
    
}
