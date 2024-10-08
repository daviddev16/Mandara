package br.com.mandara.impl.executor;

import java.sql.Connection;
import java.sql.SQLException;

import br.com.mandara.core.IEntityDeserializer;
import br.com.mandara.core.IEntityManager;
import br.com.mandara.core.IEntityQueryExecutor;
import br.com.mandara.core.StatementType;
import br.com.mandara.core.queries.IDMLQuery;
import br.com.mandara.core.queries.IDQLQuery;
import br.com.mandara.entity.EntityMetadata;
import br.com.mandara.impl.deserializer.JavaTypeEntityDeserializer;
import br.com.mandara.util.Checks;

public class EntityQueryExecutorImpl implements IEntityQueryExecutor {
    
    private final QueryProcessorDelegator queryProcessorDelegator;
    
    private final IEntityDeserializer defaultDeserializer;
    
    private final IEntityManager entityManager;
    private final Connection connection;
    
    public EntityQueryExecutorImpl(IEntityManager entityManager, Connection connection) {
        Checks.stateNotNull(entityManager, "EntityQueryExecutorImpl#entityManager");
        Checks.stateNotNull(connection, "EntityQueryExecutorImpl#connection");
        
        Checks.state((!entityManager.isReady()), "EntityQueryExecutorImpl#entityManager is not ready.");
      
        this.connection = connection;
        this.entityManager = entityManager;
        
        this.queryProcessorDelegator = new QueryProcessorDelegator();
        this.defaultDeserializer = new JavaTypeEntityDeserializer();        
    }

    @Override
    public <T> IDQLQuery<T> executeSelectQuery(String sqlQuery, Class<? super T> entityClassType) 
            throws SQLException {
        return queryProcessorDelegator.delegateSelectQuery(
                this,
                entityManager.getMetadataOf(entityClassType), 
                StatementType.NONE,
                sqlQuery, defaultDeserializer);
    }
    
    @Override
    public <T> IDQLQuery<T> executeSelectQuery(String sqlQuery, 
            Class<? super T> entityClassType, IEntityDeserializer deserializer) 
            throws SQLException {
        return queryProcessorDelegator.delegateSelectQuery(
                this,
                entityManager.getMetadataOf(entityClassType), 
                StatementType.NONE,
                sqlQuery, deserializer);
    }
    
    @Override
    public <T> IDQLQuery<T> executePreparedSelectQuery(String sqlQuery, Class<? super T> entityClassType) 
            throws SQLException {
        return queryProcessorDelegator.delegateSelectQuery(
                this,
                entityManager.getMetadataOf(entityClassType), 
                StatementType.PREPARED,
                sqlQuery, defaultDeserializer);
    }
    
    @Override
    public <T> IDQLQuery<T> executePreparedSelectQuery(String sqlQuery, 
            Class<? super T> entityClassType, IEntityDeserializer deserializer) 
            throws SQLException {
        return queryProcessorDelegator.delegateSelectQuery(
                this,
                entityManager.getMetadataOf(entityClassType), 
                StatementType.PREPARED,
                sqlQuery, deserializer);
    }
    
    @Override
    public <T> IDMLQuery<T> executeInsertQuery(T entityObject) throws SQLException {
        EntityMetadata entityMetadata = 
                entityManager.getMetadataOf(entityObject.getClass());
        return queryProcessorDelegator.delegateInsertQuery(
                this, entityMetadata, entityObject);
    }

    @Override
    public <T> IDMLQuery<T> executeUpdateQuery(T entityObject) throws SQLException {
        EntityMetadata entityMetadata = 
                entityManager.getMetadataOf(entityObject.getClass());
        return queryProcessorDelegator.delegateUpdateQuery(
                this, entityMetadata, entityObject);
    }

    @Override
    public <T> IDMLQuery<T> executeDeleteQuery(T entityObject) throws SQLException {
        EntityMetadata entityMetadata = 
                entityManager.getMetadataOf(entityObject.getClass());
        return queryProcessorDelegator.delegateDeleteQuery(
                this, entityMetadata, entityObject);
    }

    @Override
    public IEntityDeserializer getEntityDeserializer() {
        return defaultDeserializer;
    }
    
    @Override
    public IEntityManager getEntityManager() {
        return entityManager;
    }
    
    @Override
    public Connection getConnection() {
        return connection;
    }
    
}
