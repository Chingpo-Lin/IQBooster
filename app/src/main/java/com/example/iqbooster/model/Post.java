package com.example.iqbooster.model;

public class Post {
    private String randomID;
    private String title;
    private String lower_case_title;
    private String subTitle;
    private String body;
    private String author;
    private String date;
    private long timestamp;
    private long like_counts;
    private Tags tags;
    private String thumbnail_image;
    private boolean isLiked;
    private boolean isCollected;

    public Post() {
    }

    public Post(String randomID, String title, String lower_case_title, String subTitle, String body, String author, String date, long timestamp, Tags tags, String thumbnail_image) {
        this.randomID = randomID;
        this.title = title;
        this.lower_case_title = lower_case_title;
        this.subTitle = subTitle;
        this.body = body;
        this.author = author;
        this.date = date;
        this.timestamp = timestamp;
        this.like_counts = 0;
        this.tags = tags;
        this.thumbnail_image = thumbnail_image;
        this.isLiked = false;
        this.isCollected = false;
    }

    public String getRandomID() {
        return randomID;
    }

    public void setRandomID(String randomID) {
        this.randomID = randomID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public long getLike_counts() {
        return like_counts;
    }

    public void setLike_counts(long like_counts) {
        this.like_counts = like_counts;
    }

    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }

    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public void setThumbnail_image(String thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public String getLower_case_title() {
        return lower_case_title;
    }

    public void setLower_case_title(String lower_case_title) {
        this.lower_case_title = lower_case_title;
    }
}