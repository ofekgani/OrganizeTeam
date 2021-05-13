package com.myapp.organizeteam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.organizeteam.Adapters.MeetingsListAdapter;
import com.myapp.organizeteam.Adapters.RolesListAdapterRel;
import com.myapp.organizeteam.Adapters.RolesUsersListAdapterRel;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Meeting;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.FileManage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class UserInformationActivity extends AppCompatActivity {

    ActivityTransition activityTransition;
    DataExtraction dataExtraction;
    FileManage fileManage;

    TextView tv_userEmail, tv_userPhone, tv_cameToMeetings, tv_absentFromMeetings, tv_tasksSubmitted, tv_missedTasks;
    ImageView mv_userLogo;
    RecyclerView lv_userRoles;
    CardView cv_cameToMeetings, cv_absentFromMeetings, cv_tasksSubmitted, cv_missedTasks;
    Toolbar toolbar;

    Intent intent;
    User user;
    Team team;

    private ArrayList<User> arrivalsList;
    private ArrayList<User> absentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        activityTransition = new ActivityTransition();
        dataExtraction = new DataExtraction();
        fileManage = new FileManage();

        tv_userEmail = findViewById(R.id.tv_userEmail);
        tv_userPhone = findViewById(R.id.tv_userPhone);
        tv_cameToMeetings = findViewById(R.id.tv_cameToMeetings);
        tv_absentFromMeetings = findViewById(R.id.tv_absentFromMeetings);
        tv_tasksSubmitted = findViewById(R.id.tv_tasksSubmitted);
        tv_missedTasks = findViewById(R.id.tv_missedTasks);
        mv_userLogo = findViewById(R.id.mv_userLogo);
        lv_userRoles = findViewById(R.id.lv_rolesList);
        cv_cameToMeetings = findViewById(R.id.cv_CameToMeetings);
        cv_absentFromMeetings = findViewById(R.id.cv_absentFromMeetings);
        cv_tasksSubmitted = findViewById(R.id.cv_TasksSubmitted);
        cv_missedTasks = findViewById(R.id.cv_missedTasks);

        toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        intent = getIntent();
        user = (User) intent.getSerializableExtra(ConstantNames.USER);
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(""+user.getFullName());

        setUI();

        dataExtraction.getAllRolesByUser(user.getKeyID(), team.getKeyID(), new ISavable() {
            @Override
            public void onDataRead(Object save) {
                setAdapter((ArrayList<Role>) save,lv_userRoles);
            }
        });

        dataExtraction.getKeysByStatus(ConstantNames.MEETINGS_PATH, team.getKeyID(), user.getKeyID(), ConstantNames.DATA_USER_STATUS_ARRIVED, new ISavable() {
            @Override
            public void onDataRead(Object save) {
                arrivalsList = (ArrayList<User>) save;
                tv_cameToMeetings.setText(""+ arrivalsList.size());
            }
        });

        dataExtraction.getKeysByStatus(ConstantNames.MEETINGS_PATH, team.getKeyID(), user.getKeyID(), ConstantNames.DATA_USER_STATUS_MISSING, new ISavable() {

            @Override
            public void onDataRead(Object save) {
                absentsList = (ArrayList<User>) save;
                tv_absentFromMeetings.setText(""+ absentsList.size());
            }
        });

        cv_absentFromMeetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> save = new Hashtable<>();
                save.put("meetingsID",absentsList);
                save.put(ConstantNames.TEAM,team);
                activityTransition.goTo(UserInformationActivity.this, MeetingListActivity.class,false,save,null);
            }
        });

        cv_cameToMeetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> save = new Hashtable<>();
                save.put("meetingsID",arrivalsList);
                save.put(ConstantNames.TEAM,team);
                activityTransition.goTo(UserInformationActivity.this, MeetingListActivity.class,false,save,null);
            }
        });

    }

    private void setAdapter(ArrayList<Role> roles, RecyclerView recyclerView) {
        if(roles != null)
        {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
            RecyclerView.Adapter adapter = new RolesListAdapterRel(roles);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    private void setUI() {
        tv_userEmail.setText(""+user.getEmail());
        if(user.getPhone() != null)
            tv_userPhone.setText(""+user.getPhone());
        if(user.getLogo() != null)
            fileManage.setImageUri(user.getLogo(),mv_userLogo);
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