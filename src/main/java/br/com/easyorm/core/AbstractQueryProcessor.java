package br.com.easyorm.core;

import java.time.LocalTime;
import java.util.UUID;

import br.com.easyorm.GlobalQueryListener;
import br.com.easyorm.entity.EntityMetadata;

public abstract class AbstractQueryProcessor<T> implements IQueryProcessor<T> {

    private static final String EMPTY_SQL = "<<EmptySQL>>";
    
    private final IEntityQueryExecutor entityQueryExecutor;
    
    protected final EntityMetadata entityMetadata;
    protected final UUID queryProcessId;
    
    private String sqlQuery;
    protected final StatementType statementType;
    
    private volatile QueryState queryState;
    
    private final boolean isSpyable = false;
    
    private final LocalTime queryStartTime;
    
    public AbstractQueryProcessor(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata,
            StatementType statementType) 
    {
        this(entityQueryExecutor, entityMetadata, EMPTY_SQL, statementType);
    }
    
    public AbstractQueryProcessor(
            IEntityQueryExecutor entityQueryExecutor,
            EntityMetadata entityMetadata,
            String sqlQuery, 
            StatementType statementType) 
    {
        this.entityQueryExecutor = entityQueryExecutor;
        
        this.entityMetadata   = entityMetadata;
        this.sqlQuery         = sqlQuery;
        this.statementType    = statementType;

        this.queryProcessId = QueryUUIDFactory.newQueryUuid(this);
        this.queryStartTime = LocalTime.now();
        
        setQueryState(QueryState.PENDING);
    }
    
    protected void updateSQLQuery(String newSqlQuery)
    {
        this.sqlQuery = newSqlQuery;
    }
    
    protected synchronized void setQueryState(QueryState newQueryState)
    {
        QueryState oldState = queryState;
        queryState = newQueryState;
        if (isSpyable)
            GlobalQueryListener.getInstance().fireOnChangedStateEvent(oldState, newQueryState, this);
    }
    
    @Override
    public void cancelQuery()
    {
        setQueryState(QueryState.CANCELLED);
    }

    public boolean isCancelled()
    {
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
