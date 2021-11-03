package com.example.iqbooster.model;

import android.content.Context;
import android.security.keystore.StrongBoxUnavailableException;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class User {
    public String username;
    public String name;
    public String email;
    public String uid;
    public String location;
    public String my_posts;
    public String collect_posts;
    public String like_posts;
    public String following_users;
    public String follower_users;

    public User() {
    }

    public User(String username, String name, String email, String uid, String location, String my_posts, String collect_posts, String like_posts, String following_users, String follower_users) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.location = location;
        this.my_posts = my_posts;
        this.collect_posts = collect_posts;
        this.like_posts = like_posts;
        this.following_users = following_users;
        this.follower_users = follower_users;
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

    public String getMy_posts() {
        return my_posts;
    }

    public void setMy_posts(String my_posts) {
        this.my_posts = my_posts;
    }

    public String getCollect_posts() {
        return collect_posts;
    }

    public void setCollect_posts(String collect_posts) {
        this.collect_posts = collect_posts;
    }

    public String getLike_posts() {
        return like_posts;
    }

    public void setLike_posts(String like_posts) {
        this.like_posts = like_posts;
    }

    public String getFollowing_users() {
        return following_users;
    }

    public void setFollowing_users(String following_users) {
        this.following_users = following_users;
    }

    public String getFollower_users() {
        return follower_users;
    }

    public void setFollower_users(String follower_users) {
        this.follower_users = follower_users;
    }
}
