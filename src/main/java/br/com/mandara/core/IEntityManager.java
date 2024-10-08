package br.com.mandara.core;

import br.com.mandara.entity.EntityMetadata;
import br.com.mandara.exception.EntityCreationException;

public interface IEntityManager {
    
    public EntityMetadata register(Class<?> entityClassType);

    public EntityMetadata getMetadataOf(Class<?> entityClassType);
    
    public <T> T newInstanceOf(Class<?> entityClassType) throws EntityCreationException;
    
    public boolean isReady();
    
}
