package com.srgbrl.laba.dao;

import com.srgbrl.laba.entity.User;
import com.srgbrl.laba.util.TestConnectionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDAOTest {

    private UserDAO userDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        // Reset the database before each test
        TestConnectionManager.resetDatabase();
        connection = TestConnectionManager.open();

        // Get the singleton instance
        userDAO = UserDAO.getInstance();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testSaveUser() throws SQLException, ClassNotFoundException {
        // Create a test user using the constructor without ID
        User user = new User("testuser", "password123", "USER");

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<com.srgbrl.laba.util.ConnectionManager> mockedConnectionManager = 
                Mockito.mockStatic(com.srgbrl.laba.util.ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(() -> com.srgbrl.laba.util.ConnectionManager.open())
                    .thenReturn(connection);

            // Save the user
            User savedUser = userDAO.save(user);

            // Verify the user was saved with an ID
            assertNotNull(savedUser, "Saved user should not be null");
            assertTrue(savedUser.getId() > 0, "Saved user should have a positive ID");
            assertEquals("testuser", savedUser.getLogin(), "Login should match");
            assertEquals("password123", savedUser.getPassword(), "Password should match");
            assertEquals("USER", savedUser.getRole(), "Role should match");
        }

        // Open a new connection to verify the results
        try (Connection newConnection = TestConnectionManager.open();
             PreparedStatement statement = newConnection.prepareStatement("SELECT * FROM users WHERE login = ?")) {

            statement.setString(1, "testuser");
            ResultSet resultSet = statement.executeQuery();

            assertTrue(resultSet.next(), "User should exist in database");
            assertEquals("testuser", resultSet.getString("login"), "Login should match");
            assertEquals("password123", resultSet.getString("password"), "Password should match");
            assertEquals("USER", resultSet.getString("role"), "Role should match");
        }
    }

    @Test
    void testFindByLogin() throws SQLException, ClassNotFoundException {
        // Insert a test user directly into the database
        PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO users (login, password, role) VALUES (?, ?, ?)");
        insertStatement.setString(1, "finduser");
        insertStatement.setString(2, "findpass");
        insertStatement.setString(3, "ADMIN");
        insertStatement.executeUpdate();

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<com.srgbrl.laba.util.ConnectionManager> mockedConnectionManager = 
                Mockito.mockStatic(com.srgbrl.laba.util.ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(() -> com.srgbrl.laba.util.ConnectionManager.open())
                    .thenReturn(connection);

            // Find the user
            User foundUser = userDAO.findByLogin("finduser");

            // Verify the user was found
            assertNotNull(foundUser, "Found user should not be null");
            assertEquals("finduser", foundUser.getLogin(), "Login should match");
            assertEquals("findpass", foundUser.getPassword(), "Password should match");
            assertEquals("ADMIN", foundUser.getRole(), "Role should match");

            // Test finding a non-existent user
            User nonExistentUser = userDAO.findByLogin("nonexistent");
            assertNull(nonExistentUser, "Non-existent user should be null");
        }
    }
}
