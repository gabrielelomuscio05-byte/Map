package database;

public class DatabaseConnectionException extends Exception {

    private static final long serialVersionUID = 1L;

    public DatabaseConnectionException() {
        super();
    }

    public DatabaseConnectionException(String message) {
        super(message);
    }
}
