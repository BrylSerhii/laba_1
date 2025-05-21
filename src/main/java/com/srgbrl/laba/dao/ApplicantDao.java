package com.srgbrl.laba.dao;

import com.srgbrl.laba.entity.Applicant;
import com.srgbrl.laba.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicantDao {

    private static final Logger logger = LoggerFactory.getLogger(ApplicantDao.class);

    static ApplicantDao INSTANCE = new ApplicantDao();

    private ApplicantDao() {
    }

    public static ApplicantDao getInstance() {
        return INSTANCE;
    }

    public List<Applicant> findAllByFacultyId(Integer id) {
        List<Applicant> applicants = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(
                     "SELECT * FROM applicants WHERE faculty_id = ? ORDER BY sum DESC")) {
            statement.setInt(1, id);
            var rs = statement.executeQuery();
            while (rs.next()) {
                applicants.add(new Applicant(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getDouble("average_grade"),
                        rs.getInt("faculty_id"),
                        rs.getInt("user_id"),
                        Arrays.stream(rs.getString("results").split(" "))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList()),
                        rs.getFloat("sum")));
            }
            logger.info("Found {} applicants for faculty_id={}", applicants.size(), id);
        } catch (SQLException e) {
            logger.error("Database error in findAllByFacultyId({})", id, e);
        } catch (ClassNotFoundException e) {
            logger.error("Unexpected error in findAllByFacultyId({})", id, e);
        }
        return applicants;
    }

    public Applicant findByUserIdFacultyId(Integer userId, Integer facultyId) {
        Applicant applicant = null;
        try (Connection connection = ConnectionManager.open()) {
            var statement = connection.prepareStatement(
                    "SELECT * FROM applicants WHERE user_id = ? AND faculty_id = ?");
            statement.setInt(1, userId);
            statement.setInt(2, facultyId);
            var rs = statement.executeQuery();
            if (rs.next()) {
                applicant = new Applicant(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getDouble("average_grade"),
                        rs.getInt("faculty_id"),
                        rs.getInt("user_id"),
                        Arrays.stream(rs.getString("results").split(" "))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList()),
                        rs.getFloat("sum"));
                logger.info("Found applicant for user_id={} and faculty_id={}", userId, facultyId);
            } else {
                logger.warn("No applicant found for user_id={} and faculty_id={}", userId, facultyId);
            }
        } catch (SQLException e) {
            logger.error("Database error in findByUserIdFacultyId({}, {})", userId, facultyId, e);
        } catch (ClassNotFoundException e) {
            logger.error("Unexpected error in findByUserIdFacultyId({}, {})", userId, facultyId, e);
        }
        return applicant;
    }

    public void save(Applicant applicant) {
        try (Connection connection = ConnectionManager.open()) {
            var statement = connection.prepareStatement(
                    "INSERT INTO applicants(full_name, average_grade, faculty_id, results, user_id, sum) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, applicant.getFullName());
            statement.setDouble(2, applicant.getAvgGrade());
            statement.setInt(3, applicant.getFacultyId());
            statement.setString(4, applicant.getResults().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" ")));
            statement.setInt(5, applicant.getUserId());
            float sum = (float) (applicant.getAvgGrade() * 0.1 +
                    applicant.getResults().stream().mapToInt(Integer::intValue).sum() / 3.0);
            statement.setFloat(6, sum);
            statement.executeUpdate();
            logger.info("Applicant '{}' successfully saved (faculty_id={}, user_id={})", applicant.getFullName(), applicant.getFacultyId(), applicant.getUserId());
        } catch (SQLException e) {
            logger.error("Database error while saving applicant '{}'", applicant.getFullName(), e);
        } catch (ClassNotFoundException e) {
            logger.error("Unexpected error while saving applicant '{}'", applicant.getFullName(), e);
        }
    }
}
