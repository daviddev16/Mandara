package br.com.mandara.core.queries;

import br.com.mandara.core.IEntityDeserializer;
import br.com.mandara.exception.QueryProcessorException;

public interface IDMLQuery<T> extends IQuery {

    void execute() throws QueryProcessorException;
    
    default T executeReturning() throws QueryProcessorException {
        return executeReturning(null);
    }
    
    T executeReturning(IEntityDeserializer deserializer) throws QueryProcessorException;
    
}
