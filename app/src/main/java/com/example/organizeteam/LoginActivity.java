package com.example.organizeteam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.organizeteam.AuthorizationSystem.Authorization;
import com.example.organizeteam.AuthorizationSystem.UserInput;
import com.example.organizeteam.Resources.Loading;
import  com.example.organizeteam.Resources.Transformation;
import  com.example.organizeteam.Core.ActivityTransition;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class LoginActivity extends AppCompatActivity {

    EditText ed_email, ed_password;
    ProgressBar pb_singIn;

    Authorization authorization;
    UserInput input = new UserInput ();
    Transformation transformation;
    ActivityTransition activityTransition;
    Loading progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );

        //References
        ed_email = findViewById ( R.id.ed_email );
        ed_password = findViewById ( R.id.ed_Password );
        pb_singIn = findViewById ( R.id.pb_singIn );

        //allocating memory
        authorization = new Authorization ();
        transformation = new Transformation ();
        activityTransition = new ActivityTransition ();
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

        //login to system.
        authorization.login (LoginActivity.this,ed_email,ed_password,pb_singIn );
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_newAccount(View view) {
        //Create transform animation
        ActivityOptions options = transformation.pushDown ( LoginActivity.this );

        //go to main activity
        activityTransition.goTo ( LoginActivity.this,MainActivity.class,false,null,options );
    }
}