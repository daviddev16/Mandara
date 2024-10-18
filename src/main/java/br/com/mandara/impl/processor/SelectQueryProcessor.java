package br.com.mandara.impl.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import br.com.mandara.core.AbstractQueryProcessor;
import br.com.mandara.core.ICacheableEntityDeserializer;
import br.com.mandara.core.IEntityDeserializer;
import br.com.mandara.core.IEntityQueryExecutor;
import br.com.mandara.core.IParameterized;
import br.com.mandara.core.IStatementWrapper;
import br.com.mandara.core.QueryState;
import br.com.mandara.core.StatementType;
import br.com.mandara.core.queries.IDQLQuery;
import br.com.mandara.entity.EntityMetadata;
import br.com.mandara.exception.DeserializationException;
import br.com.mandara.exception.EntityCreationException;
import br.com.mandara.exception.QueryProcessorException;
import br.com.mandara.impl.context.QueryProcessContext;
import br.com.mandara.impl.wrapper.StatementWrapperFactory;

@SuppressWarnings("unchecked")
public class SelectQueryProcessor<T> extends AbstractQueryProcessor<T> implements IDQLQuery<T> {

    private final IEntityDeserializer deserializer;
    private final IStatementWrapper statementWrapper;

    private volatile Collection<?> dataSet;
    
    public SelectQueryProcessor(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata, 
            IEntityDeserializer deserializer, 
            StatementType statementType,
            String sqlQuery) throws SQLException {
        
        super(entityQueryExecutor, entityMetadata, sqlQuery, statementType);
        
        statementWrapper = StatementWrapperFactory
                .get(entityQueryExecutor.getConnection(), sqlQuery, statementType);

        this.deserializer = deserializer;
    }
    
    @Override
    public void processQuery() throws QueryProcessorException {
        List<T> fetchedDataSet = new ArrayList<T>();
        QueryProcessContext queryProcessContext = null;
        try {
            int rowCount = 0;
            
            final ResultSet resultSet = statementWrapper.executeQuery();
            setQueryState(QueryState.PROCESSING);
            
            queryProcessContext = createQueryResultContext(resultSet);
            
            while (resultSet.next() && !isCancelled()) 
                fetchedDataSet.add(deserializeSingleInternal(
                        rowCount++, entityMetadata, queryProcessContext));
        
            if (!isCancelled()) {
                dataSet = fetchedDataSet;
                setQueryState(QueryState.DONE);
            } else {
                dataSet = Collections.EMPTY_LIST;
                fetchedDataSet.clear();
            }
            
        } catch (Exception ex) {
            setQueryState(QueryState.ERROR);
            throw new QueryProcessorException("Failed to process "
                    + "SELECT query: \n\n " + getSQLQuery() + "\n\n" , ex);
            
        } finally {
            statementWrapper.closeQuietly();
            if ((queryProcessContext != null) && 
                (deserializer instanceof ICacheableEntityDeserializer))
                ((ICacheableEntityDeserializer)deserializer)
                    .clearQueryCaching(queryProcessContext.getQueryId());
        }
    }

    @Override
    public T getSingleEntity() throws QueryProcessorException {
        List<T> dataSet0 = getDataSet();
        return dataSet0.isEmpty() ? null : dataSet0.get(0);
    }
    
    @Override
    public List<T> getDataSet() throws QueryProcessorException {
        if (dataSet == null) 
            processQuery();
        return (List<T>) dataSet;
    }

    private T deserializeSingleInternal(
            int rowCount, EntityMetadata entityMetadata, 
            QueryProcessContext queryResultContext) 
                    throws DeserializationException, EntityCreationException {
        return (T) deserializer.deserialize(queryResultContext, rowCount, entityMetadata);
    }
    
    @Override
    public IParameterized getParameterized() {
        return statementWrapper;
    }

    public EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    public IEntityDeserializer getDeserializer() {
        return deserializer;
    }

}
