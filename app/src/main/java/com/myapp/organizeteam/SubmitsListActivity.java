package com.myapp.organizeteam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.myapp.organizeteam.Adapters.UsersListAdapterRel;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubmitsListActivity extends AppCompatActivity {

    DataExtraction dataExtraction;
    ActivityTransition activityTransition;

    RecyclerView lv_users, lv_rejects;
    private RecyclerView.Adapter usersAdapter,rejectsAdapter;
    private RecyclerView.LayoutManager usersLayoutManager,rejectsLayoutManager;
    private UsersListAdapterRel.RecycleViewClickListener usersListener;

    ArrayList<User> usersList;
    ArrayList<User> rejectsList;

    Intent intent;
    Team team;
    Mission mission;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submits_list);

        dataExtraction = new DataExtraction();
        activityTransition = new ActivityTransition();

        lv_users = findViewById(R.id.lv_submits);
        lv_rejects = findViewById(R.id.lv_rejects);

        intent = getIntent();
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
        mission = (Mission) intent.getSerializableExtra(ConstantNames.TASK);
        user = (User) intent.getSerializableExtra(ConstantNames.USER);

        dataExtraction.getSubmitters(team.getKeyID(), mission.getKeyID(), new ISavable() {
            @Override
            public void onDataRead(Object save) {
                usersList = (ArrayList<User>) save;
                dataExtraction.getRejects(team.getKeyID(), mission.getKeyID(), new ISavable() {
                    @Override
                    public void onDataRead(Object save) {
                        rejectsList = (ArrayList<User>) save;
                        setAdapters(usersList, rejectsList);
                    }
                });
            }
        });

    }

    private void setAdapters(ArrayList<User> usersList, ArrayList<User> rejectsList) {
        if(usersList != null) {
            setOnClickListener();
            lv_users.setHasFixedSize(true);
            usersLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
            usersAdapter = new UsersListAdapterRel(usersList, usersListener);
            lv_users.setLayoutManager(usersLayoutManager);
            lv_users.setAdapter(usersAdapter);
        }

        if(rejectsList != null){
            lv_rejects.setHasFixedSize(true);
            rejectsLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
            rejectsAdapter = new UsersListAdapterRel(rejectsList, null);
            lv_rejects.setLayoutManager(rejectsLayoutManager);
            lv_rejects.setAdapter(rejectsAdapter);
        }
    }

    private void setOnClickListener() {
        usersListener = new UsersListAdapterRel.RecycleViewClickListener() {
            @Override
            public void onClick(View v, final int position) {
                dataExtraction.getSubmitterTask(team.getKeyID(), mission.getKeyID(), usersList.get(position).getKeyID(), new ISavable() {
                    @Override
                    public void onDataRead(Object submitter) {
                        Map<String,Object> save = new HashMap<>();
                        save.put(ConstantNames.TASK,mission);
                        save.put(ConstantNames.USER_SUBMITTER,usersList.get(position));
                        save.put(ConstantNames.SUBMITTER,submitter);
                        save.put(ConstantNames.USER,user);
                        save.put(ConstantNames.TEAM,team);
                        activityTransition.goTo(SubmitsListActivity.this,SubmissionActivity.class,false,save,null);
                    }
                });

            }
        };
    }
}