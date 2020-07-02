package com.andrushka.studentattendance.model;

public class ProfileInformation {

    private String title;
    private String content;
    private String imageUrl;

    public ProfileInformation(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public ProfileInformation(){
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
