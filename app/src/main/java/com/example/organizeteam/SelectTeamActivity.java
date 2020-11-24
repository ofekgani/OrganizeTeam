package com.example.organizeteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.organizeteam.Core.ActivityTransition;
import com.example.organizeteam.Core.ConstantNames;
import com.example.organizeteam.Core.Team;
import com.example.organizeteam.Core.User;
import com.example.organizeteam.DataManagement.Authorization;
import com.example.organizeteam.DataManagement.DataExtraction;
import com.example.organizeteam.DataManagement.ISavable;
import com.example.organizeteam.Dialogs.RequestJoinDialog;
import com.example.organizeteam.Resources.Image;
import com.example.organizeteam.Adapters.TeamListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectTeamActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, RequestJoinDialog.DialogListener {

    ListView listView;
    ImageView mv_teamLogo;
    TextView tv_teamName, btn_cancelRequest;

    ActivityTransition activityTransition;
    Authorization authorization;
    DataExtraction dataExtraction;
    Image image;

    Intent intent;

    ArrayList<Team> teamList;
    TeamListAdapter adapter;

    User user;
    String userID;
    String teamID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_select_team );

        listView = findViewById ( R.id.lv_teamsList );
        tv_teamName = findViewById(R.id.tv_teamName);
        mv_teamLogo = findViewById(R.id.mv_teamLogo);
        btn_cancelRequest = findViewById(R.id.tv_cancelRequest);

        //allocating memory
        activityTransition = new ActivityTransition ();
        authorization = new Authorization ();
        dataExtraction = new DataExtraction ();
        image = new Image();

        intent = getIntent (  );

        //get all teams
        teamList = (ArrayList<Team>)intent.getSerializableExtra ( ConstantNames.TEAMS_LIST);

        //get user information
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
                                    updateTeamInformation((Team) save);
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
     * Update the title and logo of the team that got join request by user.
     * @param save The team information to update.
     */
    private void updateTeamInformation(Team save) {
        Team team = save;
        tv_teamName.setText(""+team.getName());
        image.setImageUri(team.getLogo(),mv_teamLogo);
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

         //get host`s token
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

    /**
     * Called when user apply request from "RequestJoinDialog" .
     * @param teamID team`s id.
     * @param teamName team`s name.
     * @param teamLogo team`s logo.
     */
    @Override
    public void applyRequest(String teamID,String teamName,String teamLogo) {
        this.teamID = teamID;
        tv_teamName.setText(""+teamName);
        image.setImageUri(teamLogo,mv_teamLogo);
    }

    public void oc_cancelRequest(View view) {
        if(teamID == null) return;
        dataExtraction.hasChild(ConstantNames.USER_PATH, userID, ConstantNames.DATA_REQUEST_TO_JOIN, new ISavable() {
            @Override
            public void onDataRead(Object save) {
                if((boolean)save)
                {
                    //Delete join request in firebase
                    dataExtraction.deleteData(ConstantNames.USER_PATH,userID,ConstantNames.DATA_REQUEST_TO_JOIN);
                    dataExtraction.deleteValue(ConstantNames.TEAM_PATH,teamID,ConstantNames.DATA_REQUEST_TO_JOIN,userID);

                    //Update the title and logo
                    teamID = null;
                    tv_teamName.setText("No join request.");
                    mv_teamLogo.setImageResource(R.drawable.massivemultiplayer);
                    Toast.makeText(SelectTeamActivity.this,"has child",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(SelectTeamActivity.this,"has not child",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}