package com.andrushka.studentattendance.model;

import java.util.ArrayList;

public class Groups {
    private String degreeName;
    private ArrayList<String>  groupList;

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public ArrayList<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<String> groupList) {
        this.groupList = groupList;
    }

    public Groups(String degreeName, ArrayList<String> groupList) {
        this.degreeName = degreeName;
        this.groupList = groupList;
    }
}
