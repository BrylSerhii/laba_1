package com.srgbrl.laba.service;

import com.srgbrl.laba.dao.UserDAO;
import com.srgbrl.laba.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    static AuthService INSTANCE = new AuthService();

    private final UserDAO userDao = UserDAO.getInstance();

    private AuthService() {
    }

    public static AuthService getInstance() {
        return INSTANCE;
    }

    public User register(User user) {
        if (userDao.findByLogin(user.getLogin()) != null) {
            logger.warn("Registration failed: user with login '{}' already exists", user.getLogin());
            return null;
        } else {
            if (userDao.save(user) != null) {
                logger.info("User '{}' registered successfully", user.getLogin());
                return user;
            } else {
                logger.error("Registration failed for '{}': unexpected database error", user.getLogin());
                return null;
            }
        }
    }

    public User login(String login, String password) {
        var user = userDao.findByLogin(login);
        if (user != null) {
            if (Objects.equals(user.getPassword(), password)) {
                logger.info("Successful login: {} with role {}", user.getLogin(), user.getRole());
                return user;
            }
        }
        logger.warn("Login failed for user '{}'", login);
        return null;
    }
}
