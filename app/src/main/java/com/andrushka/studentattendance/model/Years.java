package com.andrushka.studentattendance.model;

import java.util.ArrayList;

public class Years {

    private String degreeName;
    private int duration;
    private ArrayList<String> yearList;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Years(String degreeName, ArrayList<String> yearList) {
        this.degreeName = degreeName;
        this.yearList = yearList;
    }

    public Years(String degreeName, int duration) {
        this.degreeName = degreeName;
        this.duration = duration;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public ArrayList<String> getYearList() {
        return yearList;
    }

    public void setYearList(ArrayList<String> yearList) {
        this.yearList = yearList;
    }
}
