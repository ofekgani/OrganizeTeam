package com.myapp.organizeteam;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.widget.ListView;
import android.widget.TextView;

import com.myapp.organizeteam.Adapters.MeetingsListAdapter;
import com.myapp.organizeteam.Adapters.MyStepperAdapter;
import com.myapp.organizeteam.Adapters.UsersListAdapter;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Meeting;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;

import java.util.ArrayList;
import java.util.zip.Inflater;

import javax.xml.transform.Result;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment{

    ListView lv_meetingList;
    MeetingsListAdapter adapter;
    Bundle bundle;

    ArrayList<Meeting> meetingsList;
    LayoutInflater inflater;

    Team team;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        this.inflater = inflater;

        lv_meetingList = v.findViewById(R.id.lv_meeting);

        bundle = getArguments();
        team = (Team) bundle.getSerializable ( ConstantNames.TEAM );

        createMeetingsList();

        return v;
    }

    private void createMeetingsList()
    {
        DataExtraction dataExtraction = new DataExtraction();
        dataExtraction.getAllMeetingsByTeam(team.getKeyID() ,new ISavable() {
            @Override
            public void onDataRead(Object save) {
                meetingsList = (ArrayList<Meeting>)save;
                setAdapter(meetingsList);
            }
        });
    }

    private void setAdapter(ArrayList<Meeting> meetings) {
        adapter = new MeetingsListAdapter(inflater.getContext(),R.layout.adapter_meetings_list,meetings);
        lv_meetingList.setAdapter ( adapter );
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
        }
    }

    public void updateList(int position) {
        meetingsList.remove(position);
        adapter.notifyDataSetChanged();
    }
}
