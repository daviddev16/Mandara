package br.com.easyorm.entity;

import static br.com.easyorm.exception.StaticEntityException.*;

import java.util.HashMap;
import java.util.Map;

import br.com.easyorm.exception.EntityCreationException;

public final class EntityManager {

    private static EntityManager INSTANCE;
    
    private final Map<Class<?>, EntityMetadata> entityMetadataMapping;
    
    private EntityManager() {
        entityMetadataMapping = new HashMap<Class<?>, EntityMetadata>();
    }
    
    public EntityMetadata register(Class<?> entityClassType) {
        return createOrGetMetadataImpl(entityClassType, true);
    }
    
    public EntityMetadata getMetadataOf(Class<?> entityClassType) {
        return createOrGetMetadataImpl(entityClassType, false);
    }
    
    private EntityMetadata createOrGetMetadataImpl(
            Class<?> entityClassType, boolean throwIfExists) {
        
        EntityMetadata entityMetadata = entityMetadataMapping.get(entityClassType);
        
        if (throwIfExists && entityMetadata != null)
            throw alreadyRegisteredStateException(entityClassType);
            
        if (entityMetadata == null) {
            synchronized (EntityManager.class) {
                entityMetadata = new EntityMetadata(entityClassType);
                entityMetadataMapping.put(entityClassType, entityMetadata);                
            }
        }

        return entityMetadata;
    }
    
    public <T> T newInstanceOf(Class<?> entityClassType) throws EntityCreationException {
        return entityMetadataMapping.get(entityClassType).newInstance();
    }
    
    public boolean isReady() {
        return entityMetadataMapping != null;
    }
    
    public static EntityManager getInstance() {
        return (INSTANCE != null) ? INSTANCE : (createSynchronizedSingleton());
    }
    
    private static EntityManager createSynchronizedSingleton() {
        synchronized (EntityManager.class) {
            return (INSTANCE = new EntityManager());
        }
    }
    
}
