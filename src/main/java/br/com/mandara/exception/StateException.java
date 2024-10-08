package br.com.mandara.exception;

public class StateException extends RuntimeException {

    private static final long serialVersionUID = 4176502759491224249L;

    public StateException() {
        super();
    }

    public StateException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateException(String message) {
        super(message);
    }

    public StateException(Throwable cause) {
        super(cause);
    }
}
