package br.com.mandara.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.mandara.entity.EntityMetadata;
import br.com.mandara.exception.DeserializationException;
import br.com.mandara.exception.EntityCreationException;
import br.com.mandara.impl.context.QueryProcessContext;

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
            int rowCount, EntityMetadata entityMetadata) 
                    throws DeserializationException, EntityCreationException {
        try {
            return map(queryProcessContext.getResultSet(), rowCount);
        } catch (SQLException e) {
            throw new DeserializationException("Failed to map a row to entity." , e);
        }
    }
    
}
