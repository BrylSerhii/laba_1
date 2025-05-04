package com.srgbrl.laba.dao;

import com.srgbrl.laba.entity.Applicant;
import com.srgbrl.laba.util.ConnectionManager;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicantDao {

    static ApplicantDao INSTANCE = new ApplicantDao();

    private ApplicantDao() {

    }

    public static ApplicantDao getInstance() {
        return INSTANCE;
    }

    public List<Applicant> findAllByFacultyId(Integer id) {
        List<Applicant> applicants = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement("SELECT * FROM applicants where faculty_id = ? order by sum DESC ;")) {
            statement.setInt(1, id);
            var rs = statement.executeQuery();
            while (rs.next()) {
                applicants.add(new Applicant(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getDouble("average_grade"),
                        rs.getInt("faculty_id"),
                        rs.getInt("user_id"),
                        Arrays.stream(rs.getString("results").split(" ")).map(Integer::parseInt).collect(Collectors.toList()),
                        rs.getFloat("sum")));
            }
            System.out.println("applicants found!");
        } catch (SQLException e) {
            System.out.println("db error in applicant findAll by faculty");
            ;
        } catch (ClassNotFoundException e) {
            System.out.println("unexpected error");
        }
        return applicants;
    }

    public Applicant findByUserIdFacultyId(Integer userId, Integer facultyId) {
        Applicant applicant = null;
        try (Connection connection = ConnectionManager.open()) {
            var statement = connection.prepareStatement("SELECT * FROM applicants where user_id = ? and faculty_id = ?");
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
                        Arrays.stream(rs.getString("results").split(" ")).map(Integer::parseInt).collect(Collectors.toList()),
                        rs.getFloat("sum"));
                System.out.println("successfully found applicant with user faculty " + userId + " " + facultyId);
            } else {
                System.out.println("no applicant with user id " + userId + " and faculty id " + facultyId);
                return null;
            }
        } catch (SQLException e) {
            System.out.println("db error in applicant find by user faculty");
            ;
        } catch (ClassNotFoundException e) {
            System.out.println("unexpected error");
        }
        return applicant;
    }

    public void save(Applicant applicant) {
        try (Connection connection = ConnectionManager.open()) {
            var statement = connection.prepareStatement("INSERT into applicants(full_name, average_grade, faculty_id, results, user_id,sum) values (?,?,?,?,?,?)");
            statement.setString(1, applicant.getFullName());
            statement.setDouble(2, applicant.getAvgGrade());
            statement.setInt(3, applicant.getFacultyId());
            statement.setString(4, applicant.getResults().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" ")));
            statement.setInt(5, applicant.getUserId());
            statement.setFloat(6, (float)
                    (applicant.getAvgGrade() * 0.1f
                            + applicant.getResults().stream()
                            .mapToInt(Integer::intValue)
                            .sum() / 3.0));
            statement.executeUpdate();
            connection.close();
            System.out.println("Successful application creation");
        } catch (SQLException e) {
            System.out.println("db error in applicant save");
        } catch (ClassNotFoundException e) {
            System.out.println("unexpected error");
        }

    }
}
