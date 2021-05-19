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
import com.myapp.organizeteam.Core.Mission;

import static com.myapp.organizeteam.Core.Meeting.FLAG_MEETING_STARTED;

public class TaskAlarmReceiver extends BroadcastReceiver {

    Bundle bundle;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "AlarmReceiver.onReceive()", Toast.LENGTH_LONG).show();

        bundle = intent.getBundleExtra("bundle");

        Mission mission = (Mission) bundle.getSerializable(ConstantNames.TASK);
        updateTaskStatus(context, mission);

    }

    private void updateTaskStatus(Context context, Mission mission) {

        FirebaseApp.initializeApp(context);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.TASK_PATH);
        mDatabase.child (mission.getTeamID()).child(mission.getKeyID()).child(ConstantNames.DATA_MEETING_STATUS).setValue ( Mission.TIME_IS_UP );
    }
}