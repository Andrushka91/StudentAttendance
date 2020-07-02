package com.andrushka.studentattendance.model;

import com.google.type.Date;

public class Attendance {
    private String userId;
    private String degree;
    private String course;
    private String group;
    private String year;

    private String student;
    private String date;


    public Attendance(String userId, String degree, String course, String group, String year, String student, String date) {
        this.userId = userId;
        this.degree = degree;
        this.course = course;
        this.group = group;
        this.year = year;
        this.student = student;
        this.date = date;
    }

    public Attendance(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
