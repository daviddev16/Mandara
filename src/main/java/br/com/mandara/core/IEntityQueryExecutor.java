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

    IEntityManager getEntityManager();

    IEntityDeserializer getEntityDeserializer();

    Connection getConnection();

}
