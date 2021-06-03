package com.myapp.organizeteam.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;

import com.myapp.organizeteam.Adapters.MeetingsListAdapter;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Meeting;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MeetingListActivity extends AppCompatActivity {

    ActivityTransition activityTransition;
    DataExtraction dataExtraction;

    ListView lv_meetings;
    Toolbar toolbar;

    MeetingsListAdapter adapter;

    Intent intent;
    Team team;
    ArrayList<String> meetingsID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_list);

        activityTransition = new ActivityTransition();
        dataExtraction = new DataExtraction();

        lv_meetings = findViewById(R.id.lv_meetings);

        toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        intent = getIntent();
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
        meetingsID = (ArrayList<String>) intent.getSerializableExtra("meetingsID");

        dataExtraction.getDeletedMeetingsByKeys(team.getKeyID(), meetingsID, new ISavable() {
            @Override
            public void onDataRead(Object save) {
                setAdapter((ArrayList<Meeting>) save);
            }
        });
    }

    private void setAdapter(ArrayList<Meeting> meetingsList) {
        if(meetingsList == null) return;
        adapter = new MeetingsListAdapter(this,R.layout.adapter_meetings_list,meetingsList);
        lv_meetings.setAdapter ( adapter );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}