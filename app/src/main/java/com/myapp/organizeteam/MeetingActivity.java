package com.myapp.organizeteam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Meeting;
import com.myapp.organizeteam.Core.Submitter;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;

public class MeetingActivity extends AppCompatActivity {

    DataExtraction dataExtraction;

    TextView tv_meetingName, tv_meetingDate, tv_meetingDescription;
    Button btn_arrivalConfirmation;
    ListView lv_arrivals, lv_rejects;
    Toolbar toolbar;

    Intent intent;
    Meeting meeting;
    Team team;
    User user;

    boolean arrival;
    private Submitter submitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        dataExtraction = new DataExtraction();

        btn_arrivalConfirmation = findViewById(R.id.btn_confirmArrival);
        tv_meetingName = findViewById(R.id.tv_meetingName);
        tv_meetingDate = findViewById(R.id.tv_meetingDate);
        tv_meetingDescription = findViewById(R.id.tv_meetingDescription);

        toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        intent = getIntent();
        meeting = (Meeting) intent.getSerializableExtra(ConstantNames.MEETING);
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
        user = (User) intent.getSerializableExtra(ConstantNames.USER);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(""+meeting.getMeetingName());

        tv_meetingName.setText(""+meeting.getMeetingName());
        tv_meetingDate.setText(""+meeting.getDate());
        tv_meetingDescription.setText(""+meeting.getMeetingDescription());

        btn_arrivalConfirmation.setVisibility(View.GONE);
        dataExtraction.getSubmitter(ConstantNames.MEETINGS_PATH,team.getKeyID(), meeting.getKeyID(), user.getKeyID(), new ISavable() {
            @Override
            public void onDataRead(Object save) {
                submitter = (Submitter) save;
                if(meeting.isArrivalConfirmation())
                {
                    btn_arrivalConfirmation.setVisibility(View.VISIBLE);

                }

                if(submitter.getConfirmStatus() == Meeting.NO_ANSWER) {
                    btn_arrivalConfirmation.setText("Confirm arrival");
                    arrival = true;
                }
                else if(submitter.getConfirmStatus() == Meeting.ARRIVAL_CONFIRMATION)
                {
                    btn_arrivalConfirmation.setText("Cancel arrival");
                    arrival = false;
                }
            }
        });
    }

    public void oc_confirmArrival(View view) {
        if(arrival)
        {
            submitter.setConfirm(Meeting.ARRIVAL_CONFIRMATION);
        }
        else
        {
            submitter.setConfirm(Meeting.NO_ANSWER);
        }
        dataExtraction.setObject(ConstantNames.MEETINGS_PATH,team.getKeyID(),meeting.getKeyID(),meeting);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.MEETINGS_PATH);
        mDatabase.child(team.getKeyID())
                .child(meeting.getKeyID())
                .child(ConstantNames.DATA_USERS_LIST)
                .child(user.getKeyID())
                .setValue(submitter);
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