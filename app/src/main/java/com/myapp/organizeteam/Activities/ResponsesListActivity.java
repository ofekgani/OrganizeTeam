package com.myapp.organizeteam.Activities;

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
import com.myapp.organizeteam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResponsesListActivity extends AppCompatActivity {

    DataExtraction dataExtraction;
    ActivityTransition activityTransition;

    RecyclerView lv_responses;
    private RecyclerView.Adapter usersAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private UsersListAdapterRel.RecycleViewClickListener listener;

    ArrayList<User> usersList;

    Intent intent;
    Team team;
    Mission mission;
    User user, userSubmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses_list);

        dataExtraction = new DataExtraction();
        activityTransition = new ActivityTransition();

        lv_responses = findViewById(R.id.lv_responses);

        intent = getIntent();
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
        userSubmitter = (User) intent.getSerializableExtra(ConstantNames.USER_SUBMITTER);
        user = (User) intent.getSerializableExtra(ConstantNames.USER);
        mission = (Mission) intent.getSerializableExtra(ConstantNames.TASK);

        //build list of responses
        dataExtraction.getResponsesOfSubmitter(team.getKeyID(),mission.getKeyID(),userSubmitter.getKeyID(), new ISavable() {
            @Override
            public void onDataRead(Object save) {
                usersList = (ArrayList<User>) save;

                setAdapter(usersList);
            }
        });

    }

    private void setAdapter(ArrayList<User> users) {
        if(users != null)
        {
            setOnClickListener();
            lv_responses.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
            usersAdapter = new UsersListAdapterRel(users, listener);
            lv_responses.setLayoutManager(mLayoutManager);
            lv_responses.setAdapter(usersAdapter);
        }
    }

    private void setOnClickListener() {
        listener = new UsersListAdapterRel.RecycleViewClickListener() {
            @Override
            public void onClick(View v, final int position) {
                dataExtraction.getResponse(team.getKeyID(), mission.getKeyID(), userSubmitter.getKeyID(),usersList.get(position).getKeyID(), new ISavable() {
                    @Override
                    public void onDataRead(Object submitter) {
                        Map<String,Object> save = new HashMap<>();
                        save.put(ConstantNames.TASK,mission);
                        save.put(ConstantNames.USER_SUBMITTER,usersList.get(position));
                        save.put(ConstantNames.SUBMITTER,submitter);
                        save.put(ConstantNames.USER,user);
                        save.put(ConstantNames.TEAM,team);
                        activityTransition.goTo(ResponsesListActivity.this, SubmissionActivity.class,false,save,null);
                    }
                });

            }
        };
    }
}