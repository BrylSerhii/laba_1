package com.srgbrl.laba.dao;

import com.srgbrl.laba.entity.Faculty;
import com.srgbrl.laba.entity.Status;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class FacultyDaoTest {

    private FacultyDao facultyDao;
    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        // Reset the database before each test
        TestConnectionManager.resetDatabase();
        connection = TestConnectionManager.open();

        // Get the singleton instance
        facultyDao = FacultyDao.getInstance();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testSaveFaculty() throws SQLException, ClassNotFoundException {
        // Create a test faculty
        Faculty faculty = new Faculty("Test Faculty", 50, Status.OPEN);

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<com.srgbrl.laba.util.ConnectionManager> mockedConnectionManager = 
                mockStatic(com.srgbrl.laba.util.ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(() -> com.srgbrl.laba.util.ConnectionManager.open())
                    .thenReturn(connection);

            // Save the faculty
            facultyDao.save(faculty);
        }

        // Open a new connection to verify the results
        try (Connection newConnection = TestConnectionManager.open();
             PreparedStatement statement = newConnection.prepareStatement("SELECT * FROM faculties WHERE name = ?")) {

            statement.setString(1, "Test Faculty");
            ResultSet resultSet = statement.executeQuery();

            assertTrue(resultSet.next(), "Faculty should exist in database");
            assertEquals("Test Faculty", resultSet.getString("name"), "Name should match");
            assertEquals(50, resultSet.getInt("limit"), "Limit should match");
            assertEquals("OPEN", resultSet.getString("status"), "Status should match");
        }
    }

    @Test
    void testFindById() throws SQLException, ClassNotFoundException {
        // Insert a test faculty directly into the database
        PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO faculties (name, \"limit\", status) VALUES (?, ?, ?)");
        insertStatement.setString(1, "Find Faculty");
        insertStatement.setInt(2, 100);
        insertStatement.setString(3, "OPEN");
        insertStatement.executeUpdate();

        // Get the ID of the inserted faculty
        PreparedStatement idStatement = connection.prepareStatement("SELECT id FROM faculties WHERE name = ?");
        idStatement.setString(1, "Find Faculty");
        ResultSet idResultSet = idStatement.executeQuery();
        assertTrue(idResultSet.next(), "Faculty should exist in database");
        int facultyId = idResultSet.getInt("id");

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<com.srgbrl.laba.util.ConnectionManager> mockedConnectionManager = 
                mockStatic(com.srgbrl.laba.util.ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(() -> com.srgbrl.laba.util.ConnectionManager.open())
                    .thenReturn(connection);

            // Find the faculty
            Faculty foundFaculty = facultyDao.findById(facultyId);

            // Verify the faculty was found
            assertNotNull(foundFaculty, "Found faculty should not be null");
            assertEquals("Find Faculty", foundFaculty.getName(), "Name should match");
            assertEquals(100, foundFaculty.getLimit(), "Limit should match");
            assertEquals(Status.OPEN, foundFaculty.getStatus(), "Status should match");

            // Test finding a non-existent faculty
            Faculty nonExistentFaculty = facultyDao.findById(999);
            assertNull(nonExistentFaculty, "Non-existent faculty should be null");
        }
    }

    @Test
    void testFindAll() throws SQLException, ClassNotFoundException {
        // Insert multiple test faculties directly into the database
        PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO faculties (name, \"limit\", status) VALUES (?, ?, ?)");

        // Faculty 1
        insertStatement.setString(1, "Faculty 1");
        insertStatement.setInt(2, 50);
        insertStatement.setString(3, "OPEN");
        insertStatement.executeUpdate();

        // Faculty 2
        insertStatement.setString(1, "Faculty 2");
        insertStatement.setInt(2, 75);
        insertStatement.setString(3, "CLOSED");
        insertStatement.executeUpdate();

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<com.srgbrl.laba.util.ConnectionManager> mockedConnectionManager = 
                mockStatic(com.srgbrl.laba.util.ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(() -> com.srgbrl.laba.util.ConnectionManager.open())
                    .thenReturn(connection);

            // Find all faculties
            List<Faculty> faculties = facultyDao.findAll();

            // Verify the faculties were found
            assertNotNull(faculties, "Faculties list should not be null");
            assertEquals(2, faculties.size(), "Should find 2 faculties");

            // Verify the first faculty
            Faculty faculty1 = faculties.stream()
                    .filter(f -> f.getName().equals("Faculty 1"))
                    .findFirst()
                    .orElse(null);
            assertNotNull(faculty1, "Faculty 1 should be found");
            assertEquals(50, faculty1.getLimit(), "Limit should match");
            assertEquals(Status.OPEN, faculty1.getStatus(), "Status should match");

            // Verify the second faculty
            Faculty faculty2 = faculties.stream()
                    .filter(f -> f.getName().equals("Faculty 2"))
                    .findFirst()
                    .orElse(null);
            assertNotNull(faculty2, "Faculty 2 should be found");
            assertEquals(75, faculty2.getLimit(), "Limit should match");
            assertEquals(Status.CLOSED, faculty2.getStatus(), "Status should match");
        }
    }

    @Test
    void testCloseFaculty() throws SQLException, ClassNotFoundException {
        // Insert a test faculty directly into the database
        PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO faculties (name, \"limit\", status) VALUES (?, ?, ?)");
        insertStatement.setString(1, "Close Faculty");
        insertStatement.setInt(2, 100);
        insertStatement.setString(3, "OPEN");
        insertStatement.executeUpdate();

        // Get the ID of the inserted faculty
        PreparedStatement idStatement = connection.prepareStatement("SELECT id FROM faculties WHERE name = ?");
        idStatement.setString(1, "Close Faculty");
        ResultSet idResultSet = idStatement.executeQuery();
        assertTrue(idResultSet.next(), "Faculty should exist in database");
        int facultyId = idResultSet.getInt("id");

        // Create a faculty object with the ID
        Faculty faculty = new Faculty(facultyId, "Close Faculty", 100, Status.OPEN);

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<com.srgbrl.laba.util.ConnectionManager> mockedConnectionManager = 
                mockStatic(com.srgbrl.laba.util.ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(() -> com.srgbrl.laba.util.ConnectionManager.open())
                    .thenReturn(connection);

            // Close the faculty
            facultyDao.closeFaculty(faculty);
        }

        // Open a new connection to verify the results
        try (Connection newConnection = TestConnectionManager.open();
             PreparedStatement statement = newConnection.prepareStatement("SELECT status FROM faculties WHERE id = ?")) {

            statement.setInt(1, facultyId);
            ResultSet resultSet = statement.executeQuery();

            assertTrue(resultSet.next(), "Faculty should exist in database");
            assertEquals("CLOSED", resultSet.getString("status"), "Status should be CLOSED");
        }
    }
}
