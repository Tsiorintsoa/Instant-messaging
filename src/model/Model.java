package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Model {

    private Connection CONNECTION = null;
    private final String DB_NAME;
    private final String USER;
    private final String PASSWORD;

    public Model(String dbName, String username, String password) {
        this.DB_NAME = dbName;
        this.USER = username;
        this.PASSWORD = password;
    }

    public Model() {
        this.DB_NAME = "instant-messaging";
        this.USER = "root";
        this.PASSWORD = "";
    }

    public Connection getConnection() throws SQLException {
        if (CONNECTION == null || CONNECTION.isClosed()) {
            final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
            try {
                CONNECTION = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Successful connection to MySQL / " + DB_NAME);
            } catch (SQLException e) {
                System.err.println("Database connection failed :\\ " + e.getMessage());
            }
        }
        return CONNECTION;
    }

    public void closeConnection() {
        if (CONNECTION != null) {
            try {
                CONNECTION.close();
                CONNECTION = null;
            } catch (SQLException e) {
                System.err.println("Error while closing connection: " + e.getMessage());
            }
        }
    }

    public boolean isConnected() throws SQLException {
        return CONNECTION != null && !CONNECTION.isClosed();
    }

    public String getDB_NAME() {
        return DB_NAME;
    }

    public String getUSER() {
        return USER;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

}
