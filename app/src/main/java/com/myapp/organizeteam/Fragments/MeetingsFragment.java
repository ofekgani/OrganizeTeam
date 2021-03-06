package com.myapp.organizeteam.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Fragment;
import android.widget.AdapterView;
import android.widget.ListView;

import com.myapp.organizeteam.Activities.MeetingActivity;
import com.myapp.organizeteam.Adapters.MeetingsListAdapter;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Meeting;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.myapp.organizeteam.DataManagement.Authorization.isManager;

public class MeetingsFragment extends Fragment{

    DataExtraction dataExtraction;
    ActivityTransition activityTransition;

    ListView lv_meetingList;
    MeetingsListAdapter adapter;
    Bundle bundle;

    ArrayList<Meeting> meetingsList;
    ArrayList<Role> rolesList;
    LayoutInflater inflater;

    Team team;
    User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meetings, container, false);
        this.inflater = inflater;

        dataExtraction = new DataExtraction();
        activityTransition = new ActivityTransition();

        lv_meetingList = v.findViewById(R.id.lv_meeting);

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
            dataExtraction.getAllMeetingsByTeam(team.getKeyID() ,new ISavable() {
                @Override
                public void onDataRead(Object save) {
                    meetingsList = (ArrayList<Meeting>)save;
                    setAdapter();
                }
            });
        }
        else
        {
            dataExtraction.getMeetingsByUser(user.getKeyID(), team.getKeyID(), new ISavable() {
                @Override
                public void onDataRead(Object save) {
                    ArrayList<String> meetingsID = (ArrayList<String>) save;
                    dataExtraction.getMeetingsByID(meetingsID,team.getKeyID(), user.getKeyID() ,new ISavable() {
                        @Override
                        public void onDataRead(Object save) {
                            meetingsList = (ArrayList<Meeting>)save;
                            setAdapter();
                        }
                    });
                }
            });

        }

    }

    private void setAdapter() {
        adapter = new MeetingsListAdapter(inflater.getContext(),R.layout.adapter_meetings_list,meetingsList);
        lv_meetingList.setAdapter ( adapter );
        lv_meetingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Meeting meeting = meetingsList.get(position);
                Map<String,Object> save = new HashMap<>();
                save.put(ConstantNames.MEETING,meeting);
                save.put(ConstantNames.TEAM,team);
                save.put(ConstantNames.USER,user);
                save.put("pos",position);
                activityTransition.goToWithResult(getActivity(), MeetingActivity.class,966,save,null);
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(data != null)
        {
            if(resultCode == RESULT_OK && requestCode == 976)
            {
                Meeting meeting = (Meeting) data.getSerializableExtra(ConstantNames.MEETING);
                meetingsList.add(meeting);
                adapter.notifyDataSetChanged();
            }
            if(resultCode == RESULT_OK && requestCode == 966)
            {
                Meeting meeting = (Meeting) data.getSerializableExtra(ConstantNames.MEETING);
                int position = data.getIntExtra("pos",-1);
                if(position >= 0)
                {
                    if (meeting == null)
                    {
                        meetingsList.remove(position);
                    }
                    else
                    {
                        meetingsList.set(position,meeting);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        }
    }

    public void updateList(int position) {
        meetingsList.remove(position);
        adapter.notifyDataSetChanged();
    }
}
