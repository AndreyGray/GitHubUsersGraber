package com.dron.githubusersgraber.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.dron.githubusersgraber.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import static com.dron.githubusersgraber.misc.Const.ACTION_NAME;
import static com.dron.githubusersgraber.misc.Const.CHANGES_COUNT_MESS;
import static com.dron.githubusersgraber.misc.Const.USER_ID_MESS;

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    private static final String TAG = "MyFirebaseService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        // Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            int userId = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("userId")));
            Integer changesCount = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("changesCount")));

            Intent intent = new Intent();
            intent.putExtra(USER_ID_MESS, userId);
            intent.putExtra(CHANGES_COUNT_MESS, changesCount);
            intent.setAction(ACTION_NAME);

            //Send extras to MainActivity
            sendBroadcast(intent);

            //Send Custom notification
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),userId);
        }
    }

    private void sendNotification(String title, String content,int id) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "GITHUB";

        //Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "GITHUB Notification",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("GITHUB Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID);

        //Customize notification
        notifBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.mlauncher)
                .setContentTitle(title)
                .setContentText(content)
                .setContentInfo("Info");

        //Send custom notification
        notificationManager.notify(id, notifBuilder.build());

    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}