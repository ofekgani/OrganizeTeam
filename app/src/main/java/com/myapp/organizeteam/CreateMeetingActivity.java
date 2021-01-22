package com.myapp.organizeteam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.organizeteam.Adapters.UsersListAdapter;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Date;
import com.myapp.organizeteam.Core.Hour;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.Meeting;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.MyService.APIService;
import com.myapp.organizeteam.MyService.AlarmReceiver;
import com.myapp.organizeteam.MyService.Data;
import com.myapp.organizeteam.MyService.Notification;
import com.myapp.organizeteam.MyService.Sender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.myapp.organizeteam.Core.Meeting.FLAG_MEETING_BOOKED;
import static com.myapp.organizeteam.Core.Meeting.FLAG_MEETING_STARTED;


public class CreateMeetingActivity extends AppCompatActivity{

    InputManagement inputManagement;
    DataExtraction dataExtraction;

    EditText ed_meetingName,ed_meetingDescription,ed_meetingDate, ed_meetingHour;

    Intent intent;
    Team team;
    User user;

    Calendar calendar;

    int m_year;
    int m_month;
    int m_day;
    int m_minute;
    int m_hour;

    private final String CHANNEL_ID = "2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

        inputManagement = new InputManagement();
        dataExtraction = new DataExtraction();

        ed_meetingDate = findViewById(R.id.ed_meetingDate);
        ed_meetingDescription = findViewById(R.id.ed_meetingDescription);
        ed_meetingName = findViewById(R.id.ed_meetingName);
        ed_meetingHour = findViewById(R.id.ed_meetingHour);

        intent = getIntent();
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
        user = (User) intent.getSerializableExtra(ConstantNames.USER);

        ed_meetingDate.setRawInputType(InputType.TYPE_NULL);
        ed_meetingDate.setFocusable(false);
        ed_meetingDate.setKeyListener(null);

        ed_meetingHour.setRawInputType(InputType.TYPE_NULL);
        ed_meetingHour.setFocusable(false);
        ed_meetingHour.setKeyListener(null);

        calendar = Calendar.getInstance();
        m_year = calendar.get(Calendar.YEAR);
        m_month = calendar.get(Calendar.MONTH)+1;
        m_day = calendar.get(Calendar.DAY_OF_MONTH);

        ed_meetingDate.setText(m_day+"/"+m_month+1+"/"+m_year);

        ed_meetingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateMeetingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        m_year = year;
                        m_month = month+1;
                        m_day = day;

                        String date = m_day+"/"+m_month+"/"+m_year;
                        ed_meetingDate.setText(date);
                        calendar.set(Calendar.YEAR, m_year);
                        calendar.set(Calendar.MONTH, m_month);
                        calendar.set(Calendar.DAY_OF_MONTH, m_day);
                    }
                },m_year,m_month,m_day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        m_minute = calendar.get(Calendar.MINUTE);
       m_hour = calendar.get(Calendar.HOUR_OF_DAY);

        ed_meetingHour.setText(m_hour+1 + ":"+m_minute);

        ed_meetingHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateMeetingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                        m_minute = minute;
                        m_hour = hour;
                        calendar.set(Calendar.SECOND, 0);

                        String time = m_hour + ":"+m_minute;
                        ed_meetingHour.setText(time);
                        calendar.set(Calendar.MINUTE, m_minute);
                        calendar.set(Calendar.HOUR_OF_DAY, m_hour);
                    }
                },m_hour,m_minute,true);
                timePickerDialog.show();
            }
        });

    }

    public void oc_createMeeting(View view) {
        if(inputManagement.isInputEmpty(ed_meetingName)) return;
        if(inputManagement.isInputEmpty(ed_meetingDate) && inputManagement.isInputEmpty(ed_meetingHour)) return;

        String meetingName, meetingDescription;

        meetingName = inputManagement.getInput(ed_meetingName);
        meetingDescription = inputManagement.getInput(ed_meetingDescription);

        Date meetingDate = new Date(m_year,m_month,m_day);
        Hour meetingTime = new Hour(m_hour,m_minute);

        String keyID = team.getKeyID();

        DataExtraction dataExtraction = new DataExtraction();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance ().getReference ( ConstantNames.MEETINGS_PATH).child(keyID);
        String meetingID = mDatabase.push ().getKey ();

        Meeting meeting = new Meeting(meetingID,team.getKeyID(),meetingName,meetingDescription,meetingDate,meetingTime,FLAG_MEETING_BOOKED);
        dataExtraction.setObject(ConstantNames.MEETINGS_PATH,keyID,meetingID,meeting);
        setAlarm(calendar,meeting);
        ActivityTransition activityTransition = new ActivityTransition();
        Map<String,Object> save = new HashMap<>();
        save.put(ConstantNames.MEETING,meeting);
        activityTransition.back(this,save);
        sendNotification(view);
    }

    private void setAlarm(Calendar target, Meeting meeting){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantNames.MEETING,meeting);
        intent.putExtra("bundle", bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (target.before(Calendar.getInstance())) {
            target.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), pendingIntent);
    }

    private void sendNotification(final View view) {
        final String id = user.getKeyID();
        final String name = user.getFullName();
        dataExtraction.getAllUsersByTeam(team.getKeyID(), ConstantNames.DATA_USERS_AT_TEAM, new ISavable() {
            @Override
            public void onDataRead(Object save) {
                ArrayList<User> users = ( ArrayList<User>)save;
                for(final User user : users)
                {
                    final String keyID = user.getKeyID();
                    dataExtraction.getToken(keyID, new ISavable() {
                        @Override
                        public void onDataRead(Object token) {
                            String userToken = (String)token;
                            String body = name + " created a new meeting booked on " + m_day+"/"+m_month+"/"+m_year+ " .";
                            Data data = new Data (id,body,"New meeting",user.getKeyID());
                            Sender sender = new Sender (data,userToken);

                            //Send notification to device
                            APIService apiService = getAPIService(view);
                            apiService.sendNotification ( sender ).enqueue ( new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Toast.makeText(view.getContext(),""+response,Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(view.getContext(),""+t,Toast.LENGTH_LONG).show();
                                }
                            } );
                        }
                    });
                }
            }
        });
    }

    private APIService getAPIService(View view) {
        Notification notification = new Notification ();

        notification.createNotificationChannel (view.getContext (),CHANNEL_ID,"Meetings","Handle meetings notifications",5);

        return notification.createClient ();
    }

}
