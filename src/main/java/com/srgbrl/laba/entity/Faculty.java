package com.srgbrl.laba.entity;

public class Faculty {

    private Integer id;

    private String name;

    private Integer limit;

    private Status status;

    public Faculty() {
    }

    public Faculty(Integer id, String name, Integer limit, Status status) {
        this.id = id;
        this.name = name;
        this.limit = limit;
        this.status = status;
    }

    public Faculty(String name, Integer limit, Status status) {
        this.name = name;
        this.limit = limit;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
