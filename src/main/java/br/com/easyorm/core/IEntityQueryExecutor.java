package br.com.easyorm.core;

import java.sql.Connection;
import java.sql.SQLException;

import br.com.easyorm.core.dialect.IDialect;
import br.com.easyorm.core.queries.IDMLQuery;
import br.com.easyorm.core.queries.IDQLQuery;

public interface IEntityQueryExecutor {

    <T> IDQLQuery<T> executeSelectQuery(String sql, Class<? super T> entityClassType) throws SQLException;
    
    <T> IDQLQuery<T> executeSelectQuery(String sql, Class<? super T> entityClassType, IEntityDeserializer deserializer) throws SQLException;
    
    <T> IDQLQuery<T> executePreparedSelectQuery(String sql, Class<? super T> entityClassType, IEntityDeserializer deserializer) throws SQLException;
    
    <T> IDQLQuery<T> executePreparedSelectQuery(String sql, Class<? super T> entityClassType) throws SQLException;
    
    <T> IDQLQuery<T> executePreparedInsertQuery(T entityObject) throws SQLException;
    
    <T> IDMLQuery<T> executeInsertQuery(T entityObject) throws SQLException;
    
    Connection getConnection();
    
    IDialect getDialect();
    
    
/*    <T, IdT> T queryEntityById(IdT id, Class<? super T> entityClassType, IEntityDeserializer<T> deserializer);

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
