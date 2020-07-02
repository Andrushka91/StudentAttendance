package com.andrushka.studentattendance.model;

public class User {

    private String username;
    private String email;
    private String imageUrl;
    private String userId;

    public User(){

    }

    public User(String username, String email, String imageUrl, String userId) {
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
