package com.myapp.organizeteam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Role;

public class RoleInformationActivity extends AppCompatActivity {

    TextView tv_description;

    Toolbar toolbar;

    Intent intent;
    Role role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_information);

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
}