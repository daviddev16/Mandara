package br.com.easyorm.impl.processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import br.com.easyorm.core.AbstractQueryProcessor;
import br.com.easyorm.core.IEntityDeserializer;
import br.com.easyorm.core.IEntityQueryExecutor;
import br.com.easyorm.core.IParameterized;
import br.com.easyorm.core.IStatementWrapper;
import br.com.easyorm.core.QueryState;
import br.com.easyorm.core.StatementType;
import br.com.easyorm.core.queries.IDQLQuery;
import br.com.easyorm.entity.EntityMetadata;
import br.com.easyorm.exception.DeserializationException;
import br.com.easyorm.exception.EntityCreationException;
import br.com.easyorm.exception.QueryProcessorException;
import br.com.easyorm.impl.context.QueryProcessContext;
import br.com.easyorm.impl.wrapper.StatementWrapperFactory;

@SuppressWarnings("unchecked")
public class SelectQueryProcessorImpl<T> extends AbstractQueryProcessor<T> implements IDQLQuery<T> {

    private final IEntityDeserializer deserializer;
    private final IStatementWrapper statementWrapper;

    private volatile Collection<?> dataSet;
    
    public SelectQueryProcessorImpl(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata, 
            IEntityDeserializer deserializer, 
            StatementType statementType,
            String sqlQuery) throws SQLException 
    {
        super(entityQueryExecutor, entityMetadata, sqlQuery, statementType);
        
        this.deserializer = deserializer;
        
        final Connection connection = entityQueryExecutor.getConnection();

        statementWrapper = StatementWrapperFactory.get(connection, sqlQuery, statementType);
    }
    
    @Override
    public void processQuery() throws QueryProcessorException 
    {
        Collection<T> fetchedDataSet = new ArrayList<T>();
        QueryProcessContext queryProcessContext = null;
        
        try {
            int rowCount = 0;
            
            final ResultSet resultSet = statementWrapper.executeQuery();
            setQueryState(QueryState.PROCESSING);
            
            queryProcessContext = createQueryResultContext(resultSet);
            
            while (resultSet.next() && !isCancelled()) 
                fetchedDataSet.add(deserializeSingleInternal(
                        rowCount++, entityMetadata, queryProcessContext));
        
            if (!isCancelled())
            {
                dataSet = fetchedDataSet;
                setQueryState(QueryState.DONE);
            }
            
        } catch (Exception ex) {
            setQueryState(QueryState.ERROR);
            throw new QueryProcessorException("Failed to process "
                    + "SELECT query: \n\n " + getSQLQuery() + "\n\n" , ex);
            
        } finally {
            statementWrapper.closeQuietly();

            if (queryProcessContext != null)
                deserializer.clearQueryCaching(queryProcessContext.getQueryId());
        }
        
        if (isCancelled())
        {
            dataSet = Collections.EMPTY_LIST;
            fetchedDataSet.clear();
        }
    }
    
    @Override
    public Collection<T> getDataSet() throws QueryProcessorException 
    {
        if (dataSet == null) 
            processQuery();

        return (Collection<T>) dataSet;
    }

    @Override
    public T getSingleEntity() {
        throw new UnsupportedOperationException("getSingleEntity() not implemented yet.");
    }

    private T deserializeSingleInternal(
            int rowCount, 
            EntityMetadata entityMetadata, 
            QueryProcessContext queryResultContext) 
                    throws DeserializationException, EntityCreationException
    {
        return (T) deserializer.deserialize(queryResultContext, rowCount, entityMetadata);
    }
    
    private QueryProcessContext createQueryResultContext(ResultSet resultSet) 
            throws SQLException
    {
        return new QueryProcessContext(resultSet, getQueryProcessId());
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
