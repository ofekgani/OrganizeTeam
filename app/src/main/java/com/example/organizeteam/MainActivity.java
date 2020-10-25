package com.example.organizeteam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.organizeteam.DataManagement.Authorization;
import com.example.organizeteam.DataManagement.DataExtraction;
import com.example.organizeteam.Core.InputManagement;
import com.example.organizeteam.Resources.Loading;
import  com.example.organizeteam.Resources.Transformation;
import  com.example.organizeteam.Core.ActivityTransition;
import com.google.firebase.auth.FirebaseAuth;

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

        checkUserConnected ();
    }

    /**
     * check if the user is connected to the device, if he is than connect him to system.
     */
    private void checkUserConnected() {
        fba = FirebaseAuth.getInstance ();
        if(fba.getCurrentUser () != null)
        {
            authorization.connectUserToSystem ( fba,this );
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

        //login to system.
        authorization.login ( MainActivity.this,ed_email,ed_password,pb_singIn );
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_newAccount(View view) {
        //Create transform animation
        ActivityOptions options = transformation.pushDown ( MainActivity.this );

        //go to main activity
        activityTransition.goTo ( MainActivity.this, RegisterActivity.class,false,null,options );
    }
}