package com.srgbrl.laba.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Test version of ConnectionManager that uses H2 in-memory database.
 * This class is used for testing DAO classes without connecting to the real database.
 */
public class TestConnectionManager {
    private static final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    private static boolean initialized = false;

    /**
     * Opens a connection to the H2 in-memory database.
     * On first call, initializes the database schema.
     */
    public static Connection open() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        
        if (!initialized) {
            initializeDatabase(connection);
            initialized = true;
        }
        
        return connection;
    }
    
    /**
     * Initializes the database schema for testing.
     */
    private static void initializeDatabase(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Create users table
            statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "login VARCHAR(255) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(50) NOT NULL)");
            
            // Create faculties table
            statement.execute("CREATE TABLE IF NOT EXISTS faculties (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "\"limit\" INT NOT NULL, " +
                    "status VARCHAR(50) NOT NULL)");
            
            // Create applicants table
            statement.execute("CREATE TABLE IF NOT EXISTS applicants (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "full_name VARCHAR(255) NOT NULL, " +
                    "average_grade DOUBLE NOT NULL, " +
                    "faculty_id INT NOT NULL, " +
                    "user_id INT NOT NULL, " +
                    "results VARCHAR(255) NOT NULL, " +
                    "sum FLOAT NOT NULL)");
        }
    }
    
    /**
     * Resets the database by dropping all tables.
     * Useful for cleaning up between tests.
     */
    public static void resetDatabase() throws SQLException, ClassNotFoundException {
        try (Connection connection = open();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            initialized = false;
        }
    }
}