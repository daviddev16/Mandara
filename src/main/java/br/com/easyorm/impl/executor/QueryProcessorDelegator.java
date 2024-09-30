package br.com.easyorm.impl.executor;

import java.sql.SQLException;

import br.com.easyorm.core.IEntityDeserializer;
import br.com.easyorm.core.IEntityQueryExecutor;
import br.com.easyorm.core.StatementType;
import br.com.easyorm.core.queries.IDMLQuery;
import br.com.easyorm.core.queries.IDQLQuery;
import br.com.easyorm.entity.EntityMetadata;
import br.com.easyorm.impl.processor.InsertQueryProcessorImpl;
import br.com.easyorm.impl.processor.SelectQueryProcessorImpl;

final class QueryProcessorDelegator {

    protected QueryProcessorDelegator() {}
    
    public <T> IDQLQuery<T> delegateSelectQuery(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata, 
            StatementType statementType,
            String sqlQuery, 
            IEntityDeserializer deserializer) throws SQLException 
    {
        return new SelectQueryProcessorImpl<T>(
                entityQueryExecutor, entityMetadata, deserializer, 
                statementType, sqlQuery);
    }

    public <T> IDMLQuery<T> delegateInsertQuery(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata, 
            StatementType statementType,
            Object entityObject) throws SQLException 
    {
        return new InsertQueryProcessorImpl<T>(
                entityQueryExecutor, entityMetadata, statementType, entityObject);
    }

    
}
