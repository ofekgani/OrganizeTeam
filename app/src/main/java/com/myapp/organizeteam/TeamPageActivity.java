package com.myapp.organizeteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.organizeteam.Adapters.UsersListAdapter;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.Core.User;

import java.util.ArrayList;

public class TeamPageActivity extends AppCompatActivity implements UsersListAdapter.AdapterListener{

    TextView tv_teamName, tv_managerName;
    ImageView mv_teamLogo, mv_managerLogo;
    ListView lv_users;

    ActivityTransition activityTransition;
    Image image;

    Intent intent;
    UsersListAdapter adapter;

    String teamName,teamLogo,managerName, managerLogo;

    User user;
    User manager;
    Team team;

    ArrayList<User> requestsList;
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

        if(isManager())
        {
            createUsersList();
        }

    }

    private boolean isManager() {
        user = (User)intent.getSerializableExtra(ConstantNames.USER);
        if(manager.getKeyID().equals(user.getKeyID()))
        {
            Toast.makeText(this,"You are the manager!",Toast.LENGTH_SHORT).show();
            return true;
        }
        else
        {
            Toast.makeText(this,"You are not the manager!",Toast.LENGTH_SHORT).show();
            return false;
        }
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

    private void createUsersList()
    {
        final DataExtraction dataExtraction = new DataExtraction();
        dataExtraction.getAllUsersByTeam(team.getKeyID(), ConstantNames.DATA_REQUEST_TO_JOIN,new ISavable() {
            @Override
            public void onDataRead(Object save) {
                requestsList = (ArrayList<User>)save;
                dataExtraction.getAllUsersByTeam(team.getKeyID(), ConstantNames.DATA_USERS_AT_TEAM, new ISavable() {
                            @Override
                            public void onDataRead(Object save) {
                                usersList = (ArrayList<User>)save;
                                requestsList.addAll(usersList);
                                setAdapter(requestsList);
                            }
                        });

            }
        });
    }

    private void setAdapter(ArrayList<User> users) {
        adapter = new UsersListAdapter(this,R.layout.adapter_users_list,users,team.getKeyID());
        lv_users.setAdapter ( adapter );
    }

    public void oc_signOut(View view) {
        Authorization authorization = new Authorization ();
        authorization.singOut ( this );
    }

    /**
     * called any time the users list has change.
     * @param position The item from the list to delete.
     */
    @Override
    public void updateList(boolean accept,int position) {
        User user = requestsList.get(position);
        requestsList.remove(position);
        if(accept)
        {
            requestsList.add(user);
        }

        adapter.notifyDataSetChanged();
    }
}