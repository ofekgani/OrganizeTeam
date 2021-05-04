package com.myapp.organizeteam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.organizeteam.Adapters.UsersListAdapterRel;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Meeting;
import com.myapp.organizeteam.Core.Submitter;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.DataListener;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.MyService.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MeetingActivity extends AppCompatActivity{

    DataExtraction dataExtraction;
    ActivityTransition activityTransition;

    TextView tv_meetingName, tv_meetingDate, tv_meetingDescription;
    Button btn_arrivalConfirmation, btn_enableMeeting, btn_confirmArrivals;
    RecyclerView lv_arrivals, lv_rejects;
    Toolbar toolbar;

    Intent intent;
    Meeting meeting;
    Team team;
    User user;

    boolean arrival;
    private Submitter submitter;
    int position;

    ArrayList<User> arrivalConfirmationList, rejectsList, arrivalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        dataExtraction = new DataExtraction();
        activityTransition = new ActivityTransition();

        btn_arrivalConfirmation = findViewById(R.id.btn_confirmArrival);
        btn_confirmArrivals = findViewById(R.id.btn_confirmArrivals);
        btn_enableMeeting = findViewById(R.id.btn_enableMeeting);
        tv_meetingName = findViewById(R.id.tv_meetingName);
        tv_meetingDate = findViewById(R.id.tv_meetingDate);
        tv_meetingDescription = findViewById(R.id.tv_meetingDescription);
        lv_arrivals = findViewById(R.id.lv_arrivalConfirmations);
        lv_rejects = findViewById(R.id.lv_rejects);

        toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        intent = getIntent();
        meeting = (Meeting) intent.getSerializableExtra(ConstantNames.MEETING);
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
        user = (User) intent.getSerializableExtra(ConstantNames.USER);
        position = (int) intent.getSerializableExtra("pos");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(""+meeting.getMeetingName());

        setUI();
        setConfirmationButton();
        dataExtraction.getUsersByConfirmations(ConstantNames.MEETINGS_PATH,team.getKeyID(),meeting.getKeyID(),Meeting.ARRIVAL_CONFIRMATION, new ISavable() {
            @Override
            public void onDataRead(Object save) {
                arrivalConfirmationList = (ArrayList<User>) save;
                setAdapter(arrivalConfirmationList,lv_arrivals);
            }
        });

        dataExtraction.getUsersByConfirmations(ConstantNames.MEETINGS_PATH, team.getKeyID(), meeting.getKeyID(), Meeting.NO_ANSWER, new ISavable() {
            @Override
            public void onDataRead(Object save) {
                rejectsList = (ArrayList<User>) save;
                setAdapter(rejectsList,lv_rejects);
            }
        });

        dataExtraction.getUsersByConfirmations(ConstantNames.MEETINGS_PATH, team.getKeyID(), meeting.getKeyID(), Meeting.ARRIVED, new ISavable() {
            @Override
            public void onDataRead(Object save) {
                arrivalList = (ArrayList<User>) save;
            }
        });
    }

    private void setConfirmationButton() {
        btn_arrivalConfirmation.setVisibility(View.GONE);
        dataExtraction.getSubmitter(ConstantNames.MEETINGS_PATH,team.getKeyID(), meeting.getKeyID(), user.getKeyID(), new ISavable() {
            @Override
            public void onDataRead(Object save) {
                submitter = (Submitter) save;
                if(submitter == null) return;
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

    private void setUI() {
        btn_enableMeeting.setVisibility(View.GONE);
        btn_confirmArrivals.setVisibility(View.GONE);

        tv_meetingName.setText(""+meeting.getMeetingName());
        tv_meetingDate.setText(""+meeting.getDate().getDay()+"/"+meeting.getDate().getMonth()+"/"+meeting.getDate().getYear());
        tv_meetingDescription.setText(""+meeting.getMeetingDescription());

        if(Authorization.isManager)
        {
            btn_enableMeeting.setVisibility(View.VISIBLE);
            btn_confirmArrivals.setVisibility(View.VISIBLE);

            if(meeting.getStatus() == Meeting.FLAG_MEETING_STARTED)
            {
                btn_enableMeeting.setText("End meeting");
            }
            else if(meeting.getStatus() == Meeting.FLAG_MEETING_BOOKED)
            {
                btn_enableMeeting.setText("Start meeting");
            }
        }
        else
        {
            btn_enableMeeting.setVisibility(View.GONE);
            btn_confirmArrivals.setVisibility(View.GONE);
        }

    }

    private void setAdapter(ArrayList<User> users, RecyclerView recyclerView) {
        if(users != null)
        {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
            RecyclerView.Adapter adapter = new UsersListAdapterRel(users, null);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
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
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.MEETINGS_PATH);
        mDatabase.child(team.getKeyID())
                .child(meeting.getKeyID())
                .child(ConstantNames.DATA_USERS_LIST)
                .child(user.getKeyID())
                .setValue(submitter);
        activityTransition.back(this);
    }

    public void oc_handleMeeting(View view) {
        if(meeting.getStatus() == Meeting.FLAG_MEETING_STARTED)
        {
            dataExtraction.deleteData(ConstantNames.MEETINGS_PATH, team.getKeyID(), meeting.getKeyID(), new DataListener() {
                @Override
                public void onDataDelete() {
                    for(User user : rejectsList)
                    {
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_STATUSES_PATH)
                                .child(team.getKeyID())
                                .child(user.getKeyID())
                                .child(ConstantNames.MEETINGS_PATH)
                                .child(ConstantNames.DATA_USER_STATUS_MISSING);
                        mDatabase.child(meeting.getKeyID()).setValue(meeting);
                    }

                    for(User user : arrivalConfirmationList)
                    {
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_STATUSES_PATH)
                                .child(team.getKeyID())
                                .child(user.getKeyID())
                                .child(ConstantNames.MEETINGS_PATH)
                                .child(ConstantNames.DATA_USER_STATUS_MISSING);
                        mDatabase.child(meeting.getKeyID()).setValue(meeting);
                    }

                    for(User user : arrivalList)
                    {
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_STATUSES_PATH)
                                .child(team.getKeyID())
                                .child(user.getKeyID())
                                .child(ConstantNames.MEETINGS_PATH)
                                .child(ConstantNames.DATA_USER_STATUS_ARRIVED);
                        mDatabase.child(meeting.getKeyID()).setValue(meeting);
                    }

                }
            });
            finishActivity(null);
        }
        else if(meeting.getStatus() == Meeting.FLAG_MEETING_BOOKED)
        {
            startAlarm(meeting);
            meeting.setStatus(Meeting.FLAG_MEETING_STARTED);
            dataExtraction.setNewData(ConstantNames.MEETINGS_PATH,team.getKeyID(),meeting.getKeyID(),ConstantNames.DATA_MEETING_STATUS,meeting.getStatus());
            finishActivity(meeting);
        }
    }

    public void oc_confirmArrivals(View view) {
        if(arrivalConfirmationList == null || arrivalConfirmationList.size() == 0) return;
        Map<String,Object> users = new HashMap<>();
        users.put(ConstantNames.USERS_LIST, arrivalConfirmationList);
        users.put(ConstantNames.TEAM_KEY_ID,team.getKeyID());
        activityTransition.goToWithResult(this,UserSelectionActivity.class,753,users,null);
    }

    private void finishActivity(Meeting meeting) {
        Map<String,Object> save = new HashMap<>();
        save.put("pos",position);
        if(meeting != null)
        {
            save.put(ConstantNames.MEETING,meeting);
        }
        activityTransition.back(this,save);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK && requestCode == 753)
            {
                ArrayList<User> users = (ArrayList<User>) data.getSerializableExtra(ConstantNames.USERS_LIST);
                for(User user : users)
                {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_STATUSES_PATH)
                            .child(team.getKeyID())
                            .child(user.getKeyID())
                            .child(ConstantNames.MEETINGS_PATH)
                            .child(ConstantNames.DATA_USER_STATUS_ARRIVED);
                    mDatabase.child(meeting.getKeyID()).setValue(meeting);

                    DatabaseReference submitterDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.MEETINGS_PATH)
                            .child(team.getKeyID())
                            .child(meeting.getKeyID())
                            .child(ConstantNames.DATA_USERS_LIST)
                            .child(user.getKeyID())
                            .child(ConstantNames.DATA_TASK_CONFIRM);
                    submitterDatabase.setValue(Meeting.ARRIVED);
                    if(isExist(user,arrivalConfirmationList))
                    {
                        arrivalConfirmationList.remove(user);
                    }
                    if(!isExist(user,arrivalList))
                    {
                        arrivalList.add(user);
                    }
                }
            }
        }
    }

    private boolean isExist(User user, ArrayList<User> users)
    {
        for(User u : users)
        {
            if(u.getKeyID().equals(user.getKeyID()))
            {
                return true;
            }
        }
        return false;
    }

    private void startAlarm(Meeting meeting){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantNames.MEETING,meeting);
        intent.putExtra("bundle", bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.DATE, pendingIntent);
    }
}