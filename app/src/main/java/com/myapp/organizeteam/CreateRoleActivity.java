package com.myapp.organizeteam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateRoleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    InputManagement inputManagement;
    DataExtraction dataExtraction;
    ActivityTransition activityTransition;

    Spinner sp_createMeetingTo,sp_createTaskTo;
    ConstraintLayout meetingSettings,taskSettings;
    CheckBox cb_createMeeting,cb_createTask;
    EditText ed_roleName, ed_roleDescription;
    TextView btn_selectRolesPermissionMeeting, btn_selectRolesPermissionTask;
    Toolbar toolbar;

    Intent intent;

    int meetingSpinnerChoice,taskSpinnerChoice;
    String teamID;
    boolean rulesSelected;

    Map<String,Object> save = new HashMap<>();

    ArrayList<Role> rolesPublishMeetings,rolesPublishTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_role);

        inputManagement = new InputManagement();
        dataExtraction = new DataExtraction();
        activityTransition = new ActivityTransition();

        sp_createMeetingTo = findViewById(R.id.sp_createMeetingTo);
        meetingSettings = findViewById(R.id.meetingSettings);
        cb_createMeeting = findViewById(R.id.cb_createMeeting);
        ed_roleName = findViewById(R.id.ed_roleName);
        ed_roleDescription = findViewById(R.id.ed_roleDescription);
        btn_selectRolesPermissionMeeting = findViewById(R.id.tv_btn_select_meeting);
        sp_createTaskTo = findViewById(R.id.sp_createTaskTo);
        taskSettings = findViewById(R.id.taskSettings);
        cb_createTask = findViewById(R.id.cb_createTask);
        btn_selectRolesPermissionTask = findViewById(R.id.tv_btn_select_task);

        toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        intent = getIntent();
        teamID = intent.getStringExtra(ConstantNames.TEAM_KEY_ID);
        save = new HashMap<>();

        configuringSetting(meetingSettings, cb_createMeeting);
        configuringSetting(taskSettings, cb_createTask);

        configuringSpinner(sp_createMeetingTo);
        meetingSpinnerChoice = 0;
        checkSpinnerChoice(meetingSpinnerChoice, btn_selectRolesPermissionMeeting);

        configuringSpinner(sp_createTaskTo);
        taskSpinnerChoice = 0;
        checkSpinnerChoice(taskSpinnerChoice, btn_selectRolesPermissionTask);

        btn_selectRolesPermissionMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> save = new HashMap<>();
                save.put(ConstantNames.TEAM_KEY_ID,teamID);
                activityTransition.goToWithResult(CreateRoleActivity.this,RoleSelectionActivity.class,313,save,null);
            }
        });

        btn_selectRolesPermissionTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> save = new HashMap<>();
                save.put(ConstantNames.TEAM_KEY_ID,teamID);
                activityTransition.goToWithResult(CreateRoleActivity.this,RoleSelectionActivity.class,314,save,null);
            }
        });
    }

    private void checkSpinnerChoice(int spinnerChoice, TextView textView) {
        if (spinnerChoice == 0) {
            textView.setVisibility(View.INVISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void configuringSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> sp_meetingAdapter = ArrayAdapter.createFromResource(this, R.array.publishTo, android.R.layout.simple_spinner_item);
        sp_meetingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sp_meetingAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void configuringSetting(final ConstraintLayout constraintLayout, final CheckBox checkBox) {
        constraintLayout.setVisibility(View.INVISIBLE);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    constraintLayout.setVisibility(View.VISIBLE);
                } else {
                    constraintLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId())
        {
            case R.id.sp_createMeetingTo:
                meetingSpinnerChoice = i;
                checkSpinnerChoice(meetingSpinnerChoice, btn_selectRolesPermissionMeeting);
                break;

            case R.id.sp_createTaskTo:
                taskSpinnerChoice = i;
                checkSpinnerChoice(taskSpinnerChoice, btn_selectRolesPermissionTask);
                break;
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
            rolesPublishMeetings = (ArrayList<Role>) data.getSerializableExtra(ConstantNames.ROLES_LIST);
            rulesSelected = true;
        }
        if(resultCode == RESULT_OK && requestCode == 314)
        {
            save.put(ConstantNames.ROLES_LIST,data.getSerializableExtra(ConstantNames.ROLES_LIST));
            rolesPublishTasks = (ArrayList<Role>) data.getSerializableExtra(ConstantNames.ROLES_LIST);
            rulesSelected = true;
        }

        if(resultCode == RESULT_OK && requestCode == 319)
        {
            //List of users selected for this role
            ArrayList<User> selectedUsers = (ArrayList<User>)data.getSerializableExtra(ConstantNames.USERS_LIST);

            //Check if users are selected.
            if(selectedUsers.isEmpty() || selectedUsers == null) return;

            //Get user`s input
            String name = inputManagement.getInput(ed_roleName);
            String description = inputManagement.getInput(ed_roleDescription);

            //Create key to role
            DatabaseReference rollDatabase = FirebaseDatabase.getInstance ().getReference ( ConstantNames.ROLE_PATH).child(teamID);
            String roleID = rollDatabase.push ().getKey ();

            //Get users`s keysID to save into the firebase.
            ArrayList<String> usersID = new ArrayList<>();
            for(User user : selectedUsers)
            {
                usersID.add(user.getKeyID());
            }

            //Create role and add its to firebase
            Role role = new Role(roleID,teamID,name,description,usersID);
            dataExtraction.setObject(ConstantNames.ROLE_PATH,teamID,roleID,role);

            //Check if to this role has permission to publish meetings, if has, add all the roles that can be published to
            addPermissions(rollDatabase, roleID, rolesPublishMeetings, ConstantNames.DATA_ROLE_MEETING_PERMISSION, cb_createMeeting, meetingSpinnerChoice);
            addPermissions(rollDatabase, roleID, rolesPublishTasks, ConstantNames.DATA_ROLE_TASK_PERMISSION, cb_createTask, taskSpinnerChoice);

            DatabaseReference userDatabase = FirebaseDatabase.getInstance ().getReference ( ConstantNames.USER_ACTIVITY_PATH).child(teamID);
            for(String id : usersID)
            {
                userDatabase.child(id).child(ConstantNames.DATA_USER_ROLES).push().setValue(roleID);
            }

            Map<String,Object> saveRole = new HashMap<>();
            saveRole.put(ConstantNames.ROLE,role);
            activityTransition.back(CreateRoleActivity.this,saveRole);
        }

    }

    private void addPermissions(DatabaseReference databaseReference, String roleID, ArrayList<Role> roles, String path, CheckBox checkBox, int spinnerChoice) {
        if (roles != null && !roles.isEmpty()) {
            for (Role r : roles) {
                databaseReference.child(roleID).child(path).push().setValue(r.getKeyID());
            }
        } else if (checkBox.isChecked() && spinnerChoice == 0) {
            databaseReference.child(roleID).child(path).setValue("All");
        }
    }
}