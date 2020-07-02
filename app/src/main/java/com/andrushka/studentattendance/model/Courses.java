package com.andrushka.studentattendance.model;

import java.util.ArrayList;

public class Courses {

    private String degreeName;
    private ArrayList<String> coursesList;

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public ArrayList<String> getCoursesList() {
        return coursesList;
    }

    public void setCoursesList(ArrayList<String> coursesList) {
        this.coursesList = coursesList;
    }

    public Courses(String degreeName, ArrayList<String> coursesList) {
        this.degreeName = degreeName;
        this.coursesList = coursesList;
    }
}
