package com.example.iqbooster.notification;

import static com.example.iqbooster.notification.FirebaseConstants.SERVER_KEY;
import static com.example.iqbooster.notification.FirebaseConstants.CONTENT_TYPE;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationAPI {
    @Headers({"Authorization: key=" + SERVER_KEY, "Content-Type:" + CONTENT_TYPE})
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body PushNotification notification);
}
