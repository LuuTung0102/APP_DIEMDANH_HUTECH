package com.example.test.database;

public class Student {
    private String mssv;
    private String name;

    public Student() {
    }

    public Student(String mssv, String name) {
        this.mssv = mssv;
        this.name = name;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
