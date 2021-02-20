package com.myapp.organizeteam;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.myapp.organizeteam.Adapters.RolesListAdapter;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.myapp.organizeteam.DataManagement.Authorization.isManager;

public class RoleSelectionActivity extends AppCompatActivity implements RolesListAdapter.RoleSelectedListener {

    DataExtraction dataExtraction;
    ActivityTransition activityTransition;

    ArrayList<Role> rolesList;
    ArrayList<Role> selectedRoles;
    ArrayList<Role> permissionToPublish;

    ListView lv_roles;
    RolesListAdapter adapterRoles;

    Toolbar toolbar;

    Intent intent;

    String teamID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        dataExtraction = new DataExtraction();
        activityTransition = new ActivityTransition();

        lv_roles = findViewById(R.id.lv_users);

        toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selectedRoles = new ArrayList<>();

        intent = getIntent();
        teamID = intent.getStringExtra(ConstantNames.TEAM_KEY_ID);
        permissionToPublish = (ArrayList<Role>) intent.getSerializableExtra(ConstantNames.USER_PERMISSIONS_MEETING);

        createUsersList();
    }

    private void createUsersList()
    {
        if(permissionToPublish == null || isManager)
        {
            final DataExtraction dataExtraction = new DataExtraction();
            dataExtraction.getAllRolesByTeam(teamID, new ISavable() {
                @Override
                public void onDataRead(Object save) {
                    rolesList = (ArrayList<Role>)save;
                    setAdapter(rolesList);
                }
            });
        }
        else
        {
            rolesList = permissionToPublish;
            setAdapter(rolesList);
        }
    }

    private void setAdapter(ArrayList<Role> roles) {
        adapterRoles = new RolesListAdapter(this,R.layout.adapter_roles_selection_list,roles);
        lv_roles.setAdapter (adapterRoles);
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

    public void oc_doneSelecting(View view) {
        Map<String,Object> save = new HashMap<>();
        save.put(ConstantNames.ROLES_LIST,selectedRoles);
        activityTransition.back(this,save);
    }

    @Override
    public void roleSelected(Role role, boolean check) {
        if(check)
        {
            selectedRoles.add(role);
        }
        else
        {
            selectedRoles.remove(role);
        }
    }
}