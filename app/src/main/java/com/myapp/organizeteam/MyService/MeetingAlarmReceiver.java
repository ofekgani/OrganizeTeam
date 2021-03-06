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
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;

import static com.myapp.organizeteam.Core.Meeting.FLAG_MEETING_STARTED;

public class MeetingAlarmReceiver extends BroadcastReceiver {

    Bundle bundle;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "AlarmReceiver.onReceive()", Toast.LENGTH_LONG).show();

        bundle = intent.getBundleExtra("bundle");

        Meeting meeting = (Meeting) bundle.getSerializable(ConstantNames.MEETING);
        updateMeetingStatus(context, meeting);

        sendNotification(context,"2","Meeting get started!","");

    }

    private void updateMeetingStatus(Context context, final Meeting meeting) {

        FirebaseApp.initializeApp(context);
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.MEETINGS_PATH);
        DataExtraction dataExtraction = new DataExtraction();
        dataExtraction.hasChild(ConstantNames.MEETINGS_PATH, meeting.getTeamID(), meeting.getKeyID(), new ISavable() {
            @Override
            public void onDataRead(Object save) {
                if((boolean)save)
                {
                    mDatabase.child (meeting.getTeamID()).child(meeting.getKeyID()).child(ConstantNames.DATA_MEETING_STATUS).setValue ( FLAG_MEETING_STARTED );
                }
            }
        });
    }

    private void sendNotification(Context context,String channel,String title, String description)
    {
        Notification notification = new Notification();
        notification.sendNotification(context,channel,title,description);
    }

}