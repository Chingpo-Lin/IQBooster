package com.example.iqbooster.model;

public class AdapterPost {
    private String randomID;
    private String author;

    public AdapterPost() {
    }

    public AdapterPost(String randomID, String author) {
        this.randomID = randomID;
        this.author = author;
    }

    public String getRandomID() {
        return randomID;
    }

    public void setRandomID(String randomID) {
        this.randomID = randomID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
