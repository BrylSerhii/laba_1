package com.srgbrl.laba.service;

import com.srgbrl.laba.dao.UserDAO;
import com.srgbrl.laba.entity.User;
import com.srgbrl.laba.util.ConnectionManager;
import com.srgbrl.laba.util.TestConnectionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private Connection connection;
    private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        // Reset the database before each test
        TestConnectionManager.resetDatabase();
        connection = TestConnectionManager.open();

        // Get the singleton instance
        authService = AuthService.getInstance();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Helper method to insert a test user directly into the database
    private User insertTestUser(String login, String password, String role) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO users (login, password, role) VALUES (?, ?, ?)",
                java.sql.Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, login);
        statement.setString(2, password);
        statement.setString(3, role);
        statement.executeUpdate();

        int userId = 0;
        try (var keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                userId = keys.getInt(1);
            }
        }

        return new User(userId, login, password, role);
    }

    // Helper method to check if a user exists in the database
    private boolean userExistsInDb(String login) throws SQLException, ClassNotFoundException {
        try (Connection newConnection = TestConnectionManager.open();
             PreparedStatement statement = newConnection.prepareStatement("SELECT 1 FROM users WHERE login = ?")) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Test
    void testRegisterSuccess() throws SQLException {
        // Setup - make sure the user doesn't exist
        String login = "newuser";
        String password = "password";
        String role = "USER";

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<ConnectionManager> mockedConnectionManager = 
                Mockito.mockStatic(ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(ConnectionManager::open)
                    .thenReturn(connection);

            // Execute
            User user = new User(login, password, role);
            User result = authService.register(user);

            // Verify
            assertNotNull(result, "Registered user should not be null");
            assertTrue(result.getId() > 0, "User ID should be set");
            assertEquals(login, result.getLogin(), "Login should match");
            assertEquals(password, result.getPassword(), "Password should match");
            assertEquals(role, result.getRole(), "Role should match");

            // Verify user exists in database
            assertTrue(userExistsInDb(login), "User should exist in database");
        }
    }

    @Test
    void testRegisterUserAlreadyExists() throws SQLException {
        // Setup - insert a user that already exists
        String login = "existinguser";
        String password = "oldpassword";
        String role = "USER";
        insertTestUser(login, password, role);

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<ConnectionManager> mockedConnectionManager = 
                Mockito.mockStatic(ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(ConnectionManager::open)
                    .thenReturn(connection);

            // Execute - try to register with the same login but different password
            User newUser = new User(login, "newpassword", role);
            User result = authService.register(newUser);

            // Verify
            assertNull(result, "Registration should fail for existing user");

            // Verify the password wasn't changed in the database
            try (PreparedStatement statement = connection.prepareStatement("SELECT password FROM users WHERE login = ?")) {
                statement.setString(1, login);
                try (ResultSet resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next(), "User should exist in database");
                    assertEquals(password, resultSet.getString("password"), "Password should not be changed");
                }
            }
        }
    }

    @Test
    void testLoginSuccess() throws SQLException {
        // Setup - insert a test user
        String login = "testuser";
        String password = "correctpassword";
        String role = "USER";
        insertTestUser(login, password, role);

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<ConnectionManager> mockedConnectionManager = 
                Mockito.mockStatic(ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(ConnectionManager::open)
                    .thenReturn(connection);

            // Execute
            User result = authService.login(login, password);

            // Verify
            assertNotNull(result, "Login should succeed with correct credentials");
            assertTrue(result.getId() > 0, "User ID should be set");
            assertEquals(login, result.getLogin(), "Login should match");
            assertEquals(password, result.getPassword(), "Password should match");
            assertEquals(role, result.getRole(), "Role should match");
        }
    }

    @Test
    void testLoginWrongPassword() throws SQLException {
        // Setup - insert a test user
        String login = "testuser";
        String password = "correctpassword";
        String role = "USER";
        insertTestUser(login, password, role);

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<ConnectionManager> mockedConnectionManager = 
                Mockito.mockStatic(ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(ConnectionManager::open)
                    .thenReturn(connection);

            // Execute - try to login with wrong password
            User result = authService.login(login, "wrongpassword");

            // Verify
            assertNull(result, "Login should fail with wrong password");
        }
    }

    @Test
    void testLoginUserNotFound() throws SQLException {
        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<ConnectionManager> mockedConnectionManager = 
                Mockito.mockStatic(ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(ConnectionManager::open)
                    .thenReturn(connection);

            // Execute - try to login with non-existent user
            User result = authService.login("nonexistentuser", "anypassword");

            // Verify
            assertNull(result, "Login should fail for non-existent user");
        }
    }
}
