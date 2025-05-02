package org.srgbrl.laba1.service;

import com.srgbrl.laba.dao.UserDAO;
import com.srgbrl.laba.entity.User;

import java.util.Objects;

public class AuthService {

    static AuthService INSTANCE = new AuthService();

    private final UserDAO userDao = UserDAO.getInstance();

    private AuthService() {

    }

    public static AuthService getInstance() {
        return INSTANCE;
    }

    public User register(User user) {
        if (userDao.findByLogin(user.getLogin()) != null) {
            return null;
        } else {
            if (userDao.save(user) != null) {
                System.out.println("Successful registration");
                return user;
            } else {
                System.out.println("Registration failed, unexpected error");
                return null;
            }
        }
    }

    public User login(String login, String password) {
        var user = userDao.findByLogin(login);
        if (user != null) {
            if (Objects.equals(user.getPassword(), password)) {
                System.out.println("Successful login in " + user.getLogin() + " " + user.getRole());
                return user;
            }
        }
        System.out.println("Login failed");
        return null;
    }
}
