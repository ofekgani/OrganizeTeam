package com.myapp.organizeteam.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.R;

import java.util.HashMap;
import java.util.Map;

public class EditRoleActivity extends AppCompatActivity {

    ActivityTransition activityTransition;
    InputManagement inputManagement;
    DataExtraction dataExtraction;

    EditText ed_name, ed_description;
    Toolbar toolbar;

    Intent intent;
    Role role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_role);

        activityTransition = new ActivityTransition();
        dataExtraction = new DataExtraction();
        inputManagement = new InputManagement();

        ed_name =findViewById(R.id.ed_name);
        ed_description =findViewById(R.id.ed_description);
        toolbar = findViewById(R.id.appBarLayout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        intent = getIntent();
        role = (Role) intent.getSerializableExtra(ConstantNames.ROLE);

        ed_name.setText(""+role.getName());
        ed_description.setText(""+role.getDescription());
    }

    public void oc_confirm(View view) {
        if(inputManagement.isInputEmpty(ed_name)) return;

        role.setName(inputManagement.getInput(ed_name));
        role.setDescription(inputManagement.getInput(ed_description));

        dataExtraction.setNewData(ConstantNames.ROLE_PATH,role.getTeamID(),role.getKeyID(),ConstantNames.DATA_ROLE_NAME,role.getName());
        dataExtraction.setNewData(ConstantNames.ROLE_PATH,role.getTeamID(),role.getKeyID(),ConstantNames.DATA_ROLE_DESCRIPTION,role.getDescription());

        Map<String, Object> save = new HashMap<>();
        save.put(ConstantNames.ROLE,role);
        activityTransition.back(EditRoleActivity.this,save);
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
}