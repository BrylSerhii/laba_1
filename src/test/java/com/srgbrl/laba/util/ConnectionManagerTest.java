package com.srgbrl.laba.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionManagerTest {

    @AfterEach
    void tearDown() throws SQLException, ClassNotFoundException {
        TestConnectionManager.resetDatabase();
    }

    @Test
    void testOpenConnection() {
        try {
            Connection connection = TestConnectionManager.open();
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");

            // Clean up
            connection.close();
            assertTrue(connection.isClosed(), "Connection should be closed after calling close()");
        } catch (ClassNotFoundException | SQLException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testDatabaseInitialization() {
        try {
            Connection connection = TestConnectionManager.open();

            // Test that tables exist
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery("SHOW TABLES");

            boolean usersTableExists = false;
            boolean facultiesTableExists = false;
            boolean applicantsTableExists = false;

            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                if ("USERS".equalsIgnoreCase(tableName)) {
                    usersTableExists = true;
                } else if ("FACULTIES".equalsIgnoreCase(tableName)) {
                    facultiesTableExists = true;
                } else if ("APPLICANTS".equalsIgnoreCase(tableName)) {
                    applicantsTableExists = true;
                }
            }

            assertTrue(usersTableExists, "Users table should exist");
            assertTrue(facultiesTableExists, "Faculties table should exist");
            assertTrue(applicantsTableExists, "Applicants table should exist");

            // Clean up
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}
