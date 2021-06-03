package com.myapp.organizeteam.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.organizeteam.Adapters.UsersListAdapter;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserSelectionActivity extends AppCompatActivity implements UsersListAdapter.UserSelectedListener {

    DataExtraction dataExtraction;
    ActivityTransition activityTransition;

    ArrayList<User> usersList;
    ArrayList<User> selectedUsers;

    ArrayList<Role> rolesPermission;
    String publishPermission;

    ListView lv_users;
    UsersListAdapter adapterUsers;

    Toolbar toolbar;

    Intent intent;

    String teamID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);

        dataExtraction = new DataExtraction();
        activityTransition = new ActivityTransition();

        lv_users = findViewById(R.id.lv_users);

        toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selectedUsers = new ArrayList<>();

        intent = getIntent();
        teamID = intent.getStringExtra(ConstantNames.TEAM_KEY_ID);

        if(intent.getSerializableExtra(ConstantNames.ROLES_LIST) != null)
        {
            rolesPermission = (ArrayList<Role>) intent.getSerializableExtra(ConstantNames.ROLES_LIST);
        }
        else if(intent.getSerializableExtra(ConstantNames.ROLE_MEETING_PERMISSION) != null)
        {
            publishPermission = (String) intent.getSerializableExtra(ConstantNames.ROLE_MEETING_PERMISSION);
        }

        if(intent.getSerializableExtra(ConstantNames.USERS_LIST) != null)
        {
            usersList = (ArrayList<User>) intent.getSerializableExtra(ConstantNames.USERS_LIST);
        }

        createUsersList();
    }

    public void oc_createRole(View view) {
        Map<String,Object> save = new HashMap<>();
        save.put(ConstantNames.ROLES_LIST,rolesPermission);
        save.put(ConstantNames.USERS_LIST,selectedUsers);
        activityTransition.back(this,save);
    }

    private void createUsersList()
    {
        if(usersList == null)
        {
            final DataExtraction dataExtraction = new DataExtraction();
            dataExtraction.getAllUsersByTeam(teamID, ConstantNames.DATA_USERS_LIST,new ISavable() {
                @Override
                public void onDataRead(Object save) {
                    ArrayList<User> users = (ArrayList<User>)save;
                    setAdapter(users);
                }
            });
        }
        else
        {
            setAdapter(usersList);
        }

    }

    private void setAdapter(ArrayList<User> users) {
        adapterUsers = new UsersListAdapter(this,R.layout.adapter_users_list,users);
        lv_users.setAdapter ( adapterUsers );
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

    @Override
    public void userSelected(User user, boolean check) {
        if(check)
        {
            selectedUsers.add(user);
        }
        else
        {
            selectedUsers.remove(user);
        }
    }
}