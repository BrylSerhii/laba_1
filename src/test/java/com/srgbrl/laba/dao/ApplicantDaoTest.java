package com.srgbrl.laba.dao;

import com.srgbrl.laba.entity.Applicant;
import com.srgbrl.laba.entity.Faculty;
import com.srgbrl.laba.entity.Status;
import com.srgbrl.laba.entity.User;
import com.srgbrl.laba.util.TestConnectionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class ApplicantDaoTest {

    private ApplicantDao applicantDao;
    private Connection connection;
    private int facultyId;
    private int userId;

    @BeforeEach
    void setUp() throws Exception {
        // Reset the database before each test
        TestConnectionManager.resetDatabase();
        connection = TestConnectionManager.open();

        // Get the singleton instance
        applicantDao = ApplicantDao.getInstance();

        // Create a test faculty and user for the applicants
        setupTestData();
    }

    private void setupTestData() throws SQLException {
        // Insert a test faculty
        PreparedStatement facultyStatement = connection.prepareStatement(
                "INSERT INTO faculties (name, \"limit\", status) VALUES (?, ?, ?)", 
                java.sql.Statement.RETURN_GENERATED_KEYS);
        facultyStatement.setString(1, "Test Faculty");
        facultyStatement.setInt(2, 50);
        facultyStatement.setString(3, "OPEN");
        facultyStatement.executeUpdate();

        try (ResultSet keys = facultyStatement.getGeneratedKeys()) {
            if (keys.next()) {
                facultyId = keys.getInt(1);
            }
        }

        // Insert a test user
        PreparedStatement userStatement = connection.prepareStatement(
                "INSERT INTO users (login, password, role) VALUES (?, ?, ?)",
                java.sql.Statement.RETURN_GENERATED_KEYS);
        userStatement.setString(1, "testuser");
        userStatement.setString(2, "password");
        userStatement.setString(3, "USER");
        userStatement.executeUpdate();

        try (ResultSet keys = userStatement.getGeneratedKeys()) {
            if (keys.next()) {
                userId = keys.getInt(1);
            }
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testSaveApplicant() throws SQLException, ClassNotFoundException {
        // Create a test applicant
        Applicant applicant = new Applicant(
                "John Doe", 
                4.5, 
                facultyId, 
                userId, 
                Arrays.asList(90, 85, 95)
        );

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<com.srgbrl.laba.util.ConnectionManager> mockedConnectionManager = 
                mockStatic(com.srgbrl.laba.util.ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(() -> com.srgbrl.laba.util.ConnectionManager.open())
                    .thenReturn(connection);

            // Save the applicant
            applicantDao.save(applicant);
        }

        // Open a new connection to verify the results
        try (Connection newConnection = TestConnectionManager.open();
             PreparedStatement statement = newConnection.prepareStatement(
                     "SELECT * FROM applicants WHERE full_name = ? AND faculty_id = ? AND user_id = ?")) {

            statement.setString(1, "John Doe");
            statement.setInt(2, facultyId);
            statement.setInt(3, userId);
            ResultSet resultSet = statement.executeQuery();

            assertTrue(resultSet.next(), "Applicant should exist in database");
            assertEquals("John Doe", resultSet.getString("full_name"), "Name should match");
            assertEquals(4.5, resultSet.getDouble("average_grade"), 0.001, "Average grade should match");
            assertEquals(facultyId, resultSet.getInt("faculty_id"), "Faculty ID should match");
            assertEquals(userId, resultSet.getInt("user_id"), "User ID should match");
            assertEquals("90 85 95", resultSet.getString("results"), "Results should match");

            // Verify the sum calculation (0.1 * avgGrade + sum(results) / 3.0)
            float expectedSum = (float) (4.5 * 0.1 + (90 + 85 + 95) / 3.0);
            assertEquals(expectedSum, resultSet.getFloat("sum"), 0.001, "Sum should be calculated correctly");
        }
    }

    @Test
    void testFindByUserIdFacultyId() throws SQLException, ClassNotFoundException {
        // Insert a test applicant directly into the database
        PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO applicants (full_name, average_grade, faculty_id, user_id, results, sum) VALUES (?, ?, ?, ?, ?, ?)");
        insertStatement.setString(1, "Jane Smith");
        insertStatement.setDouble(2, 4.8);
        insertStatement.setInt(3, facultyId);
        insertStatement.setInt(4, userId);
        insertStatement.setString(5, "95 90 85");
        insertStatement.setFloat(6, (float) (4.8 * 0.1 + (95 + 90 + 85) / 3.0));
        insertStatement.executeUpdate();

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<com.srgbrl.laba.util.ConnectionManager> mockedConnectionManager = 
                mockStatic(com.srgbrl.laba.util.ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(() -> com.srgbrl.laba.util.ConnectionManager.open())
                    .thenReturn(connection);

            // Find the applicant
            Applicant foundApplicant = applicantDao.findByUserIdFacultyId(userId, facultyId);

            // Verify the applicant was found
            assertNotNull(foundApplicant, "Found applicant should not be null");
            assertEquals("Jane Smith", foundApplicant.getFullName(), "Name should match");
            assertEquals(4.8, foundApplicant.getAvgGrade(), 0.001, "Average grade should match");
            assertEquals(facultyId, foundApplicant.getFacultyId(), "Faculty ID should match");
            assertEquals(userId, foundApplicant.getUserId(), "User ID should match");
            assertEquals(Arrays.asList(95, 90, 85), foundApplicant.getResults(), "Results should match");

            // Test finding a non-existent applicant
            Applicant nonExistentApplicant = applicantDao.findByUserIdFacultyId(999, 999);
            assertNull(nonExistentApplicant, "Non-existent applicant should be null");
        }
    }

    @Test
    void testFindAllByFacultyId() throws SQLException, ClassNotFoundException {
        // Insert multiple test applicants directly into the database
        PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO applicants (full_name, average_grade, faculty_id, user_id, results, sum) VALUES (?, ?, ?, ?, ?, ?)");

        // Applicant 1
        insertStatement.setString(1, "Applicant 1");
        insertStatement.setDouble(2, 4.5);
        insertStatement.setInt(3, facultyId);
        insertStatement.setInt(4, userId);
        insertStatement.setString(5, "90 85 95");
        insertStatement.setFloat(6, (float) (4.5 * 0.1 + (90 + 85 + 95) / 3.0));
        insertStatement.executeUpdate();

        // Applicant 2 (different user ID)
        PreparedStatement userStatement = connection.prepareStatement(
                "INSERT INTO users (login, password, role) VALUES (?, ?, ?)",
                java.sql.Statement.RETURN_GENERATED_KEYS);
        userStatement.setString(1, "testuser2");
        userStatement.setString(2, "password2");
        userStatement.setString(3, "USER");
        userStatement.executeUpdate();

        int userId2;
        try (ResultSet keys = userStatement.getGeneratedKeys()) {
            keys.next();
            userId2 = keys.getInt(1);
        }

        insertStatement.setString(1, "Applicant 2");
        insertStatement.setDouble(2, 4.8);
        insertStatement.setInt(3, facultyId);
        insertStatement.setInt(4, userId2);
        insertStatement.setString(5, "95 90 85");
        insertStatement.setFloat(6, (float) (4.8 * 0.1 + (95 + 90 + 85) / 3.0));
        insertStatement.executeUpdate();

        // Use Mockito to mock the ConnectionManager.open() method
        try (MockedStatic<com.srgbrl.laba.util.ConnectionManager> mockedConnectionManager = 
                mockStatic(com.srgbrl.laba.util.ConnectionManager.class)) {

            // Make ConnectionManager.open() return our test connection
            mockedConnectionManager.when(() -> com.srgbrl.laba.util.ConnectionManager.open())
                    .thenReturn(connection);

            // Find all applicants for the faculty
            List<Applicant> applicants = applicantDao.findAllByFacultyId(facultyId);

            // Verify the applicants were found
            assertNotNull(applicants, "Applicants list should not be null");
            assertEquals(2, applicants.size(), "Should find 2 applicants");

            // Verify the applicants are sorted by sum in descending order
            assertTrue(applicants.get(0).getSum() >= applicants.get(1).getSum(), 
                    "Applicants should be sorted by sum in descending order");

            // Verify the first applicant (should be Applicant 2 with higher sum)
            Applicant applicant1 = applicants.stream()
                    .filter(a -> a.getFullName().equals("Applicant 2"))
                    .findFirst()
                    .orElse(null);
            assertNotNull(applicant1, "Applicant 2 should be found");
            assertEquals(4.8, applicant1.getAvgGrade(), 0.001, "Average grade should match");
            assertEquals(facultyId, applicant1.getFacultyId(), "Faculty ID should match");
            assertEquals(userId2, applicant1.getUserId(), "User ID should match");
            assertEquals(Arrays.asList(95, 90, 85), applicant1.getResults(), "Results should match");

            // Verify the second applicant
            Applicant applicant2 = applicants.stream()
                    .filter(a -> a.getFullName().equals("Applicant 1"))
                    .findFirst()
                    .orElse(null);
            assertNotNull(applicant2, "Applicant 1 should be found");
            assertEquals(4.5, applicant2.getAvgGrade(), 0.001, "Average grade should match");
            assertEquals(facultyId, applicant2.getFacultyId(), "Faculty ID should match");
            assertEquals(userId, applicant2.getUserId(), "User ID should match");
            assertEquals(Arrays.asList(90, 85, 95), applicant2.getResults(), "Results should match");

            // Test finding applicants for a non-existent faculty
            List<Applicant> nonExistentApplicants = applicantDao.findAllByFacultyId(999);
            assertTrue(nonExistentApplicants.isEmpty(), "Non-existent faculty should have no applicants");
        }
    }
}
