package com.example.iqbooster.model;

public class AdapterUser {
    private String username;
    private String name;
    private String email;
    private String uid;
    private String location;
    private boolean changeToFollowing;

    public AdapterUser() {
    }

    public AdapterUser(String username, String name, String email, String uid, String location) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.location = location;
        this.changeToFollowing = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean customCTF() {
        return changeToFollowing;
    }

    public void customCTF(boolean changeToFollowing) {
        this.changeToFollowing = changeToFollowing;
    }
}
