package com.example.organizeteam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.organizeteam.AuthorizationSystem.UserInput;

import com.example.organizeteam.Resources.Loading;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import  com.example.organizeteam.AuthorizationSystem.Authorization;
import  com.example.organizeteam.Resources.Transformation;
import  com.example.organizeteam.Core.ActivityTransition;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class MainActivity extends AppCompatActivity {

    //xml Widgets
    EditText ed_name, ed_email, ed_password, ed_currentPassword;
    ProgressBar pb_singUp;

    Authorization authorization;
    UserInput input;
    Transformation transformation;
    Loading progressBar;
    ActivityTransition activityTransition;

    FirebaseAuth fba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        //References
        ed_name = findViewById ( R.id.ed_name );
        ed_email = findViewById ( R.id.ed_email );
        ed_password = findViewById ( R.id.ed_password );
        ed_currentPassword = findViewById ( R.id.ed_currentPassword );
        pb_singUp = findViewById ( R.id.pb_singUp );

        authorization = new Authorization ();
        transformation = new Transformation ();
        activityTransition = new ActivityTransition ();
        input = new UserInput ();
        progressBar = new Loading ( );

        fba = FirebaseAuth.getInstance ();

        progressBar.setVisible ( pb_singUp,false );

        checkUserConnected ();
    }

    /**
     * check if the user is connected to the device, if he is than connect him to system.
     */
    private void checkUserConnected() {
        FirebaseUser currentUser = fba.getCurrentUser();
        if(fba.getCurrentUser () != null)
        {
            Map<String,Object> save = new HashMap<> (  );
            save.put ( "email",(String)currentUser.getEmail ()  );
            activityTransition.goTo ( MainActivity.this,TeamListActivity.class,true,save, null );
        }
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_singUp(final View view) {
        //check if user input is valid.
        if(!input.isInputValid ( ed_name, ed_email, ed_password, ed_currentPassword ))
            return;

        //create user in firebase
        authorization.createUser ( fba,MainActivity.this,ed_email,ed_password,ed_name,pb_singUp );

    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_alreadyAccount(View view) {
        //transform animation
        ActivityOptions options = transformation.pushUp ( MainActivity.this );

        //start Activity
        activityTransition.goTo ( MainActivity.this,LoginActivity.class,true,null, options );
    }
}