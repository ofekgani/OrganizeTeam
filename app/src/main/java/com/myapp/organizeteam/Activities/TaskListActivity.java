package com.myapp.organizeteam.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.myapp.organizeteam.Adapters.MeetingsListAdapter;
import com.myapp.organizeteam.Adapters.TasksListAdapter;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Meeting;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.R;

import java.util.ArrayList;

public class TaskListActivity extends AppCompatActivity {

    ActivityTransition activityTransition;
    DataExtraction dataExtraction;

    ListView lv_tasks;
    Toolbar toolbar;

    TasksListAdapter adapter;

    Intent intent;
    Team team;
    ArrayList<String> tasksID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        activityTransition = new ActivityTransition();
        dataExtraction = new DataExtraction();

        lv_tasks = findViewById(R.id.lv_tasks);

        toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        intent = getIntent();
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
        tasksID = (ArrayList<String>) intent.getSerializableExtra("tasksID");

        dataExtraction.getDeletedTasksByKeys(team.getKeyID(), tasksID, new ISavable() {
            @Override
            public void onDataRead(Object save) {
                setAdapter((ArrayList<Mission>) save);
            }
        });
    }

    private void setAdapter(ArrayList<Mission> tasksList) {
        if(tasksList == null) return;
        adapter = new TasksListAdapter(this,R.layout.adapter_tasks_list,tasksList);
        lv_tasks.setAdapter ( adapter );
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