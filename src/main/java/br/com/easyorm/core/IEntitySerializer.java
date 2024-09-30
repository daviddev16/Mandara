package br.com.easyorm.core;

import java.util.Map;
import java.util.UUID;

import br.com.easyorm.entity.EntityMetadata;
import br.com.easyorm.exception.DeserializationException;
import br.com.easyorm.exception.EntityCreationException;
import br.com.easyorm.impl.context.QueryProcessContext;

public interface IEntitySerializer {

    Map<String, Object> serialize(
            QueryProcessContext queryProcessContext, 
            int rowCount, 
            EntityMetadata entityMetadata) throws DeserializationException, EntityCreationException;
    
    void clearQueryCaching(UUID queryId);
    
}
