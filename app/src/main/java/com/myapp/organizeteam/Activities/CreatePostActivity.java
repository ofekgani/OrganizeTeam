package com.myapp.organizeteam.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Date;
import com.myapp.organizeteam.Core.Hour;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.Post;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.MyService.APIService;
import com.myapp.organizeteam.MyService.Data;
import com.myapp.organizeteam.MyService.Notification;
import com.myapp.organizeteam.MyService.Sender;
import com.myapp.organizeteam.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    public static final int REQUEST = 290;
    InputManagement inputManagement;
    DataExtraction dataExtraction;
    ActivityTransition activityTransition;

    EditText ed_postName, ed_postContent;

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
        setContentView(R.layout.activity_create_post);

        activityTransition = new ActivityTransition();
        dataExtraction = new DataExtraction();
        inputManagement = new InputManagement();

        ed_postName = findViewById(R.id.ed_postName);
        ed_postContent = findViewById(R.id.ed_postDescription);

        intent = getIntent();
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
        user = (User) intent.getSerializableExtra(ConstantNames.USER);

        calendar = Calendar.getInstance();
        m_year = calendar.get(Calendar.YEAR);
        m_month = calendar.get(Calendar.MONTH)+1;
        m_day = calendar.get(Calendar.DAY_OF_MONTH);

        m_minute = calendar.get(Calendar.MINUTE);
        m_hour = calendar.get(Calendar.HOUR_OF_DAY);
    }

    public void oc_createPost(View view) {
        if(inputManagement.isInputEmpty(ed_postName) && inputManagement.isInputEmpty(ed_postContent)) return;

        Map<String,Object> save = new HashMap<>();
        save.put(ConstantNames.TEAM_KEY_ID,team.getKeyID());
        save.put(ConstantNames.USER_PERMISSIONS,intent.getSerializableExtra(ConstantNames.USER_PERMISSIONS));
        activityTransition.goToWithResult(CreatePostActivity.this, RoleSelectionActivity.class, REQUEST,save,null);

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
                    String body = name + " created a new post.";
                    Data data = new Data (id,body,"New post",user.getKeyID());
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

        notification.createNotificationChannel (view.getContext (),CHANNEL_ID,"Posts","Handle posts notifications",5);

        return notification.createClient ();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null) return;
        if(resultCode == RESULT_OK && requestCode == REQUEST)
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
            String postName = inputManagement.getInput(ed_postName);
            String postDescription = inputManagement.getInput(ed_postContent);
            Date postDate = new Date(m_year,m_month,m_day);
            Hour postTime = new Hour(m_hour,m_minute);

            final String teamID = team.getKeyID();

            //Create to meeting keyID
            DatabaseReference mDatabase = FirebaseDatabase.getInstance ().getReference ( ConstantNames.POST_PATH).child(teamID);
            final String postID = mDatabase.push ().getKey ();

            //Add meeting into firebase
            final Post post = new Post(postID,teamID,postName,postDescription,postDate,postTime);
            dataExtraction.setObject(ConstantNames.POST_PATH,teamID,postID,post);

            //Add to cloud all selected roles to which the meeting will be published
            for(String id : rolesID)
            {
                DatabaseReference rolesDatabase = FirebaseDatabase.getInstance ().getReference ( ConstantNames.POST_PATH).child(teamID);
                rolesDatabase.child(postID).child(ConstantNames.DATA_PUBLISH_TO).push().setValue(id);
            }

            dataExtraction.getUsersByRoles(rolesID, teamID, new ISavable() {
                @Override
                public void onDataRead(Object usersList) {
                    ArrayList<String> usersID = (ArrayList<String>) usersList;
                    DatabaseReference taskDatabase = FirebaseDatabase.getInstance ().getReference (ConstantNames.USER_ACTIVITY_PATH).child(teamID);
                    for(String id : usersID)
                    {
                        taskDatabase.child(id).child(ConstantNames.DATA_USER_POSTS).push().setValue(postID);
                    }
                    Map<String,Object> save = new HashMap<>();
                    save.put(ConstantNames.TASK,post);
                    activityTransition.back(CreatePostActivity.this,save);
                    sendNotification(view,usersID);
                }
            });
        }
    }
}