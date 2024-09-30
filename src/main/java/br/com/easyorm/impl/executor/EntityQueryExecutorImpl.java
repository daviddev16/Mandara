package br.com.easyorm.impl.executor;

import java.sql.Connection;
import java.sql.SQLException;

import br.com.easyorm.core.IEntityDeserializer;
import br.com.easyorm.core.IEntityQueryExecutor;
import br.com.easyorm.core.StatementType;
import br.com.easyorm.core.dialect.IDialect;
import br.com.easyorm.core.queries.IDMLQuery;
import br.com.easyorm.core.queries.IDQLQuery;
import br.com.easyorm.entity.EntityManager;
import br.com.easyorm.entity.EntityMetadata;
import br.com.easyorm.impl.deserializer.JavaTypeEntityDeserializer;
import br.com.easyorm.util.Checks;

public class EntityQueryExecutorImpl implements IEntityQueryExecutor {
    
    private final QueryProcessorDelegator queryProcessorDelegator;
    
    private final IEntityDeserializer defaultDeserializer;
    
    private final EntityManager entityManager;
    private final Connection connection;
    
    private final IDialect dialect;
    
    public EntityQueryExecutorImpl(EntityManager entityManager, Connection connection, IDialect dialect) 
    {
        Checks.stateNotNull(entityManager,    "EntityQueryExecutorImpl#entityManager");
        Checks.stateNotNull(connection,       "EntityQueryExecutorImpl#connection");
        Checks.stateNotNull(dialect,          "EntityQueryExecutorImpl#dialect");
        
        Checks.state((!entityManager.isReady()), "EntityQueryExecutorImpl#entityManager is not ready.");
      
        this.connection    = connection;
        this.entityManager = entityManager;
        this.dialect       = dialect;
        
        this.queryProcessorDelegator = new QueryProcessorDelegator();
        this.defaultDeserializer     = new JavaTypeEntityDeserializer();        
    }

    @Override
    public <T> IDQLQuery<T> executeSelectQuery(String sqlQuery, Class<? super T> entityClassType) 
            throws SQLException 
    {
        return queryProcessorDelegator.delegateSelectQuery(
                this,
                entityManager.getMetadataOf(entityClassType), 
                StatementType.NONE,
                sqlQuery, defaultDeserializer);
    }
    
    @Override
    public <T> IDQLQuery<T> executeSelectQuery(String sqlQuery, 
            Class<? super T> entityClassType, IEntityDeserializer deserializer) 
            throws SQLException 
    {
        return queryProcessorDelegator.delegateSelectQuery(
                this,
                entityManager.getMetadataOf(entityClassType), 
                StatementType.NONE,
                sqlQuery, deserializer);
    }
    
    @Override
    public <T> IDQLQuery<T> executePreparedSelectQuery(String sqlQuery, Class<? super T> entityClassType) 
            throws SQLException 
    {
        return queryProcessorDelegator.delegateSelectQuery(
                this,
                entityManager.getMetadataOf(entityClassType), 
                StatementType.PREPARED,
                sqlQuery, defaultDeserializer);
    }
    
    @Override
    public <T> IDQLQuery<T> executePreparedSelectQuery(String sqlQuery, 
            Class<? super T> entityClassType, IEntityDeserializer deserializer) 
            throws SQLException 
    {
        return queryProcessorDelegator.delegateSelectQuery(
                this,
                entityManager.getMetadataOf(entityClassType), 
                StatementType.PREPARED,
                sqlQuery, deserializer);
    }
    
    @Override
    public <T> IDQLQuery<T> executePreparedInsertQuery(T entityObject) throws SQLException {
        return null;
    }

    @Override
    public <T> IDMLQuery<T> executeInsertQuery(T entityObject) throws SQLException 
    {
        EntityMetadata entityMetadata = 
                entityManager.getMetadataOf(entityObject.getClass());
        
        return queryProcessorDelegator.delegateInsertQuery(
                this,
                entityMetadata, 
                StatementType.PREPARED,
                entityObject);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    public Connection getConnection() {
        return connection;
    }

    @Override
    public IDialect getDialect() {
        return dialect;
    }

    
}
