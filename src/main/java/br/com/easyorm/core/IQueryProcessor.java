package br.com.easyorm.core;

import java.time.LocalTime;
import java.util.UUID;

import br.com.easyorm.entity.EntityMetadata;
import br.com.easyorm.exception.QueryProcessorException;

public interface IQueryProcessor<T> {
    
    IEntityQueryExecutor getEntityQueryExecutor();
    
    void processQuery() throws QueryProcessorException;
    
    EntityMetadata getEntityMetadata();
    
    QueryState getQueryState();
    
    UUID getQueryProcessId();
    
    String getSQLQuery();
    
    LocalTime getQueryStartTime();
    
    void cancelQuery();
    
}
