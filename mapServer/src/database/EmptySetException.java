package database;

public class EmptySetException extends Exception {

    private static final long serialVersionUID = 1L;

    public EmptySetException() {
        super();
    }

    public EmptySetException(String message) {
        super(message);
    }
}
