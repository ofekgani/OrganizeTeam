package com.example.organizeteam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.organizeteam.AuthorizationSystem.IUser;
import com.example.organizeteam.AuthorizationSystem.UserInfo;
import com.example.organizeteam.AuthorizationSystem.UserInput;

import com.example.organizeteam.Resources.Loading;
import com.google.firebase.auth.FirebaseAuth;

import  com.example.organizeteam.AuthorizationSystem.Authorization;
import  com.example.organizeteam.Resources.Transformation;
import  com.example.organizeteam.Core.ActivityTransition;

import java.util.Map;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class RegisterActivity extends AppCompatActivity {

    //xml Widgets
    EditText ed_name, ed_email, ed_password, ed_currentPassword;
    ProgressBar pb_singUp;

    Authorization authorization;
    UserInput input;
    Transformation transformation;
    Loading progressBar;
    ActivityTransition activityTransition;
    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_register );

        //References
        ed_name = findViewById ( R.id.ed_name );
        ed_email = findViewById ( R.id.ed_email );
        ed_password = findViewById ( R.id.ed_password );
        ed_currentPassword = findViewById ( R.id.ed_currentPassword );
        pb_singUp = findViewById ( R.id.pb_singUp );

        //allocating memory
        authorization = new Authorization ();
        transformation = new Transformation ();
        activityTransition = new ActivityTransition ();
        input = new UserInput ();
        progressBar = new Loading ( );
        userInfo = new UserInfo ();

        progressBar.setVisible ( pb_singUp,false );
    }

    @Override
    protected void onStart() {
        super.onStart ();
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
        authorization.createUser ( RegisterActivity.this,ed_email,ed_password,ed_name,pb_singUp );

    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_alreadyAccount(View view) {
        //transform animation
        ActivityOptions options = transformation.pushUp ( RegisterActivity.this );

        //start Activity
        activityTransition.goTo ( RegisterActivity.this, MainActivity.class,true,null, options );
    }
}