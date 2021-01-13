package com.myapp.organizeteam;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.DataListener;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.Image;

import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class JoinRequestCard extends Fragment {

    DataExtraction dataExtraction;
    ActivityTransition transformation;
    Image image;
    CardView cardRequest;

    TextView tv_teamName;
    ImageView mv_teamLogo;

    Map<String, Object> userData;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.request_to_join_card, container, false);

        //allocating memory
        dataExtraction = new DataExtraction();
        transformation = new ActivityTransition();
        image = new Image();

        tv_teamName = v.findViewById(R.id.tv_teamName);
        mv_teamLogo = v.findViewById(R.id.mv_teamLogo);
        cardRequest = v.findViewById(R.id.cardRequest);

        //Get user`s data
        userData = DataPass.passData;
        user = (User) userData.get(ConstantNames.USER);

        userData = DataPass.passData;

        if(userData.get(ConstantNames.TEAM) != null)
        {
            final Team team = (Team)userData.get(ConstantNames.TEAM);
            tv_teamName.setText(""+team.getName());
            image.setImageUri(team.getLogo(),mv_teamLogo);

            cardRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDialogRequest(team);
                }
            });
        }

        //When data is changed, check if join request canceled. if its canceled, replace to other fragment.
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference(ConstantNames.USER_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataExtraction.hasChild(ConstantNames.USER_PATH, user.getKeyID(), ConstantNames.DATA_REQUEST_TO_JOIN, new ISavable() {
                    @Override
                    public void onDataRead(Object exist) {
                        if(!(boolean)exist)
                        {
                            JoinToTeamFragment joinToTeamFragment = new JoinToTeamFragment();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.menuContainer, joinToTeamFragment).addToBackStack(null).commitAllowingStateLoss();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return v;
    }

    private void createDialogRequest(final Team team) {
        //Build an alert dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(team.getName())
                .setMessage(team.getDescription())
                .setPositiveButton("Cancel Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        final AlertDialog alertDialog = builder.create();

        //Show the alert dialog
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Delete join request in firebase
                dataExtraction.deleteData(ConstantNames.USER_PATH, user.getKeyID(), ConstantNames.DATA_REQUEST_TO_JOIN, new DataListener() {
                    @Override
                    public void onDataDelete() {

                    }
                });
                dataExtraction.deleteValue(ConstantNames.TEAM_PATH, team.getKeyID(), ConstantNames.DATA_REQUEST_TO_JOIN, user.getKeyID(), new DataListener() {
                    @Override
                    public void onDataDelete() {

                    }
                });
                alertDialog.dismiss();
            }
        });
    }
}
