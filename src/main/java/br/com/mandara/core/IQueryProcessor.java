package br.com.mandara.core;

import java.time.LocalTime;
import java.util.UUID;

import br.com.mandara.entity.EntityMetadata;
import br.com.mandara.exception.QueryProcessorException;

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
