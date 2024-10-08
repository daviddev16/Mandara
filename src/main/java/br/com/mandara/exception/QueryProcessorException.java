package br.com.mandara.exception;

public class QueryProcessorException extends Exception {

    private static final long serialVersionUID = -2737044248455766660L;

    public QueryProcessorException() {
        super();
    }

    public QueryProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryProcessorException(String message) {
        super(message);
    }

    public QueryProcessorException(Throwable cause) {
        super(cause);
    }
    
}
