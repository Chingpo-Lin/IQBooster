package com.example.iqbooster.model;

public class MyPostModel {
    String randomID;
    long timestamp;

    public MyPostModel() {
    }

    public MyPostModel(String randomID, long timestamp) {
        this.randomID = randomID;
        this.timestamp = timestamp;
    }

    public String getRandomID() {
        return randomID;
    }

    public void setRandomID(String randomID) {
        this.randomID = randomID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
