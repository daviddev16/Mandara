package br.com.easyorm.core;

import java.util.UUID;

final class QueryUUIDFactory {

    public static UUID newQueryUuid(IQueryProcessor<?> queryProcessor) {
        return UUID.randomUUID();
    }
    
}
