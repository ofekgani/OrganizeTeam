package com.myapp.organizeteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.DataManagement.IRegister;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.Loading;
import com.myapp.organizeteam.Resources.Transformation;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class MainActivity extends AppCompatActivity {

    Authorization authorization;
    InputManagement input;
    DataExtraction dataExtraction;
    Transformation transformation;
    ActivityTransition activityTransition;
    Loading progressBar;

    EditText ed_email, ed_password;
    ProgressBar pb_singIn;

    Map<String,Object> userInfo;
    int step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        //References
        ed_email = findViewById ( R.id.ed_email );
        ed_password = findViewById ( R.id.ed_Password );
        pb_singIn = findViewById ( R.id.pb_singIn );

        //allocating memory
        authorization = new Authorization ();
        transformation = new Transformation ();
        activityTransition = new ActivityTransition ();
        input = new InputManagement ();
        dataExtraction = new DataExtraction ();
        progressBar = new Loading ( );

        progressBar.setVisible ( pb_singIn,false );
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_singIn(final View view) {
        //check if user input is valid.
        if(!input.isInputValid ( ed_email, ed_password ))
            return;

        String email = input.getInput(ed_email);
        String password = input.getInput(ed_password);

        //Connect to system.
        authorization.login(email, password, new IRegister() {
            @Override
            public void onProcess() {
                progressBar.setVisible(pb_singIn,true);
            }

            @Override
            public void onDone(boolean successful, String message) {
                if(successful)
                {
                    step = 0; //Default step

                    dataExtraction.hasChild(ConstantNames.USER_PATH, authorization.getUserID(), ConstantNames.DATA_USER_TEAMS, new ISavable() {
                        @Override
                        public void onDataRead(Object exist) {
                            if((boolean)exist)
                            {
                                dataExtraction.getCurrentUserData(new ISavable() {
                                    @Override
                                    public void onDataRead(Object save) {
                                        connectToTeam((Map<String, Object>) save);
                                    }
                                });
                            }
                            else
                            {
                                //Check if user verified his email.
                                if(!authorization.isEmailVerified())
                                {
                                    //If email not verified go to step 2
                                    register();
                                }
                                else
                                {
                                    //If email is verified check if user set his name by checking if the user exist on firebase realtime.
                                    dataExtraction.hasChild(ConstantNames.USER_PATH, authorization.getUserID(), new ISavable() {
                                        @Override
                                        public void onDataRead(Object exist) {
                                            //If user exist check if his name exist on firebase realtime
                                            if((boolean)exist)
                                            {
                                                dataExtraction.hasChild(ConstantNames.USER_PATH, authorization.getUserID(), ConstantNames.DATA_USER_NAME, new ISavable() {
                                                    @Override
                                                    public void onDataRead(final Object exist) {
                                                        dataExtraction.getCurrentUserData(new ISavable() {
                                                            @Override
                                                            public void onDataRead(Object save) {

                                                                //Save all user`s information to next use.
                                                                userInfo = (Map<String,Object>)save;

                                                                //if user`s name exist go to last step
                                                                if((boolean)exist)
                                                                {
                                                                    step = 4;
                                                                    dataExtraction.hasChild(ConstantNames.USER_PATH, authorization.getUserID(), ConstantNames.DATA_REQUEST_TO_JOIN, new ISavable() {
                                                                        @Override
                                                                        public void onDataRead(Object exist) {
                                                                            if((boolean) exist)
                                                                            {
                                                                                goToStepFiveWithRequestJoin();
                                                                            }
                                                                            else
                                                                            {
                                                                                connect(userInfo);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                else
                                                                {
                                                                    step = 2;
                                                                    connect(userInfo);
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                            else
                                            {
                                                step = 2;

                                                //Save all user`s information to next use.
                                                userInfo = getUser();

                                                connect(userInfo);
                                            }
                                        }
                                    });

                                }
                            }
                        }
                    });
                }
                else
                {
                    progressBar.setVisible(pb_singIn,false);
                    Snackbar.make(view,""+message, BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goToStepFiveWithRequestJoin() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_PATH).child(authorization.getUserID()).child(ConstantNames.DATA_REQUEST_TO_JOIN);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //If request exist, get information about the team that got request.
                String teamID = snapshot.getValue().toString();
                dataExtraction.getTeamDataByID(teamID, new ISavable() {
                    @Override
                    public void onDataRead(Object save) {
                        userInfo.put(ConstantNames.TEAM,save);
                        connect(userInfo);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Go to fist step and start register
     */
    private void register() {
        step = 1;
        Map<String,Object> userInfo = getUser();
        connect(userInfo);
    }

    /**
     * Get user`s data, team`s data and manager`s data and go to team page.
     * @param save The collected data to save.
     */
    private void connectToTeam(Map<String, Object> save) {
        final Map<String,Object> userInfo = save;
        final Team team = (Team) userInfo.get ( ConstantNames.TEAM );
        dataExtraction.getUserDataByID(team.getHost(), new ISavable() {
            @Override
            public void onDataRead(Object save) {
                progressBar.setVisible(pb_singIn,false);
                userInfo.put(ConstantNames.TEAM_HOST,save);
                activityTransition.goTo(MainActivity.this, TeamPageActivity.class,true,userInfo,null);
            }
        });
    }

    /**
     * Build Map to save user`s information for register.
     * @return Return map that contains user object.
     */
    private Map<String, Object> getUser() {
        FirebaseAuth fba = FirebaseAuth.getInstance();
        User user = new User(null, fba.getCurrentUser().getEmail(),null,null,fba.getCurrentUser().getUid());
        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put(ConstantNames.USER,user);
        return userInfo;
    }

    /**
     * Send the user to register activity with user`s data and his step on the register process.
     * @param userInfo
     */
    private void connect(Map<String,Object> userInfo) {

        progressBar.setVisible(pb_singIn,false);

        //Save all user`s information and the step of create account.
        Map<String, Object> save = new HashMap<>();
        save.put("step",step);
        if(userInfo != null)
        {
            save.put(ConstantNames.USER,userInfo);
        }
        activityTransition.goTo(MainActivity.this, CreateAccountActivity.class,false,save,null);
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_newAccount(View view) {
        //Create transform animation
        ActivityOptions options = transformation.pushDown ( MainActivity.this );
        authorization.singOut();
        activityTransition.goTo ( MainActivity.this, CreateAccountActivity.class,false,null,options );
    }
}