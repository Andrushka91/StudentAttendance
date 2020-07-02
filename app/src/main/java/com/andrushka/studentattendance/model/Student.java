package com.andrushka.studentattendance.model;

public class Student {

    private String userId;
    private String name;
    private String fingerPrint;
    private String degree;
    private String course;
    private String group;
    private String year;

    public Student(String name, String fingerPrint, String degree, String course, String group, String year) {
        this.name = name;
        this.fingerPrint = fingerPrint;
        this.degree = degree;
        this.course = course;
        this.group = group;
        this.year = year;
    }


    public Student() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
