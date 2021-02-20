package com.myapp.organizeteam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateRoleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    InputManagement inputManagement;
    DataExtraction dataExtraction;
    ActivityTransition activityTransition;

    Spinner spinner;
    ConstraintLayout meetingSettings;
    CheckBox checkBox;
    EditText ed_roleName, ed_roleDescription;
    TextView btn_selectRolesPermission;

    Toolbar toolbar;

    Intent intent;

    int spinnerChoice;
    ArrayList<Role> teamRules;
    String teamID;
    boolean rulesSelected;

    Map<String,Object> save = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_role);

        inputManagement = new InputManagement();
        dataExtraction = new DataExtraction();
        activityTransition = new ActivityTransition();

        spinner = findViewById(R.id.spinner);
        meetingSettings = findViewById(R.id.meetingSettings);
        checkBox = findViewById(R.id.checkBox);
        ed_roleName = findViewById(R.id.ed_roleName);
        ed_roleDescription = findViewById(R.id.ed_roleDescription);
        btn_selectRolesPermission = findViewById(R.id.tv_btn_select);

        toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        intent = getIntent();
        teamID = intent.getStringExtra(ConstantNames.TEAM_KEY_ID);

        save = new HashMap<>();

        meetingSettings.setVisibility(View.GONE);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked())
                {
                    meetingSettings.setVisibility(View.VISIBLE);
                }
                else
                {
                    meetingSettings.setVisibility(View.GONE);
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.publishTo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinnerChoice = 0;

        if(spinnerChoice == 0)
        {
            btn_selectRolesPermission.setVisibility(View.GONE);
        }
        else
        {
            btn_selectRolesPermission.setVisibility(View.VISIBLE);
        }

        btn_selectRolesPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> save = new HashMap<>();
                save.put(ConstantNames.TEAM_KEY_ID,teamID);
                activityTransition.goToWithResult(CreateRoleActivity.this,RoleSelectionActivity.class,313,save,null);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spinnerChoice = i;
        if(spinnerChoice == 0)
        {
            btn_selectRolesPermission.setVisibility(View.GONE);
        }
        else
        {
            btn_selectRolesPermission.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void oc_createRole(View view) {
        if(inputManagement.isInputEmpty(ed_roleName)) return;

        final String name = inputManagement.getInput(ed_roleName);
        final String description = inputManagement.getInput(ed_roleDescription);

        save.put(ConstantNames.ROLE_NAME,name);
        save.put(ConstantNames.ROLE_DESCRIPTION,description);
        save.put(ConstantNames.TEAM_KEY_ID,teamID);

        activityTransition.goToWithResult(CreateRoleActivity.this, UserSelectionActivity.class,319,save,null);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null) return;
        if(resultCode == RESULT_OK && requestCode == 313)
        {
            save.put(ConstantNames.ROLES_LIST,data.getSerializableExtra(ConstantNames.ROLES_LIST));
            rulesSelected = true;
        }

        if(resultCode == RESULT_OK && requestCode == 319)
        {

            ArrayList<User> selectedUsers = (ArrayList<User>)data.getSerializableExtra(ConstantNames.USERS_LIST);
            ArrayList<Role> rolesPermission = (ArrayList<Role>)data.getSerializableExtra(ConstantNames.ROLES_LIST);
            if(selectedUsers.size() == 0) return;

            String name = inputManagement.getInput(ed_roleName);
            String description = inputManagement.getInput(ed_roleDescription);

            DatabaseReference rollDatabase = FirebaseDatabase.getInstance ().getReference ( ConstantNames.ROLE_PATH).child(teamID);
            String roleID = rollDatabase.push ().getKey ();

            ArrayList<String> usersID = new ArrayList<>();
            for(User user : selectedUsers)
            {
                usersID.add(user.getKeyID());
            }

            Role role = new Role(roleID,teamID,name,description,usersID);
            dataExtraction.setObject(ConstantNames.ROLE_PATH,teamID,roleID,role);

            if(rolesPermission != null)
            {
                for(Role r : rolesPermission)
                {
                    rollDatabase.child(roleID).child(ConstantNames.DATA_ROLE_MEETING_PERMISSION).push().setValue(r.getKeyID());
                }
            }
            else if(checkBox.isChecked() && spinnerChoice == 0)
            {
                rollDatabase.child(roleID).child(ConstantNames.DATA_ROLE_MEETING_PERMISSION).setValue("All");
            }

            Map<String,Object> saveRole = new HashMap<>();
            saveRole.put(ConstantNames.ROLE,role);
            activityTransition.back(CreateRoleActivity.this,saveRole);
        }

    }
}