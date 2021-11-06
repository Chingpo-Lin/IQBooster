package com.example.iqbooster.model;

public class Post {
    private String Title;
    private String subTitle;
    private String body;
    private String author;
    private String date;
    private long timestamp;

    public Post() {
    }

    public Post(String title, String subTitle, String body, String author, String date, long timestamp) {
        Title = title;
        this.subTitle = subTitle;
        this.body = body;
        this.author = author;
        this.date = date;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
