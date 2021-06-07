package com.myapp.organizeteam.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

public class RoleInformationActivity extends AppCompatActivity {

    ActivityTransition activityTransition;
    DataExtraction dataExtraction;

    TextView tv_description;

    Toolbar toolbar;

    Intent intent;
    Role role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_information);

        activityTransition = new ActivityTransition();
        dataExtraction = new DataExtraction();

        tv_description = findViewById(R.id.tv_roleDescription);
        toolbar = findViewById(R.id.appBarLayout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        intent = getIntent();
        role = (Role) intent.getSerializableExtra(ConstantNames.ROLE);

        getSupportActionBar().setTitle(""+role.getName());
        tv_description.setText(""+role.getDescription());
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

    public void oc_editRole(View view) {
        Map<String, Object> save = new HashMap<>();
        save.put(ConstantNames.ROLE,role);
        activityTransition.goToWithResult(RoleInformationActivity.this,EditRoleActivity.class,111,save,null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null)
        {
            if(resultCode == RESULT_OK && requestCode == 111)
            {
                role = (Role) data.getSerializableExtra(ConstantNames.ROLE);
                getSupportActionBar().setTitle(""+role.getName());
                tv_description.setText(""+role.getDescription());
            }
        }
    }
}