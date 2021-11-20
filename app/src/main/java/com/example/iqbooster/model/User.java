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
    public String following_users;
    public String followers_users;
    public String profile_image;
    public String like_posts;
    public String collect_posts;

    public User() {
    }

    public User(String username, String name, String email, String uid, String location, String my_posts, String following_users, String followers_users, String profile_image) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.location = location;
        this.my_posts = my_posts;
        this.following_users = following_users;
        this.followers_users = followers_users;
        this.profile_image = profile_image;
        this.like_posts = "";
        this.collect_posts = "";
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

    public String getFollowing_users() {
        return following_users;
    }

    public void setFollowing_users(String following_users) {
        this.following_users = following_users;
    }

    public String getFollowers_users() {
        return followers_users;
    }

    public void setFollowers_users(String followers_users) {
        this.followers_users = followers_users;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getLike_posts() {
        return like_posts;
    }

    public void setLike_posts(String like_posts) {
        this.like_posts = like_posts;
    }

    public String getCollect_posts() {
        return collect_posts;
    }

    public void setCollect_posts(String collect_posts) {
        this.collect_posts = collect_posts;
    }
}
