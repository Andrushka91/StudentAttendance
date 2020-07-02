package com.andrushka.studentattendance.model;

import java.util.ArrayList;

public class Degree {

    private String userId;
    private String name;
    private int duration;
    private ArrayList<String> courses;
    private ArrayList<String> groups;

    public Degree() {
    }


    public Degree(String userId, String name, int duration, ArrayList<String> groups, ArrayList<String> courses) {
        this.userId = userId;
        this.name = name;
        this.duration = duration;
        this.groups = groups;
        this.courses = courses;
    }



    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public ArrayList<String> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<String> courses) {
        this.courses = courses;
    }
}
