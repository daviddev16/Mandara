package br.com.mandara.core;

import java.util.UUID;

public interface ICacheableEntityDeserializer extends IEntityDeserializer {
    
    void clearQueryCaching(UUID queryId);
    
}
