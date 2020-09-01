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

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class MainActivity extends AppCompatActivity {

    EditText ed_name, ed_email, ed_password, ed_currentPassword;
    ProgressBar pb_singUp;

    FirebaseDatabase database;
    DatabaseReference mDatabase;
    FirebaseAuth fba;

    Intent connectIntent;

    private User user;

    private static final String USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        ed_name = findViewById ( R.id.ed_Name );
        ed_email = findViewById ( R.id.ed_Email );
        ed_password = findViewById ( R.id.ed_Password );
        ed_currentPassword = findViewById ( R.id.ed_CurrentPassword );

        pb_singUp = findViewById ( R.id.pb_singUp );

        database = FirebaseDatabase.getInstance ();
        mDatabase = database.getReference (USER);
        fba = FirebaseAuth.getInstance ();

        connectIntent = new Intent ( MainActivity.this , TeamListActivity.class );

        checkUserConnected ();

    }

    /**
     * create a new user in firebase
     * @param view
     * @param email user`s email.
     * @param password user`s password.
     */
    private void createUser(final View view, final String email, String password) {
        fba.createUserWithEmailAndPassword ( email,password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //if creating a user is successful than, log in to the user.
                if(task.isSuccessful ())
                {
                    Toast.makeText ( MainActivity.this,"User Created",Toast.LENGTH_SHORT ).show ();

                    //save user data in firebase
                    String keyID = mDatabase.push ().getKey ();
                    mDatabase.child ( keyID ).setValue ( user );

                    //save the user`s email in intent, to get to this user data.
                    connectIntent.putExtra ( "email",email );
                    startActivity( connectIntent );
                    finish ();
                }
                else
                {
                    Toast.makeText ( MainActivity.this,"Error ! " +task.getException ().getMessage (),Toast.LENGTH_SHORT ).show ();
                    pb_singUp.setVisibility ( view.GONE );
                }
            }
        } );
    }

    /**
     * check if the user is connected to the device
     */
    private void checkUserConnected() {
        FirebaseUser currentUser = fba.getCurrentUser();
        if(fba.getCurrentUser () != null)
        {
            connectIntent.putExtra ( "email",fba.getCurrentUser().getEmail () );
            startActivity( connectIntent );
            finish ();
        }
    }

    /**
     * this method use to check if user input is valid.
     * @param name user`s name.
     * @param email user`s email.
     * @param password user`s password.
     * @param currentPassword user`s current password.
     */
    private boolean isInputValid(String name, String email, String password, String currentPassword) {
        if(TextUtils.isEmpty ( name ))
        {
            ed_name.setError ( "Name is Required." );
            return false;
        }
        else if (name.length () >= 16)
        {
            ed_name.setError ( "Invalid name, minimum length: 16 letters" );
            return false;
        }
        else if(TextUtils.isEmpty ( email ))
        {
            ed_email.setError ( "Email is Required." );
            return false;
        }
        else if(TextUtils.isEmpty ( password ))
        {
            ed_password.setError ( "Password is Required." );
            return false;
        }
        else if(!currentPassword.equals ( password ))
        {
            ed_currentPassword.setError ( "Your current password is incorrect." );
            return false;
        }
        return true;
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_singUp(final View view) {
        //get information from edit text and put into string.
        String name = ed_name.getText ().toString ().trim ();
        String email = ed_email.getText ().toString ().trim ();
        String password = ed_password.getText ().toString ().trim ();
        String currentPassword = ed_currentPassword.getText ().toString ().trim ();

        //check if user input is valid.
        if(isInputValid ( name, email, password, currentPassword ))
        {
            //show the progress bar
            pb_singUp.setVisibility ( view.VISIBLE );

            //create user in firebase
            user = new User ( name,email );
            createUser ( view, email, password );
        }
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_alreadyAccount(View view) {
        Intent logInIntent = new Intent ( MainActivity.this , LoginActivity.class );
        startActivity ( logInIntent );
        finish ();
    }
}