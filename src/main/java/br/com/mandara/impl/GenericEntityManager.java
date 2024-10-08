package br.com.mandara.impl;

import static br.com.mandara.exception.StaticEntityException.*;

import java.util.HashMap;
import java.util.Map;

import br.com.mandara.core.IEntityManager;
import br.com.mandara.entity.EntityMetadata;
import br.com.mandara.exception.EntityCreationException;

public final class GenericEntityManager implements IEntityManager {

    private static GenericEntityManager INSTANCE;
    
    private final Map<Class<?>, EntityMetadata> entityMetadataMapping;
    
    private GenericEntityManager() {
        entityMetadataMapping = new HashMap<Class<?>, EntityMetadata>();
    }
    
    @Override
    public EntityMetadata register(Class<?> entityClassType) {
        return createOrGetMetadataImpl(entityClassType, true);
    }
    
    @Override
    public EntityMetadata getMetadataOf(Class<?> entityClassType) {
        return createOrGetMetadataImpl(entityClassType, false);
    }
    
    @Override
    public <T> T newInstanceOf(Class<?> entityClassType) throws EntityCreationException {
        return entityMetadataMapping.get(entityClassType).newInstance();
    }
    
    private EntityMetadata createOrGetMetadataImpl(
            Class<?> entityClassType, boolean throwIfExists) {
        
        EntityMetadata entityMetadata = entityMetadataMapping.get(entityClassType);
        
        if (throwIfExists && entityMetadata != null)
            throw alreadyRegisteredStateException(entityClassType);
            
        if (entityMetadata == null) {
            synchronized (GenericEntityManager.class) {
                entityMetadata = new EntityMetadata(entityClassType);
                entityMetadataMapping.put(entityClassType, entityMetadata);                
            }
        }

        return entityMetadata;
    }
    
    public boolean isReady() {
        return entityMetadataMapping != null;
    }
    
    public static GenericEntityManager getInstance() {
        return (INSTANCE != null) ? INSTANCE : (createSynchronizedSingleton());
    }
    
    private static GenericEntityManager createSynchronizedSingleton() {
        synchronized (GenericEntityManager.class) {
            return (INSTANCE = new GenericEntityManager());
        }
    }
    
}
