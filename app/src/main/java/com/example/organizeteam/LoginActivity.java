package com.example.organizeteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.organizeteam.AuthorizationSystem.Authorization;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class LoginActivity extends AppCompatActivity {

    EditText ed_email, ed_password;
    ProgressBar pb_singIn;

    Authorization authorization;

    FirebaseAuth fba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );

        ed_email = findViewById ( R.id.ed_name );
        ed_password = findViewById ( R.id.ed_Password );

        pb_singIn = findViewById ( R.id.pb_singIn );

        authorization = new Authorization ();

        fba = FirebaseAuth.getInstance ();

        pb_singIn.setVisibility ( View.GONE );
    }

    /**
     * login to system.
     * @param email user`s email.
     * @param password user`s password.
     */
    private void login(String email, String password) {
        fba.signInWithEmailAndPassword ( email,password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                pb_singIn.setVisibility ( View.GONE );

                if(task.isSuccessful ())
                {
                    if(fba.getCurrentUser ().isEmailVerified ())
                    {

                        ed_email.setText ( "" );
                        ed_password.setText ( "" );

                        //save the user`s email in intent, to get to this user data.
                        FirebaseUser currentUser = fba.getCurrentUser();
                        String userEmail = currentUser.getEmail();

                        Intent loginToSystem = new Intent ( LoginActivity.this , TeamListActivity.class );
                        loginToSystem.putExtra ("email", userEmail);
                        startActivity( loginToSystem );
                        finish ();
                    }
                    else
                    {
                        Toast.makeText ( LoginActivity.this,"Email is not verified. Please verify your email address. ",Toast.LENGTH_SHORT ).show ();
                    }

                }
                else
                {
                    Toast.makeText ( LoginActivity.this,"Error ! " +task.getException ().getMessage (),Toast.LENGTH_SHORT ).show ();
                }
            }
        } );
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_singIn(final View view) {
        //check if user input is valid.
        if(!authorization.isInputValid ( ed_email, ed_password ))
            return;

        //get information from edit text and put into string.
        String email = authorization.getInput ( ed_email );
        String password = authorization.getInput ( ed_password );

        //show the progress bar
        pb_singIn.setVisibility ( View.VISIBLE );

        //login to system.
        login (email, password );
    }

    public void oc_newAccount(View view) {
        Intent registerIntent = new Intent ( LoginActivity.this , MainActivity.class );
        startActivity ( registerIntent );
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        overridePendingTransition ( R.anim.push_down_out,R.anim.push_down_in );
        finish ();
    }
}