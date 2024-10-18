package br.com.mandara.core.queries;

import java.util.List;

import br.com.mandara.exception.QueryProcessorException;

public interface IDQLQuery<T> extends IQuery {

    List<T> getDataSet() throws QueryProcessorException;

    T getSingleEntity() throws QueryProcessorException;
    
}
