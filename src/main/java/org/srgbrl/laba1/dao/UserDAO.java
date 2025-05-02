package org.srgbrl.laba1.dao;

import com.srgbrl.laba.entity.User;
import com.srgbrl.laba.util.ConnectionManager;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class UserDAO {

    static UserDAO INSTANCE = new UserDAO();

    private UserDAO() {

    }

    public static UserDAO getInstance() {
        return INSTANCE;
    }

    public User findByLogin(String login) {
        try (var connection = ConnectionManager.open()) {
            var statement = connection.prepareStatement("SELECT * from users where login = ?;");
            statement.setString(1, login);
            var res = statement.executeQuery();
            if (res.next()) {
                System.out.println(login + " exists");
                return new User(res.getInt("id"),res.getString("login"), res.getString("password"), res.getString("role"));
            } else {
                System.out.println(login + " not exist");
                return null;
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("unexpected error in findByLogin");
            return null;
        }
    }

    public User save(User user) {
        try (var connection = ConnectionManager.open()) {
            var statement = connection.prepareStatement("INSERT into users (login, password, role) values (?,?,?)",RETURN_GENERATED_KEYS);
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            var res = statement.executeUpdate();
            System.out.println("Successful save of user " + user.getLogin() + " " + user.getRole());
            user.setId(res);
            return user;
        } catch (ClassNotFoundException e) {
            System.out.println("unexpected error in user save");
            return null;
        } catch (SQLException e) {
            System.out.println("error with database");
            throw new RuntimeException(e);
        }
    }
}
