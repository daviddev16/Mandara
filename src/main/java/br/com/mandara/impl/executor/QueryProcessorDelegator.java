package br.com.mandara.impl.executor;

import java.sql.SQLException;

import br.com.mandara.core.IEntityDeserializer;
import br.com.mandara.core.IEntityQueryExecutor;
import br.com.mandara.core.StatementType;
import br.com.mandara.core.queries.IDMLQuery;
import br.com.mandara.core.queries.IDQLQuery;
import br.com.mandara.entity.EntityMetadata;
import br.com.mandara.impl.processor.DeleteQueryProcessor;
import br.com.mandara.impl.processor.InsertQueryProcessor;
import br.com.mandara.impl.processor.SelectQueryProcessor;
import br.com.mandara.impl.processor.UpdateQueryProcessor;

final class QueryProcessorDelegator {

    protected QueryProcessorDelegator() {}
    
    public <T> IDQLQuery<T> delegateSelectQuery(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata, 
            StatementType statementType,
            String sqlQuery, 
            IEntityDeserializer deserializer) throws SQLException {
        
        return new SelectQueryProcessor<T>(
                entityQueryExecutor, entityMetadata, 
                deserializer, statementType, sqlQuery);
    }

    public <T> IDMLQuery<T> delegateInsertQuery(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata, Object entityObject) throws SQLException {
        
        return new InsertQueryProcessor<T>(
                entityQueryExecutor, entityMetadata, 
                StatementType.PREPARED, entityObject);
    }
    
    public <T> IDMLQuery<T> delegateUpdateQuery(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata, 
            Object entityObject) throws SQLException {
        
        return new UpdateQueryProcessor<T>(
                entityQueryExecutor, entityMetadata, 
                StatementType.PREPARED, entityObject);
    }
    
    public <T> IDMLQuery<T> delegateDeleteQuery(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata, 
            Object entityObject) throws SQLException {
        
        return new DeleteQueryProcessor<T>(
                entityQueryExecutor, entityMetadata, 
                StatementType.PREPARED, entityObject);
    }

}
