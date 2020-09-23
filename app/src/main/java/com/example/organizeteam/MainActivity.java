package com.example.organizeteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import  com.example.organizeteam.AuthorizationSystem.Authorization;

import static android.view.View.VISIBLE;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class MainActivity extends AppCompatActivity {

    //xml Widgets
    EditText ed_name, ed_email, ed_password, ed_currentPassword;
    ProgressBar pb_singUp;

    FirebaseDatabase database;
    DatabaseReference mDatabase;
    FirebaseAuth fba;

    Intent connectIntent;

    private User user;
    Authorization authorization;

    private static final String USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        //References
        ed_name = findViewById ( R.id.ed_Name );
        ed_email = findViewById ( R.id.ed_Email );
        ed_password = findViewById ( R.id.ed_Password );
        ed_currentPassword = findViewById ( R.id.ed_CurrentPassword );
        pb_singUp = findViewById ( R.id.pb_singUp );

        authorization = new Authorization ();
        connectIntent = new Intent ( MainActivity.this , TeamListActivity.class );

        database = FirebaseDatabase.getInstance ();
        mDatabase = database.getReference (USER);
        fba = FirebaseAuth.getInstance ();

        checkUserConnected ();
    }

    /**
     * create a new user in firebase
     * @param email user`s email.
     * @param password user`s password.
     */
    private void createUser(final String name ,final String email, String password) {
        fba.createUserWithEmailAndPassword ( email,password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                pb_singUp.setVisibility ( View.GONE );

                //if creating a user is successful than, send email verification .
                if(task.isSuccessful ())
                {
                    //send email verification and check if this task is successful.
                    fba.getCurrentUser ().sendEmailVerification ().addOnCompleteListener ( new OnCompleteListener<Void> () {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful ())
                            {

                                ed_email.setText ( "" );
                                ed_name.setText ( "" );
                                ed_password.setText ( "" );
                                ed_currentPassword.setText ( "" );

                                //save user data in firebase
                                user = new User ( name,email );

                                String keyID = mDatabase.push ().getKey ();
                                mDatabase.child ( keyID ).setValue ( user );

                                //save the user`s email in intent, to get to this user data.
                                Toast.makeText ( MainActivity.this,"Register successfully. Please check your email to verification. ",Toast.LENGTH_LONG ).show ();
                                logIn ();
                            }
                            else
                            {
                                Toast.makeText ( MainActivity.this,""+task.getException ().getMessage (),Toast.LENGTH_LONG ).show ();
                            }
                        }
                    } );
                }
                else
                {
                    Toast.makeText ( MainActivity.this,"Error ! " +task.getException ().getMessage (),Toast.LENGTH_LONG ).show ();
                }
            }
        } );
    }

    private void logIn() {
        Intent logInIntent = new Intent ( MainActivity.this, LoginActivity.class );
        startActivity ( logInIntent );
        finish ();
    }

    private void logInToSystem(String email) {
        connectIntent.putExtra ( "email", email );
        startActivity ( connectIntent );
        finish ();
    }

    /**
     * check if the user is connected to the device
     */
    private void checkUserConnected() {
        FirebaseUser currentUser = fba.getCurrentUser();
        if(fba.getCurrentUser () != null)
        {
            logInToSystem (currentUser.getEmail () );
        }
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_singUp(final View view) {
        //check if user input is valid.
        if(!authorization.isInputValid ( ed_name, ed_email, ed_password, ed_currentPassword ))
            return;

        //get information from edit text and put into string.
        String name = authorization.getInput ( ed_name );
        String email = authorization.getInput ( ed_email );
        String password = authorization.getInput ( ed_password );

        //show the progress bar
        pb_singUp.setVisibility ( VISIBLE );

        //create user in firebase
        createUser (name ,email, password );

    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_alreadyAccount(View view) {
        logIn ();
    }
}