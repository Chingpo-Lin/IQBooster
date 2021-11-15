package com.example.iqbooster.model;

public class Comment {
    private String commentID;
    private String authorID;
    private String authorDisplayName;
    private String date;
    private long timestamp;
    private String commentBody;

    public Comment() {
    }

    public Comment(String commentID, String authorID, String authorDisplayName, String date, long timestamp, String commentBody) {
        this.commentID = commentID;
        this.authorID = authorID;
        this.authorDisplayName = authorDisplayName;
        this.date = date;
        this.timestamp = timestamp;
        this.commentBody = commentBody;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
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

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }
}
