package com.myapp.organizeteam.MyService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Meeting;
import com.myapp.organizeteam.DataManagement.DataExtraction;

import static com.myapp.organizeteam.Core.Meeting.FLAG_MEETING_STARTED;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "AlarmReceiver.onReceive()", Toast.LENGTH_LONG).show();
        updateMeetingStatus(context, intent);
        sendNotification(context);

    }

    private void updateMeetingStatus(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("bundle");
        Meeting meeting = (Meeting) bundle.getSerializable(ConstantNames.MEETING);
        FirebaseApp.initializeApp(context);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.MEETINGS_PATH);
        mDatabase.child ( meeting.getTeamID() ).child(meeting.getKeyID()).child(ConstantNames.DATA_MEETING_STATUS).setValue ( FLAG_MEETING_STARTED );
    }

    private void sendNotification(Context context)
    {
        Notification notification = new Notification();
        notification.sendNotification(context,"2","Meeting get started!","");
    }

}