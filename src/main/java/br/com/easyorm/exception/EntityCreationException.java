package br.com.easyorm.exception;

public class EntityCreationException extends Exception {

    private static final long serialVersionUID = 4533252364334886869L;

    public EntityCreationException() {
        super();
    }

    public EntityCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityCreationException(String message) {
        super(message);
    }

    public EntityCreationException(Throwable cause) {
        super(cause);
    }

}
