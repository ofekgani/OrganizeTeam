package com.myapp.organizeteam;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.myapp.organizeteam.Adapters.TasksListAdapter;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Dialogs.TaskDialog;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.myapp.organizeteam.DataManagement.Authorization.isManager;

public class TasksFragment extends Fragment {

    DataExtraction dataExtraction;

    ListView lv_taskList;
    TasksListAdapter adapter;
    Bundle bundle;

    ArrayList<Mission> tasksList;
    ArrayList<Role> rolesList;
    LayoutInflater inflater;
    TeamPageActivity myContext;

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

        createTasksList();

        return v;
    }

    private void createTasksList()
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
                    ArrayList<String> tasksID = (ArrayList<String>) save;
                    dataExtraction.getTasksByID(tasksID,team.getKeyID(), user.getKeyID() ,new ISavable() {
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
        lv_taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ConstantNames.TASK,tasksList.get(i));
                DialogFragment taskDialog = new TaskDialog();
                taskDialog.setArguments ( bundle );

                //open the dialog
                taskDialog.show(myContext.getSupportFragmentManager(), "dialog");
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(data != null)
        {
            if(resultCode == RESULT_OK && requestCode == 979)
            {
                if(tasksList == null) return;
                Mission task = (Mission) data.getSerializableExtra(ConstantNames.TASK);
                tasksList.add(task);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        myContext = (TeamPageActivity) context;
    }
}
