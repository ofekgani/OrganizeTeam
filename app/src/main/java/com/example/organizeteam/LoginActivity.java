package com.example.organizeteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.organizeteam.AuthorizationSystem.Authorization;
import com.example.organizeteam.AuthorizationSystem.UserInput;
import com.example.organizeteam.Resources.Loading;
import  com.example.organizeteam.Resources.Transformation;
import  com.example.organizeteam.Core.ActivityTransition;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

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

    FirebaseAuth fba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );

        ed_email = findViewById ( R.id.ed_name );
        ed_password = findViewById ( R.id.ed_Password );
        pb_singIn = findViewById ( R.id.pb_singIn );

        authorization = new Authorization ();
        transformation = new Transformation ();
        activityTransition = new ActivityTransition ();
        progressBar = new Loading ( );

        fba = FirebaseAuth.getInstance ();

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
        authorization.login ( fba,LoginActivity.this,ed_email,ed_password,pb_singIn );
    }

    public void oc_newAccount(View view) {
        ActivityOptions options = transformation.pushDown ( LoginActivity.this );
        activityTransition.goTo ( LoginActivity.this,MainActivity.class,true,null,options );
    }
}