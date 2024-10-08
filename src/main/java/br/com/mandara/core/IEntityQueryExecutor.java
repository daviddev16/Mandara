package br.com.mandara.core;

import java.sql.Connection;
import java.sql.SQLException;

import br.com.mandara.core.queries.IDMLQuery;
import br.com.mandara.core.queries.IDQLQuery;
import br.com.mandara.impl.executor.EntityQueryExecutorImpl;

public interface IEntityQueryExecutor {

    public static IEntityQueryExecutor getImplementation(IEntityManager entityManager, Connection connection) {
        return new EntityQueryExecutorImpl(entityManager, connection);
    }
    
    <T> IDQLQuery<T> executeSelectQuery(String sql, Class<? super T> entityClassType) throws SQLException;
    
    <T> IDQLQuery<T> executeSelectQuery(String sql, Class<? super T> entityClassType, IEntityDeserializer deserializer) throws SQLException;
    
    <T> IDQLQuery<T> executePreparedSelectQuery(String sql, Class<? super T> entityClassType, IEntityDeserializer deserializer) throws SQLException;
    
    <T> IDQLQuery<T> executePreparedSelectQuery(String sql, Class<? super T> entityClassType) throws SQLException;
    
    <T> IDMLQuery<T> executeInsertQuery(T entityObject) throws SQLException;
    
    <T> IDMLQuery<T> executeUpdateQuery(T entityObject) throws SQLException;
    
    <T> IDMLQuery<T> executeDeleteQuery(T entityObject) throws SQLException;

    IEntityDeserializer getEntityDeserializer();
    
    IEntityManager getEntityManager();

    Connection getConnection();
    
    
/*  <T, IdT> T queryEntityById(IdT id, Class<? super T> entityClassType, IEntityDeserializer<T> deserializer);

    <T> List<T> queryAllEntities(Class<? super T> entityClassType, IEntityDeserializer<T> deserializer);
    
    <T> List<T> queryEntitiesBySql(String sql, Class<? super T> entityClassType, IEntityDeserializer<T> deserializer);
    
    <T> T querySingleEntityBySql(String sql, Class<? super T> entityClassType, IEntityDeserializer<T> deserializer);
    
    <T> T createEntityReturning(T entity, IEntityDeserializer<T> deserializer);
    
    <T> void createEntity(T entity);
    
    <T> T updateEntityReturning(T entity, IEntityDeserializer<T> deserializer);
    
    <T> void updateEntity(T entity);
    
    <T, IdT> void deleteEntityById(IdT id, Class<T> entityClassType);
    
    <T> void deleteEntity(T entity);*/
    
}
