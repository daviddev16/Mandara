package br.com.easyorm.core.queries;

import java.util.Collection;

import br.com.easyorm.exception.QueryProcessorException;

public interface IDQLQuery<T> extends IQuery {

    Collection<T> getDataSet() throws QueryProcessorException;

    T getSingleEntity();
    
}
