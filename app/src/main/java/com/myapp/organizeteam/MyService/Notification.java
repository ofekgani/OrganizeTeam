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

import androidx.annotation.NonNull;

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

    public void myRegistrationToken(final Context context)
    {
        FirebaseInstanceId.getInstance().getInstanceId ().addOnCompleteListener ( new OnCompleteListener<InstanceIdResult> () {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful ())
                {
                    String token = task.getResult ().getToken ();
                    Toast.makeText ( context,token,Toast.LENGTH_LONG ).show ();
                    Log.d ("Token",token);
                }
            }
        } );

    }

    public APIService createClient()
    {
        return Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }
}
