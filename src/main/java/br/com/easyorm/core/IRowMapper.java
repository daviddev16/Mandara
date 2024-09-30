package br.com.easyorm.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import br.com.easyorm.entity.EntityMetadata;
import br.com.easyorm.exception.DeserializationException;
import br.com.easyorm.exception.EntityCreationException;
import br.com.easyorm.impl.context.QueryProcessContext;

/*
 * Allow developers to manually build the entity.
 * IEntityDeserializer adapter.
 **/
@SuppressWarnings("unchecked")
public interface IRowMapper<T> extends IEntityDeserializer {
    
    T map(ResultSet resultSet, int rowCount) throws SQLException;
    
    @Override
    default T deserialize(
            QueryProcessContext queryProcessContext, 
            int rowCount, 
            EntityMetadata entityMetadata) 
                    throws DeserializationException, EntityCreationException 
    {
        try {
            return map(queryProcessContext.getResultSet(), rowCount);
        } catch (SQLException e) {
            throw new DeserializationException("Failed to map a row to entity." , e);
        }
    }
    
    
    @Override
    default void clearQueryCaching(UUID queryId) {/* do nothing */}
    
}
