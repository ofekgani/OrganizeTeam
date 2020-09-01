package com.example.organizeteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    FirebaseAuth fba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );

        ed_email = findViewById ( R.id.ed_Email );
        ed_password = findViewById ( R.id.ed_Password );

        pb_singIn = findViewById ( R.id.pb_singIn );

        fba = FirebaseAuth.getInstance ();
    }

    /**
     * login to system.
     * @param view
     * @param email user`s email.
     * @param password user`s password.
     */
    private void login(final View view, String email, String password) {
        fba.signInWithEmailAndPassword ( email,password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful ())
                {
                    Toast.makeText ( LoginActivity.this,"Logged in successfully",Toast.LENGTH_SHORT ).show ();

                    //save the user`s email in intent, to get to this user data.
                    FirebaseUser currentUser = fba.getCurrentUser();
                    String userEmail = currentUser.getEmail();

                    Intent loginIntent = new Intent ( LoginActivity.this , TeamListActivity.class );
                    loginIntent.putExtra ("email", userEmail);
                    startActivity( loginIntent );
                    finish ();
                }
                else
                {
                    Toast.makeText ( LoginActivity.this,"Error ! " +task.getException ().getMessage (),Toast.LENGTH_SHORT ).show ();
                    pb_singIn.setVisibility ( view.GONE );
                }
            }
        } );
    }

    /**
     * this method use to check if user input is valid.
     * @param email user`s email.
     * @param password user`s password.
     */
    private boolean isInputValid(String email, String password) {
        if(TextUtils.isEmpty ( email ))
        {
            ed_email.setError ( "Email is Required." );
            return false;
        }
        if(TextUtils.isEmpty ( password ))
        {
            ed_password.setError ( "Password is Required." );
            return false;
        }
        return true;
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_singIn(final View view) {
        //get information from edit text and put into string.
        String email = ed_email.getText ().toString ().trim ();
        String password = ed_password.getText ().toString ().trim ();

        //check if user input is valid.
        if(isInputValid ( email, password ))
        {
            //show the progress bar
            pb_singIn.setVisibility ( view.VISIBLE );

            //login to system.
            login ( view, email, password );
        }
    }

    public void oc_newAccount(View view) {
        Intent registerIntent = new Intent ( LoginActivity.this , MainActivity.class );
        startActivity ( registerIntent );
        finish ();
    }
}