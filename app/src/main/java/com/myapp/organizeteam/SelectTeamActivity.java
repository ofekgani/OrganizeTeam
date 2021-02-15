package com.myapp.organizeteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.DataPass;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Dialogs.RequestJoinDialog;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.Adapters.TeamListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectTeamActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, RequestJoinDialog.DialogListener {

    ActivityTransition activityTransition;
    Authorization authorization;
    DataExtraction dataExtraction;
    Image image;

    ListView listView;
    Toolbar toolbar;

    Intent intent;

    TeamListAdapter adapter;
    ArrayList<Team> teamList;

    Team team;
    User user;
    String userID;
    String teamID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_select_team );

        toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Join to team");

        listView = findViewById ( R.id.lv_teamsList );

        //allocating memory
        activityTransition = new ActivityTransition ();
        authorization = new Authorization ();
        dataExtraction = new DataExtraction ();
        image = new Image();

        intent = getIntent (  );

        //get all teams
        teamList = (ArrayList<Team>)intent.getSerializableExtra ( ConstantNames.TEAMS_LIST);

        //get user information
        team = (Team)intent.getSerializableExtra ( ConstantNames.TEAM );
        user = (User)intent.getSerializableExtra ( ConstantNames.USER );
        userID = user.getKeyID();

        createTeamList ();
        getTeamByJoinRequest();
    }

    /**
     * Get information about a team that the user sent request to join.
     */
    private void getTeamByJoinRequest() {
        //Check if join request exist by the user.
        dataExtraction.hasChild(ConstantNames.USER_PATH, userID, ConstantNames.DATA_REQUEST_TO_JOIN, new ISavable() {
            @Override
            public void onDataRead(Object save) {
                if ((boolean) save) {
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_PATH).child(userID).child(ConstantNames.DATA_REQUEST_TO_JOIN);
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //If request exist, get information about the team that got request.
                            teamID = snapshot.getValue().toString();
                            dataExtraction.getTeamDataByID(teamID, new ISavable() {
                                @Override
                                public void onDataRead(Object save) {
                                    team = (Team)save;
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }

    /**
     * Create list of teams.
     */
    private void createTeamList() {
        listView.setOnItemClickListener ( this );
        listView.setChoiceMode ( ListView.CHOICE_MODE_SINGLE );

        adapter = new TeamListAdapter(this,R.layout.team_adapter_view_layout,teamList);
        listView.setAdapter ( adapter );
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {
        //Get team information by choice item from the list
        final Team team = teamList.get ( i );
        final String hostID = team.getHost ();

         //Get user information
         final String userName = user.getFullName ();

         //get host`s token that use to send to him notification
         dataExtraction.getToken ( hostID, new ISavable () {
             @Override
             public void onDataRead(Object save) {
                 String hostToken =  (String)save;

                 //Create open dialog
                 RequestJoinDialog requestJoinDialog = new RequestJoinDialog();

                 //Save all the important information to dialog
                 Map<String,Object> values = new HashMap<> ();
                 values.put ( ConstantNames.TEAM,team );
                 values.put ( ConstantNames.USER_NAME,userName );
                 values.put ( ConstantNames.USER_KEY_ID,userID );
                 values.put ( ConstantNames.HOST_TOKEN,hostToken );

                 Bundle bundle = activityTransition.CreateBundle ( values );
                 requestJoinDialog.setArguments ( bundle );

                 //open the dialog
                 requestJoinDialog.show(getSupportFragmentManager(), "example dialog");
             }
         } );
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

    /**
     * Called when user apply request from "RequestJoinDialog" .
     */
    @Override
    public void applyRequest(Team team) {
        Map<String,Object> resultData = DataPass.passData;
        resultData.put(ConstantNames.TEAM,team);
        activityTransition.back(this,resultData);
        DataPass.passData = resultData;
        finish();
        this.teamID = team.getKeyID();
    }
}