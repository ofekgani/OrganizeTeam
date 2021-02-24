package com.myapp.organizeteam.MyService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Meeting;

import static com.myapp.organizeteam.Core.Meeting.FLAG_MEETING_STARTED;

public class AlarmReceiver extends BroadcastReceiver {

    String channel, title, description;
    Bundle bundle;

    public AlarmReceiver(String channel,String title, String description)
    {
        this.channel = channel;
        this.title = title;
        this.description = description;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "AlarmReceiver.onReceive()", Toast.LENGTH_LONG).show();

        bundle = intent.getBundleExtra("bundle");

        Meeting meeting = (Meeting) bundle.getSerializable(ConstantNames.MEETING);
        updateMeetingStatus(context, meeting);

        sendNotification(context,"2","Meeting get started!","");

    }

    private void updateMeetingStatus(Context context, Meeting meeting) {

        FirebaseApp.initializeApp(context);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.MEETINGS_PATH);
        mDatabase.child (meeting.getTeamID()).child(meeting.getKeyID()).child(ConstantNames.DATA_MEETING_STATUS).setValue ( FLAG_MEETING_STARTED );
    }

    private void sendNotification(Context context,String channel,String title, String description)
    {
        Notification notification = new Notification();
        notification.sendNotification(context,channel,title,description);
    }

}