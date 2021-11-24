package com.example.iqbooster.notification;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.iqbooster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import okhttp3.ResponseBody;
import retrofit2.Callback;

public class FirebaseUtil {

    // Update database with device id for FCM
    public static void updateDeviceId(Context context, String TAG) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        String msg = context.getString(R.string.msg_token, token);
                        Log.d(TAG, msg);

                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                        databaseRef.child(context.getResources().getString(R.string.db_users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(context.getResources().getString(R.string.db_device_id)).setValue(token);
                    }
                });
    }

    private static void sendSingleNotification(String device_id, String title, String body, String TAG) {
        Log.d(TAG, device_id);

        PushNotification notification = new PushNotification(device_id, new NotificationData(title, body));

        NotificationAPI apiService = NotificationClient.getClient().create(NotificationAPI.class);
        retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendNotification(notification);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d(TAG, "Successfully sent notification using retrofit.");
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    public static void sendSingleNotification(Context context, String destUID, String title, String body, String TAG) {
        Log.d(TAG, "sending notification");
        Log.d(TAG, context.getResources().getString(R.string.db_users));
        Log.d(TAG, context.getResources().getString(R.string.db_device_id));

        DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference().child(context.getResources().getString(R.string.db_users));
        mUsers.child(destUID).child(context.getString(R.string.db_device_id)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String destDeviceId = snapshot.getValue(String.class);
                    if (destDeviceId != null) {
                        sendSingleNotification(destDeviceId, title, body, TAG);
                    } else {
                        Log.d(TAG, "no destination device id");
                    }
                } else {
                    Log.d(TAG, "device id does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
