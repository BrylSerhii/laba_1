package org.srgbrl.laba1.entity;

import java.util.List;

public class Applicant {

    private Integer id;

    private String fullName;

    private Double avgGrade;

    private Integer facultyId;

    private List<Integer> results;

    private Integer userId;

    private Float sum;

    public Applicant() {
    }

    public Applicant(Integer id, String fullName, Double averageGrade, Integer facultyId, Integer userId, List<Integer> subjectScores, Float sum) {
        this.id = id;
        this.fullName = fullName;
        this.avgGrade = averageGrade;
        this.facultyId = facultyId;
        this.results = subjectScores;
        this.userId = userId;
        this.sum = sum;
    }

    public Applicant(String fullName, Double averageGrade, Integer facultyId, Integer userId, List<Integer> subjectScores) {
        this.fullName = fullName;
        this.avgGrade = averageGrade;
        this.facultyId = facultyId;
        this.results = subjectScores;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Double getAvgGrade() {
        return avgGrade;
    }

    public void setAvgGrade(Double avgGrade) {
        this.avgGrade = avgGrade;
    }

    public Integer getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Integer facultyId) {
        this.facultyId = facultyId;
    }

    public List<Integer> getResults() {
        return results;
    }

    public void setResults(List<Integer> results) {
        this.results = results;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Float getSum() {
        return sum;
    }

    public void setSum(Float sum) {
        this.sum = sum;
    }
}
