package com.srgbrl.laba.dao;

import com.srgbrl.laba.entity.Faculty;
import com.srgbrl.laba.entity.Status;
import com.srgbrl.laba.util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacultyDao {

    static FacultyDao INSTANCE = new FacultyDao();

    private FacultyDao() {

    }

    public static FacultyDao getInstance() {
        return INSTANCE;
    }

    public List<Faculty> findAll() {
        List<Faculty> faculties = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
             var statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM faculties");
            while (rs.next()) {
                faculties.add(new Faculty(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("limit"),
                        Status.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            System.out.println("db error in faculty findAll");;
        } catch (ClassNotFoundException e) {
            System.out.println("unexpected error");
        }
        return faculties;
    }

    public Faculty findById(Integer id) {
        Faculty faculty = null;
        try (Connection connection = ConnectionManager.open()){
            var statement = connection.prepareStatement("SELECT * FROM faculties where id = ?");
            statement.setInt(1,id);
            var rs = statement.executeQuery();
            if (rs.next()) {
                faculty = new Faculty(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("limit"),
                        Status.valueOf(rs.getString("status")));
                System.out.println("successfully found faculty with id " + id);
            } else {
                System.out.println("no faculty with id" + id);
                return null;
            }
        } catch (SQLException e) {
            System.out.println("db error in faculty findAll");;
        } catch (ClassNotFoundException e) {
            System.out.println("unexpected error");
        }
        return faculty;
    }

    public void save(Faculty faculty) throws SQLException, ClassNotFoundException {
        var connection = ConnectionManager.open();
        var statement = connection.prepareStatement("INSERT into faculties values (?,?,?)");
        statement.setString(1, faculty.getName());
        statement.setInt(2, faculty.getLimit());
        statement.setString(3, faculty.getStatus().name());
        statement.executeQuery();
        connection.close();
        System.out.println("Successful creation");
    }
}
