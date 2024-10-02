package br.com.easyorm.core;

import br.com.easyorm.entity.EntityMetadata;
import br.com.easyorm.exception.DeserializationException;
import br.com.easyorm.exception.EntityCreationException;
import br.com.easyorm.impl.context.QueryProcessContext;

public interface IEntityDeserializer {

    <T> T deserialize(
            QueryProcessContext queryProcessContext, 
            int rowCount, 
            EntityMetadata entityMetadata) throws DeserializationException, EntityCreationException;
    
}
