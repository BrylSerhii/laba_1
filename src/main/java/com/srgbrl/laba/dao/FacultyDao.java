package com.srgbrl.laba.dao;

import com.srgbrl.laba.entity.Faculty;
import com.srgbrl.laba.entity.Status;
import com.srgbrl.laba.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacultyDao {

    private static final Logger logger = LoggerFactory.getLogger(FacultyDao.class);
    static FacultyDao INSTANCE = new FacultyDao();

    private FacultyDao() {}

    public static FacultyDao getInstance() {
        return INSTANCE;
    }

    public List<Faculty> findAll() {
        List<Faculty> faculties = new ArrayList<>();
        try (Connection connection = ConnectionManager.open(); var statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM faculties ORDER BY id;");
            while (rs.next()) {
                faculties.add(new Faculty(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("limit"),
                        Status.valueOf(rs.getString("status"))
                ));
            }
            logger.info("Found {} faculties", faculties.size());
        } catch (SQLException e) {
            logger.error("Database error in findAll()", e);
        } catch (ClassNotFoundException e) {
            logger.error("Driver class not found in findAll()", e);
        }
        return faculties;
    }

    public Faculty findById(Integer id) {
        Faculty faculty = null;
        try (Connection connection = ConnectionManager.open()) {
            var statement = connection.prepareStatement("SELECT * FROM faculties WHERE id = ?");
            statement.setInt(1, id);
            var rs = statement.executeQuery();
            if (rs.next()) {
                faculty = new Faculty(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("limit"),
                        Status.valueOf(rs.getString("status"))
                );
                logger.info("Faculty found with id={}", id);
            } else {
                logger.warn("No faculty found with id={}", id);
            }
        } catch (SQLException e) {
            logger.error("Database error in findById({})", id, e);
        } catch (ClassNotFoundException e) {
            logger.error("Driver class not found in findById({})", id, e);
        }
        return faculty;
    }

    public void save(Faculty faculty) {
        try (var connection = ConnectionManager.open()) {
            var statement = connection.prepareStatement("INSERT INTO faculties(name, \"limit\", status) VALUES (?, ?, ?)");
            statement.setString(1, faculty.getName());
            statement.setInt(2, faculty.getLimit());
            statement.setString(3, faculty.getStatus().name());
            statement.executeUpdate();
            logger.info("Faculty '{}' saved successfully", faculty.getName());
        } catch (SQLException e) {
            logger.error("Database error in save() for faculty '{}'", faculty.getName(), e);
        } catch (ClassNotFoundException e) {
            logger.error("Driver class not found in save() for faculty '{}'", faculty.getName(), e);
        }
    }

    public void closeFaculty(Faculty faculty) throws SQLException {
        try (Connection connection = ConnectionManager.open()) {
            var statement = connection.prepareStatement("UPDATE faculties SET status = 'CLOSED' WHERE id = ?");
            statement.setInt(1, faculty.getId());
            statement.executeUpdate();
            logger.info("Faculty '{}' closed successfully", faculty.getName());
        } catch (ClassNotFoundException e) {
            logger.error("Driver class not found in closeFaculty() for faculty '{}'", faculty.getName(), e);
        }
    }
}
