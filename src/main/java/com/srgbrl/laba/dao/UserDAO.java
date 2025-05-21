package com.srgbrl.laba.dao;

import com.srgbrl.laba.entity.User;
import com.srgbrl.laba.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    static UserDAO INSTANCE = new UserDAO();

    private UserDAO() {}

    public static UserDAO getInstance() {
        return INSTANCE;
    }

    public User findByLogin(String login) {
        try (var connection = ConnectionManager.open()) {
            var statement = connection.prepareStatement("SELECT * FROM users WHERE login = ?;");
            statement.setString(1, login);
            var res = statement.executeQuery();
            if (res.next()) {
                logger.info("User '{}' found", login);
                return new User(res.getInt("id"), res.getString("login"), res.getString("password"), res.getString("role"));
            } else {
                logger.info("User '{}' not found", login);
                return null;
            }
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Error finding user by login '{}'", login, e);
            return null;
        }
    }

    public User save(User user) {
        try (var connection = ConnectionManager.open()) {
            var statement = connection.prepareStatement("INSERT INTO users (login, password, role) VALUES (?, ?, ?)", RETURN_GENERATED_KEYS);
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.executeUpdate();
            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
            logger.info("User '{}' with role '{}' saved successfully", user.getLogin(), user.getRole());
            return user;
        } catch (ClassNotFoundException e) {
            logger.error("Driver class not found while saving user '{}'", user.getLogin(), e);
            return null;
        } catch (SQLException e) {
            logger.error("Database error while saving user '{}'", user.getLogin(), e);
            throw new RuntimeException(e);
        }
    }
}
