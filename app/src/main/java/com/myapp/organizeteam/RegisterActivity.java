package com.myapp.organizeteam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.Core.InputManagement;

import com.myapp.organizeteam.Resources.Loading;

import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.Resources.Transformation;
import com.myapp.organizeteam.Core.ActivityTransition;

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
    InputManagement input;
    Transformation transformation;
    Loading progressBar;
    ActivityTransition activityTransition;
    DataExtraction dataExtraction;

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
        input = new InputManagement ();
        progressBar = new Loading ( );
        dataExtraction = new DataExtraction ();

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