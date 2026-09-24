package server;

public class UnknownValueException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnknownValueException() {
        super();
    }

    public UnknownValueException(String message) {
        super(message);
    }
}
