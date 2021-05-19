package com.myapp.organizeteam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.organizeteam.Adapters.JoinRequestCard;
import com.myapp.organizeteam.Adapters.JoinToTeamFragment;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.DataPass;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.FileManage;
import com.myapp.organizeteam.Resources.Loading;
import com.myapp.organizeteam.Resources.Stepper;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.HashMap;
import java.util.Map;

public class StepFiveRegisterFragment extends Fragment implements Step {

    Authorization authorization;
    InputManagement input;
    Loading progressBar;
    Stepper stepper;
    FileManage fileManage;
    DataExtraction dataExtraction;
    ActivityTransition transformation;

    StepperLayout mStepperLayout;

    TextView tv_prevStep, tv_name, tv_phone, tv_email;
    ImageView mv_userLogo;

    Map<String, Object> userData;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register_step5, container, false);

        //allocating memory
        authorization = new Authorization ();
        input = new InputManagement ();
        stepper = new Stepper();
        progressBar = new Loading ( );
        fileManage = new FileManage();
        dataExtraction = new DataExtraction();
        transformation = new ActivityTransition();

        tv_prevStep = v.findViewById(R.id.tv_prevStep);
        tv_name = v.findViewById(R.id.tv_userName);
        tv_email = v.findViewById(R.id.tv_userEmail);
        tv_phone = v.findViewById(R.id.tv_userPhone);
        mv_userLogo = v.findViewById(R.id.mv_userLogo);

        mStepperLayout = stepper.getStepperLayout(container,R.id.stepperLayout);

        tv_prevStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataExtraction.hasChild(ConstantNames.USER_PATH, user.getKeyID(), ConstantNames.DATA_USER_PHONE, new ISavable() {
                    @Override
                    public void onDataRead(Object exist) {
                        if((boolean)exist)
                        {
                            stepper.go(mStepperLayout,2);
                        }
                        else
                        {
                            stepper.goBack(mStepperLayout);
                        }
                    }
                });
            }
        });

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_PATH).child(user.getKeyID());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(ConstantNames.DATA_USER_TEAM))
                {

                    dataExtraction.getTeamDataByID(snapshot.child(ConstantNames.DATA_USER_TEAM).getValue().toString(), new ISavable() {
                        @Override
                        public void onDataRead(Object save) {
                            final Team team = (Team) save;
                            dataExtraction.getUserDataByID(team.getHost(), new ISavable() {
                                @Override
                                public void onDataRead(Object save) {
                                    ActivityTransition activityTransition = new ActivityTransition();
                                    User host = (User) save;
                                    Map<String, Object> saves = new HashMap<>();
                                    saves.put(ConstantNames.USER,user);
                                    saves.put(ConstantNames.TEAM_HOST,host);
                                    saves.put(ConstantNames.TEAM,team);
                                    activityTransition.goTo(getActivity(),TeamPageActivity.class,true, saves,null);
                                }
                            });
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return v;
    }

    /**
     * Update UI
     * @param user A user object used to update the ui.
     */
    private void updateUserData(User user) {
        if(user == null) return;
        if(user.getFullName() != null)
        {
            tv_name.setText(user.getFullName());
        }
        if(user.getEmail() != null)
        {
            tv_email.setText(user.getEmail());
        }
        if(user.getPhone() != null)
        {
            tv_phone.setText(user.getPhone());
        }
        if(user.getLogo() != null && user.getLogo() != "")
        {
            fileManage.setImageUri(user.getLogo(),mv_userLogo);
        }
    }

    /**
     * Create drawer_navigation_menu fragment by user`s information.
     * If user has not team or join request, so adapt "JoinToTeamFragment".
     * If user has team or join request, so adapt "JoinRequestCard".
     * @param keyID user`s keyID to find the user.
     */
    private void menuAdapt(String keyID) {
        //Check if to user exist team or join request to adapt the correct fragment.
        dataExtraction.hasChild(ConstantNames.USER_PATH, keyID, ConstantNames.DATA_REQUEST_TO_JOIN, new ISavable() {
            @Override
            public void onDataRead(Object exist) {
                if(!(boolean)exist)
                {
                    JoinToTeamFragment joinToTeamFragment = new JoinToTeamFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.menuContainer, joinToTeamFragment).addToBackStack(null).commit();

                }
                else
                {
                    JoinRequestCard joinRequestCard = new JoinRequestCard();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.menuContainer, joinRequestCard).addToBackStack(null).commit();
                }
            }
        });
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        return null;
    }

    @Override
    public void onSelected() {
        //update UI when selected
        userData = DataPass.passData;
        if(userData != null && userData.size() != 0)
        {
            if(userData.get(ConstantNames.USER) != null)
            {
                user = (User)userData.get(ConstantNames.USER);
                updateUserData(user);
            }
        }

        //Adapt the correct fragment
        if(user != null && user.getKeyID() != null)
        {
            menuAdapt(user.getKeyID());
        }
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }
}
