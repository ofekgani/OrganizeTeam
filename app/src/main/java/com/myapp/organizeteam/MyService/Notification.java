package com.myapp.organizeteam.MyService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.myapp.organizeteam.R;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notification {

    public void createNotificationChannel(Context context, String channelID, String title, String description, int importance)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
        {

            NotificationChannel channel = new NotificationChannel ( channelID , title , importance);
            channel.setDescription ( description );

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel ( channel );
        }
    }

    public APIService createClient()
    {
        return Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    public void sendNotification(Context context,String channelID,String title,String body)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder ( context, channelID )
                .setSmallIcon ( R.drawable.bell_icon ).setContentTitle ( title ).setContentText ( body)
                .setPriority ( NotificationCompat.PRIORITY_DEFAULT );

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from ( context );
        notificationManagerCompat.notify (2,builder.build ());
    }
}
