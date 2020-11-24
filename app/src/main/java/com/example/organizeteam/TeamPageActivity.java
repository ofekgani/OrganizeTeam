package com.example.organizeteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.organizeteam.Adapters.TeamListAdapter;
import com.example.organizeteam.Adapters.UsersListAdapter;
import com.example.organizeteam.Core.ConstantNames;
import com.example.organizeteam.Core.Team;
import com.example.organizeteam.DataManagement.Authorization;
import com.example.organizeteam.Core.ActivityTransition;
import com.example.organizeteam.DataManagement.DataExtraction;
import com.example.organizeteam.DataManagement.ISavable;
import com.example.organizeteam.Resources.Image;
import com.example.organizeteam.Core.User;

import java.util.ArrayList;

public class TeamPageActivity extends AppCompatActivity {

    TextView tv_teamName, tv_managerName;
    ImageView mv_teamLogo, mv_managerLogo;
    ListView lv_users;

    ActivityTransition activityTransition;
    Image image;

    Intent intent;
    UsersListAdapter adapter;

    String teamName,teamLogo,managerName, managerLogo;

    User manager;
    Team team;

    ArrayList<User> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_team_page );

        tv_teamName = findViewById ( R.id.tv_teamName );
        mv_teamLogo = findViewById ( R.id.mv_teamLogo );

        tv_managerName = findViewById ( R.id.tv_mangerName );
        mv_managerLogo = findViewById ( R.id.mv_mangerLogo );

        lv_users = findViewById(R.id.lv_usersList);

        activityTransition = new ActivityTransition ();
        image = new Image ();

        intent = getIntent ();

        //Get Team data.
        updateTeamUI();

        //Get Manager data.
        updateManagerUI();

        buildList();

    }

    private void updateManagerUI() {
        manager = (User)intent.getSerializableExtra ( ConstantNames.TEAM_HOST );
        if(manager != null)
        {
            managerName = manager.getFullName ();
            managerLogo = manager.getLogo ();
        }
        tv_managerName.setText ( ""+managerName );
        image.setImageUri (managerLogo, mv_managerLogo );
    }

    private void updateTeamUI() {
        team = (Team)intent.getSerializableExtra ( ConstantNames.TEAM );
        if(team != null)
        {
            teamName = team.getName();
            teamLogo = team.getLogo();
        }
        image.setImageUri ( teamLogo,mv_teamLogo );
        tv_teamName.setText ( ""+teamName );
    }

    private void buildList()
    {
        final DataExtraction dataExtraction = new DataExtraction();
        dataExtraction.getAllUsersByTeam(team.getKeyID(), new ISavable() {
            @Override
            public void onDataRead(Object save) {
                usersList = (ArrayList<User>)save;
                createUsersList();
            }
        });
    }

    private void createUsersList() {
        adapter = new UsersListAdapter(this,R.layout.adapter_users_list,usersList);
        lv_users.setAdapter ( adapter );
    }

    public void oc_signOut(View view) {
        Authorization authorization = new Authorization ();
        authorization.singOut ( this );
    }
}