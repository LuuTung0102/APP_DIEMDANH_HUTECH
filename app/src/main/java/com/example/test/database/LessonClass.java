package com.example.test.database;

public class LessonClass {
    private String id;
    private String name;
    private String date;
    private boolean status;

    public LessonClass() {
    }

    public LessonClass(String id, String name, String date, boolean status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
