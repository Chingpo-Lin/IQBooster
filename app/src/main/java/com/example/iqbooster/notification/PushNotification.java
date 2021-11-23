package com.example.iqbooster.notification;

import com.google.gson.annotations.SerializedName;

public class PushNotification {

    @SerializedName("to")
    private String token;

    @SerializedName("notification")
    private NotificationData notification;

    public PushNotification(String token, NotificationData notification) {
        this.token = token;
        this.notification = notification;
    }
}
