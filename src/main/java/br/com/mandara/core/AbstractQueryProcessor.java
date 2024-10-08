package br.com.mandara.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.UUID;

import br.com.mandara.GlobalQueryListener;
import br.com.mandara.core.internal.QueryUUIDFactory;
import br.com.mandara.entity.EntityMetadata;
import br.com.mandara.impl.context.QueryProcessContext;

public abstract class AbstractQueryProcessor<T> implements IQueryProcessor<T> {

    private static final String EMPTY_SQL = "<<EmptySQL>>";
    
    private final IEntityQueryExecutor entityQueryExecutor;
    
    protected final EntityMetadata entityMetadata;
    protected final UUID queryProcessId;
    
    private String sqlQuery;
    protected final StatementType statementType;
    
    private volatile QueryState queryState;
    
    private final boolean isSpyable = true;
    
    private final LocalTime queryStartTime;
    
    public AbstractQueryProcessor(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata,
            StatementType statementType) {
        
        this(entityQueryExecutor, entityMetadata, EMPTY_SQL, statementType);
    }
    
    public AbstractQueryProcessor(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata,
            String sqlQuery, 
            StatementType statementType) {
        
        this.entityQueryExecutor = entityQueryExecutor;
        
        this.entityMetadata   = entityMetadata;
        this.sqlQuery         = sqlQuery;
        this.statementType    = statementType;

        this.queryProcessId = QueryUUIDFactory.newQueryUuid(this);
        this.queryStartTime = LocalTime.now();
        
        this.queryState = QueryState.PENDING;
    }
    
    protected void updateSQLQuery(String newSqlQuery) {
        this.sqlQuery = newSqlQuery;
    }
    
    protected synchronized void setQueryState(QueryState newQueryState) {
        QueryState oldState = queryState;
        queryState = newQueryState;
        if (isSpyable)
            GlobalQueryListener.getInstance()
                .fireOnChangedStateEvent(oldState, newQueryState, this);
    }
    
    protected QueryProcessContext createQueryResultContext(ResultSet resultSet) throws SQLException {
        return new QueryProcessContext(resultSet, getQueryProcessId());
    }
    
    @Override
    public void cancelQuery() {
        setQueryState(QueryState.CANCELLED);
    }

    public boolean isCancelled() {
        return queryState == QueryState.CANCELLED;
    }
    
    @Override
    public IEntityQueryExecutor getEntityQueryExecutor() {
        return entityQueryExecutor;
    }
    
    @Override
    public EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }
    
    @Override
    public UUID getQueryProcessId() {
        return queryProcessId;
    }
    
    @Override
    public QueryState getQueryState() {
        return queryState;
    }

    @Override
    public String getSQLQuery() {
        return sqlQuery;
    }
    
    @Override
    public LocalTime getQueryStartTime() {
        return queryStartTime;
    }
        
}
