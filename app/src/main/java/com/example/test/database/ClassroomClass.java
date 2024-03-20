package com.example.test.database;

public class ClassroomClass {
    String code;

    String name;

    public ClassroomClass() {
    }

    public ClassroomClass(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
