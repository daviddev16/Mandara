package br.com.mandara.core.internal;

import java.util.UUID;

import br.com.mandara.core.IQueryProcessor;

public final class QueryUUIDFactory {

    public static UUID newQueryUuid(IQueryProcessor<?> queryProcessor) {
        return UUID.randomUUID();
    }
    
}
