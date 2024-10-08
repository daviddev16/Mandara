package br.com.mandara.exception;

public class SerializationException extends Exception {

    private static final long serialVersionUID = 8562026446342338784L;

    public SerializationException() {
        super();
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
    
}
