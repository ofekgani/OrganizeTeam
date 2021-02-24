package com.myapp.organizeteam;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myapp.organizeteam.Adapters.TasksListAdapter;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.myapp.organizeteam.DataManagement.Authorization.isManager;

public class TasksFragment extends Fragment{

    DataExtraction dataExtraction;

    ListView lv_taskList;
    TasksListAdapter adapter;
    Bundle bundle;

    ArrayList<Mission> tasksList;
    ArrayList<Role> rolesList;
    LayoutInflater inflater;

    Team team;
    User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks, container, false);
        this.inflater = inflater;

        dataExtraction = new DataExtraction();

        lv_taskList = v.findViewById(R.id.lv_tasks);

        bundle = getArguments();
        team = (Team) bundle.getSerializable ( ConstantNames.TEAM );
        user = (User) bundle.getSerializable ( ConstantNames.USER );
        rolesList = (ArrayList<Role>) bundle.getSerializable ( ConstantNames.USER_ROLES );

        createMeetingsList();

        return v;
    }

    private void createMeetingsList()
    {
        if(isManager)
        {
            dataExtraction.getAllTasksByTeam(team.getKeyID() ,new ISavable() {
                @Override
                public void onDataRead(Object save) {
                    tasksList = (ArrayList<Mission>)save;
                    setAdapter();
                }
            });
        }
        else
        {
            dataExtraction.getTasksByUser(user.getKeyID(), team.getKeyID(), new ISavable() {
                @Override
                public void onDataRead(Object save) {
                    ArrayList<String> meetingsID = (ArrayList<String>) save;
                    dataExtraction.getMeetingsByID(meetingsID,team.getKeyID(), user.getKeyID() ,new ISavable() {
                        @Override
                        public void onDataRead(Object save) {
                            tasksList = (ArrayList<Mission>)save;
                            setAdapter();
                        }
                    });
                }
            });

        }

    }

    private void setAdapter() {
        adapter = new TasksListAdapter(inflater.getContext(),R.layout.adapter_tasks_list, tasksList);
        lv_taskList.setAdapter ( adapter );
        adapter.notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(data != null)
        {
            if(resultCode == RESULT_OK && requestCode == 979)
            {
                Mission task = (Mission) data.getSerializableExtra(ConstantNames.TASK);
                tasksList.add(task);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
