package com.myapp.organizeteam;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.Resources.Loading;
import com.myapp.organizeteam.Resources.Stepper;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class JoinToTeamFragment extends Fragment {

    DataExtraction dataExtraction;
    ActivityTransition transformation;

    Button btn_createTeam, btn_joinToTeam;

    Map<String, Object> userData;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.join_to_team_buttons_fragment, container, false);

        //allocating memory
        dataExtraction = new DataExtraction();
        transformation = new ActivityTransition();

        btn_createTeam = v.findViewById(R.id.btn_createTeam);
        btn_joinToTeam = v.findViewById(R.id.btn_joinToTeam);

        userData = DataPass.passData;
        user = (User) userData.get(ConstantNames.USER);

        //When data is changed, check if there is join request, if there is, replace to other fragment.
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference(ConstantNames.USER_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataExtraction.hasChild(ConstantNames.USER_PATH, user.getKeyID(), ConstantNames.DATA_REQUEST_TO_JOIN, new ISavable() {
                    @Override
                    public void onDataRead(Object exist) {
                        if((boolean)exist)
                        {
                            JoinRequestCard joinRequestCard = new JoinRequestCard();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.menuContainer, joinRequestCard).addToBackStack(null).commitAllowingStateLoss();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_joinToTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if there are list of teams already saved.
                if(userData.get(ConstantNames.TEAMS_LIST) == null)
                {
                    //If there are not, active scan to find all the teams and save them.
                    dataExtraction.getTeams(new ISavable() {
                        @Override
                        public void onDataRead(Object save) {
                            userData.put(ConstantNames.TEAMS_LIST,save);
                            transformation.goTo(getActivity(),SelectTeamActivity.class,false,userData,null);
                        }
                    });
                }
                else
                {
                    transformation.goTo(getActivity(),SelectTeamActivity.class,false,userData,null);
                }

            }
        });

        btn_createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transformation.goTo(getActivity(),CreateTeamActivity.class,false,userData,null);
            }
        });

        return v;
    }
}
