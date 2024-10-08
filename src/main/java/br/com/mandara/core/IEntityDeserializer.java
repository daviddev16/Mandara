package br.com.mandara.core;

import br.com.mandara.entity.EntityMetadata;
import br.com.mandara.exception.DeserializationException;
import br.com.mandara.exception.EntityCreationException;
import br.com.mandara.impl.context.QueryProcessContext;

public interface IEntityDeserializer {

    <T> T deserialize(
            QueryProcessContext queryProcessContext, 
            int rowCount, 
            EntityMetadata entityMetadata) throws DeserializationException, EntityCreationException;
    
}
