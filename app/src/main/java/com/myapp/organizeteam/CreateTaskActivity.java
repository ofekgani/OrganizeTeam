package com.myapp.organizeteam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Date;
import com.myapp.organizeteam.Core.Hour;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Submitter;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.MyService.APIService;
import com.myapp.organizeteam.MyService.Data;
import com.myapp.organizeteam.MyService.Notification;
import com.myapp.organizeteam.MyService.Sender;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTaskActivity extends AppCompatActivity {

    InputManagement inputManagement;
    DataExtraction dataExtraction;
    ActivityTransition activityTransition;

    EditText ed_taskName,ed_taskDescription,ed_taskDate, ed_taskHour;
    CheckBox cb_enableRequired;

    Intent intent;
    Team team;
    User user;

    Calendar calendar;

    int m_year;
    int m_month;
    int m_day;
    int m_minute;
    int m_hour;

    private final String CHANNEL_ID = "3";

    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        inputManagement = new InputManagement();
        dataExtraction = new DataExtraction();
        activityTransition = new ActivityTransition();

        ed_taskDate = findViewById(R.id.ed_meetingDate);
        ed_taskDescription = findViewById(R.id.ed_roleDescription);
        ed_taskName = findViewById(R.id.ed_roleName);
        ed_taskHour = findViewById(R.id.ed_meetingHour);
        cb_enableRequired = findViewById(R.id.cb_enableRequired);

        intent = getIntent();
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
        user = (User) intent.getSerializableExtra(ConstantNames.USER);

        ed_taskDate.setRawInputType(InputType.TYPE_NULL);
        ed_taskDate.setFocusable(false);
        ed_taskDate.setKeyListener(null);

        ed_taskHour.setRawInputType(InputType.TYPE_NULL);
        ed_taskHour.setFocusable(false);
        ed_taskHour.setKeyListener(null);

        calendar = Calendar.getInstance();
        m_year = calendar.get(Calendar.YEAR);
        m_month = calendar.get(Calendar.MONTH)+1;
        m_day = calendar.get(Calendar.DAY_OF_MONTH);

        ed_taskDate.setText(m_day+"/"+m_month+1+"/"+m_year);

        ed_taskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        m_year = year;
                        m_month = month+1;
                        m_day = day;

                        String date = m_day+"/"+m_month+"/"+m_year;
                        ed_taskDate.setText(date);
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

        ed_taskHour.setText(m_hour+1 + ":"+m_minute);

        ed_taskHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                        m_minute = minute;
                        m_hour = hour;
                        calendar.set(Calendar.SECOND, 0);

                        String time = m_hour + ":"+m_minute;
                        ed_taskHour.setText(time);
                        calendar.set(Calendar.MINUTE, m_minute);
                        calendar.set(Calendar.HOUR_OF_DAY, m_hour);
                    }
                },m_hour,m_minute,true);
                timePickerDialog.show();
            }
        });

    }

    public void oc_createTask(View view) {
        if(inputManagement.isInputEmpty(ed_taskName)) return;
        if(inputManagement.isInputEmpty(ed_taskDate) && inputManagement.isInputEmpty(ed_taskHour)) return;

        Map<String,Object> save = new HashMap<>();
        save.put(ConstantNames.TEAM_KEY_ID,team.getKeyID());
        save.put(ConstantNames.USER_PERMISSIONS,intent.getSerializableExtra(ConstantNames.USER_PERMISSIONS));
        activityTransition.goToWithResult(CreateTaskActivity.this,RoleSelectionActivity.class,300,save,null);

        this.view = view;
    }

    private void sendNotification(final View view, final ArrayList<String> usersID) {
        final String id = user.getKeyID();
        final String name = user.getFullName();
        for(String userID : usersID)
        {
            dataExtraction.getToken(userID, new ISavable() {
                @Override
                public void onDataRead(Object token) {
                    String userToken = (String)token;
                    String body = name + " created a new task.";
                    Data data = new Data (id,body,"New task",user.getKeyID());
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

    private APIService getAPIService(View view) {
        Notification notification = new Notification ();

        notification.createNotificationChannel (view.getContext (),CHANNEL_ID,"Tasks","Handle tasks notifications",5);

        return notification.createClient ();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null) return;
        if(resultCode == RESULT_OK && requestCode == 300)
        {
            //Get a list of roles that will be defined within the cloud as roles to which meetings will be posted
            ArrayList<Role> rolesList = (ArrayList<Role>) data.getSerializableExtra(ConstantNames.ROLES_LIST);
            ArrayList<String> rolesID = new ArrayList<>();
            for (Role r : rolesList)
            {
                String keyID = r.getKeyID();
                rolesID.add(keyID);
            }

            //Get inputs
            String taskName = inputManagement.getInput(ed_taskName);
            String taskDescription = inputManagement.getInput(ed_taskDescription);
            boolean isRequiredConfirm = cb_enableRequired.isChecked();
            Date taskDate = new Date(m_year,m_month,m_day);
            Hour taskTime = new Hour(m_hour,m_minute);

            final String teamID = team.getKeyID();
            DataExtraction dataExtraction = new DataExtraction();

            //Create to meeting keyID
            DatabaseReference mDatabase = FirebaseDatabase.getInstance ().getReference ( ConstantNames.TASK_PATH).child(teamID);
            final String taskID = mDatabase.push ().getKey ();

            //Add meeting into firebase
            final Mission task = new Mission(taskID,teamID,taskName,taskDescription,isRequiredConfirm,taskDate,taskTime);
            dataExtraction.setObject(ConstantNames.TASK_PATH,teamID,taskID,task);

            //Add to cloud all selected roles to which the meeting will be published
            for(String id : rolesID)
            {
                DatabaseReference rolesDatabase = FirebaseDatabase.getInstance ().getReference ( ConstantNames.TASK_PATH).child(teamID);
                rolesDatabase.child(taskID).child(ConstantNames.DATA_PUBLISH_TO).push().setValue(id);
            }

            dataExtraction.getUsersByRoles(rolesID, teamID, new ISavable() {
                @Override
                public void onDataRead(Object usersList) {
                    ArrayList<String> usersID = (ArrayList<String>) usersList;
                    DatabaseReference taskDatabase = FirebaseDatabase.getInstance ().getReference (ConstantNames.USER_ACTIVITY_PATH).child(teamID);
                    DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.TASK_PATH).child(teamID).child(taskID).child(ConstantNames.DATA_USERS_LIST);
                    for(String id : usersID)
                    {
                        taskDatabase.child(id).child(ConstantNames.DATA_USER_TASKS).push().setValue(taskID);
                        Submitter submitter = new Submitter(null,null,null,null,Submitter.STATUS_UNSUBMITTED,taskID,id);
                        usersDatabase.child(id).setValue(submitter);
                    }
                    Map<String,Object> save = new HashMap<>();
                    save.put(ConstantNames.TASK,task);
                    activityTransition.back(CreateTaskActivity.this,save);
                    sendNotification(view,usersID);
                }
            });
        }
    }
}