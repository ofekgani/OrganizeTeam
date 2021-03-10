package com.myapp.organizeteam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.myapp.organizeteam.Adapters.TasksListAdapter;
import com.myapp.organizeteam.Adapters.UsersListAdapterRel;
import com.myapp.organizeteam.Adapters.UsersRequestsListAdapterRel;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Dialogs.TaskDialog;

import java.util.ArrayList;

import static com.myapp.organizeteam.DataManagement.Authorization.isManager;

public class SubmitsListActivity extends AppCompatActivity {

    DataExtraction dataExtraction;

    RecyclerView lv_users;
    private RecyclerView.Adapter usersAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<User> usersList;

    Intent intent;
    Team team;
    Mission mission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submits_list);

        dataExtraction = new DataExtraction();

        lv_users = findViewById(R.id.lv_submits);

        intent = getIntent();
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
        mission = (Mission) intent.getSerializableExtra(ConstantNames.TASK);

        dataExtraction.getSubmitters(team.getKeyID(), mission.getKeyID(), new ISavable() {
            @Override
            public void onDataRead(Object save) {
                usersList = (ArrayList<User>) save;
                setAdapter(usersList);
            }
        });

    }

    private void setAdapter(ArrayList<User> users) {
        if(users != null)
        {
            lv_users.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
            usersAdapter = new UsersListAdapterRel(users);
            lv_users.setLayoutManager(mLayoutManager);
            lv_users.setAdapter(usersAdapter);
        }
    }
}