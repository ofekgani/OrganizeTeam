package com.myapp.organizeteam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.myapp.organizeteam.Core.ConstantNames;
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

    EditText ed_email, ed_password;
    ProgressBar pb_singIn;

    Authorization authorization;
    InputManagement input;
    DataExtraction dataExtraction;
    Transformation transformation;
    ActivityTransition activityTransition;
    Loading progressBar;

    FirebaseAuth fba;

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

        //checkUserConnected ();
    }

    /**
     * check if the user is connected to the device, if he is than connect him to system.
     */
    private void checkUserConnected() {
        fba = FirebaseAuth.getInstance ();
        if(fba.getCurrentUser () != null)
        {
            authorization.connectUserToSystem ( this );
        }
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

        //login to system.
        authorization.login(email, password, new IRegister() {
            @Override
            public void onProcess() {
                progressBar.setVisible(pb_singIn,true);
            }

            @Override
            public void onDone(boolean successful, String message) {
                progressBar.setVisible(pb_singIn,false);
                if(successful)
                {
                    step = 0;
                    if(!authorization.isEmailVerified())
                    {
                        step = 1;
                        signIn(null);
                    }
                    else if(authorization.isUserConnected() && authorization.isEmailVerified())
                    {
                        dataExtraction.hasChild(ConstantNames.USER_PATH, authorization.getUserID(), new ISavable() {
                            @Override
                            public void onDataRead(Object exist) {
                                if((boolean)exist)
                                {
                                    dataExtraction.hasChild(ConstantNames.USER_PATH, authorization.getUserID(), ConstantNames.DATA_USER_NAME, new ISavable() {
                                        @Override
                                        public void onDataRead(final Object exist) {
                                            dataExtraction.getCurrentUserData(new ISavable() {
                                                @Override
                                                public void onDataRead(Object save) {
                                                    if((boolean)exist)
                                                    {
                                                        step = 4;
                                                    }
                                                    else
                                                    {
                                                        step = 2;
                                                    }
                                                    Map<String,Object> userInfo = (Map<String,Object>)save;
                                                    signIn(userInfo);
                                                }
                                            });
                                        }
                                    });
                                }
                                else
                                {
                                    step = 2;
                                    FirebaseAuth fba = FirebaseAuth.getInstance();
                                    User user = new User(null, fba.getCurrentUser().getEmail(),null,null,fba.getCurrentUser().getUid());
                                    Map<String,Object> userInfo = new HashMap<>();
                                    userInfo.put(ConstantNames.USER,user);
                                    signIn(userInfo);
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    private void signIn(Map<String,Object> userInfo) {
        Map<String, Object> save = new HashMap<>();
        save.put("step",step);
        if(userInfo != null)
        {
            save.put(ConstantNames.USER,userInfo);
        }
        activityTransition.goTo(MainActivity.this, CreateAccount.class,false,save,null);
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_newAccount(View view) {
        //Create transform animation
        ActivityOptions options = transformation.pushDown ( MainActivity.this );
        authorization.singOut();
        //go to main activity
        activityTransition.goTo ( MainActivity.this, CreateAccount.class,false,null,options );
    }
}