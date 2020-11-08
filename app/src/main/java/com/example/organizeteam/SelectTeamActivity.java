package com.example.organizeteam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.organizeteam.Core.ActivityTransition;
import com.example.organizeteam.Core.ConstantNames;
import com.example.organizeteam.Core.Team;
import com.example.organizeteam.Core.User;
import com.example.organizeteam.DataManagement.Authorization;
import com.example.organizeteam.DataManagement.DataExtraction;
import com.example.organizeteam.DataManagement.ISavable;
import com.example.organizeteam.Resources.ExampleDialog;
import com.example.organizeteam.Resources.TeamListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectTeamActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;

    ActivityTransition activityTransition;
    Authorization authorization;
    DataExtraction dataExtraction;

    Intent intent;

    ArrayList<Team> teamList;
    TeamListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_select_team );

        listView = findViewById ( R.id.lv_teamsList );

        //allocating memory
        activityTransition = new ActivityTransition ();
        authorization = new Authorization ();
        dataExtraction = new DataExtraction ();

        intent = getIntent (  );

        //get all teams
        teamList = (ArrayList<Team>)intent.getSerializableExtra ( ConstantNames.TEAMS );

        createTeamList ();
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        //Get team information by choice item from the list
         final String teamName = teamList.get ( i ).getName ();
         final String teamDescription = teamList.get ( i ).getDescription ();
         final String hostID = teamList.get ( i ).getHost ();

         //Get user information
         User user = (User)intent.getSerializableExtra ( ConstantNames.USER );
         final String userName = user.getFullName ();
         final String userID = user.getKeyID ();

         //get host`s token
         dataExtraction.getToken ( hostID, new ISavable () {
             @Override
             public void onDataRead(Object save) {
                 String hostToken =  (String)save;

                 //Create open dialog
                 ExampleDialog exampleDialog = new ExampleDialog();

                 //Save all the important information to dialog
                 Map<String,String> values = new HashMap<> ();
                 values.put ( ConstantNames.TEAM_NAME,teamName );
                 values.put ( ConstantNames.TEAM_DESCRIPTION,teamDescription );
                 values.put ( ConstantNames.USER_NAME,userName );
                 values.put ( ConstantNames.USER_KEY_ID,userID );
                 values.put ( ConstantNames.HOST_ID,hostID );
                 values.put ( ConstantNames.HOST_TOKEN,hostToken );

                 Bundle bundle = activityTransition.CreateBundle ( values );
                 exampleDialog.setArguments ( bundle );

                 //open the dialog
                 exampleDialog.show(getSupportFragmentManager(), "example dialog");
             }
         } );
    }
}