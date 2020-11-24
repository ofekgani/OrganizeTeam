package com.example.organizeteam.MyService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.View;

import com.example.organizeteam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String CHANNEL_ID = "1";

    public MyFirebaseMessagingService() {

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            showNotification (remoteMessage);
        }

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken ( s );

    }

    private void showNotification(RemoteMessage remoteMessage)
    {
        Notification notification = new Notification ();
        notification.createNotificationChannel (this,CHANNEL_ID,"Join Request","Handle join request notifications",5);

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        NotificationCompat.Builder builder = new NotificationCompat.Builder ( this, CHANNEL_ID )
                .setSmallIcon ( R.drawable.bell_icon ).setContentTitle ( title ).setContentText ( body)
                .setPriority ( NotificationCompat.PRIORITY_DEFAULT );

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from ( this );
        notificationManagerCompat.notify (2,builder.build ());
    }
}