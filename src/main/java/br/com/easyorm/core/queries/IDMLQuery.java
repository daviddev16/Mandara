package br.com.easyorm.core.queries;

import br.com.easyorm.exception.QueryProcessorException;

public interface IDMLQuery<T> extends IQuery {

    void execute() throws QueryProcessorException;
    
    T executeReturning() throws QueryProcessorException;
    
}
