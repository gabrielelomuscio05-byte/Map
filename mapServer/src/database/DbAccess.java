package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbAccess {

    private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private final String DBMS = "jdbc:mysql";
    private String SERVER = "localhost";
    private String DATABASE = "MapDB";
    private final int PORT = 3306;
    private String USER_ID = "MapUser";
    private String PASSWORD = "map";
    private Connection conn;

    public void initConnection() throws DatabaseConnectionException {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new DatabaseConnectionException("Driver non trovato: " + e.getMessage());
        }

        String url = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE + "?serverTimezone=UTC";
        try {
            conn = DriverManager.getConnection(url, USER_ID, PASSWORD);
        } catch (SQLException e) {
            try {
                url = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE;
                conn = DriverManager.getConnection(url, USER_ID, PASSWORD);
            } catch (SQLException e2) {
                throw new DatabaseConnectionException("Impossibile connettersi al database: " + e.getMessage());
            }
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}
