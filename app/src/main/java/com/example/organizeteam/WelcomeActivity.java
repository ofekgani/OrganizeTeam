package com.example.organizeteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.organizeteam.Core.ActivityTransition;
import com.example.organizeteam.Core.ConstantNames;
import com.example.organizeteam.Core.Team;
import com.example.organizeteam.Core.User;
import com.example.organizeteam.DataManagement.Authorization;
import com.example.organizeteam.DataManagement.ISavable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    ActivityTransition activityTransition;
    Authorization authorization;

    Intent intent;
    User user;

    TextView tv_userName, tv_userEmail;

    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        tv_userEmail = findViewById(R.id.tv_userEmail);
        tv_userName = findViewById(R.id.tv_userName);

         activityTransition = new ActivityTransition();
         authorization = new Authorization();

        intent = getIntent();
        user = (User)intent.getSerializableExtra ( ConstantNames.USER );

        tv_userName.setText(""+user.getFullName());
        tv_userEmail.setText(""+user.getEmail());
    }

    public void oc_joinToTeam(View view) {
        //get all teams
        authorization.getAllTeams(new ISavable() {
            @Override
            public void onDataRead(Object save) {
                ArrayList<Team> teamList = (ArrayList<Team>)save;
                Map<String,Object> data = new HashMap<>();

                data.put(ConstantNames.TEAMS_LIST,teamList);
                data.put(ConstantNames.USER,user);
                activityTransition.goTo(WelcomeActivity.this,SelectTeamActivity.class,false,data,null);
            }
        });
    }

    public void oc_createTeam(View view) {
        Map<String,Object> values = new HashMap<> (  );
        values.put ( ConstantNames.USER,user);

        activityTransition.goTo( this,CreateTeamActivity.class, false, values ,null );
    }

    public void oc_signOut(View view) {
        authorization.singOut(this);
    }

    public void oc_chooseImage(View view) {
    }
}