package tree;

/**
 * Classe mantenuta per retrocompatibilità.
 * La classe ufficiale è ora definita nel package server (server.UnknownValueException).
 */
public class UnknownValueException extends server.UnknownValueException {

    private static final long serialVersionUID = 1L;

    public UnknownValueException() {
        super();
    }

    public UnknownValueException(String message) {
        super(message);
    }
}
