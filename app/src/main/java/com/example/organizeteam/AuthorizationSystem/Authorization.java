package com.example.organizeteam.AuthorizationSystem;

import android.app.ActivityOptions;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.organizeteam.LoginActivity;
import com.example.organizeteam.MainActivity;
import com.example.organizeteam.Resources.Loading;
import com.example.organizeteam.Resources.Transformation;
import com.example.organizeteam.TeamListActivity;
import com.example.organizeteam.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.example.organizeteam.Core.ActivityTransition;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class Authorization {

    UserInput input = new UserInput ();
    ActivityTransition activityTransition = new ActivityTransition ();
    Transformation transformation = new Transformation ();
    Loading progressBar = new Loading ();

    private static final String USER = "user";

    /**
     * create a new user in firebase
     * @param fba access to firebase auth to sing in to user.
     * @param context a activity that this function use.
     * @param edEmail user`s email.
     * @param edPassword user`s password.
     * @param edName user`s name.
     * @param pb Progress Bar resource.
     */
     public void createUser(final FirebaseAuth fba, final Context context, final EditText edEmail, final EditText edPassword, final EditText edName , final ProgressBar pb) {

        //show the progress bar
         progressBar.setVisible ( pb,true );

        String password = input.getInput ( edPassword );
        final String email = input.getInput ( edEmail );
        final String name = input.getInput ( edName );

        fba.createUserWithEmailAndPassword ( email,password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressBar.setVisible ( pb,false );

                //if creating a user is successful than, send email verification .
                if(task.isSuccessful ())
                {
                    //send email verification and check if this task is successful.
                    fba.getCurrentUser ().sendEmailVerification ().addOnCompleteListener ( new OnCompleteListener<Void> () {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful ())
                            {
                                //save user data in firebase
                                User user = new User ( name,email );
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance ().getReference (USER);
                                String keyID = mDatabase.push ().getKey ();
                                mDatabase.child ( keyID ).setValue ( user );

                                //save the user`s email in intent, to get to this user data.
                                Toast.makeText ( context,"Register successfully. Please check your email to verification. ",Toast.LENGTH_LONG ).show ();

                                //transform animation
                                ActivityOptions options = transformation.pushUp ( context );

                                //start Activity
                                activityTransition.goTo ( context, LoginActivity.class,true,null, options );
                            }
                            else
                            {
                                input.setError (edEmail, ""+task.getException () );
                            }
                        }
                    } );
                }
                else
                {
                    input.setError (edEmail, ""+task.getException () );
                }
            }
        } );
    }

    /**
     * login to system.
     * @param fba access to firebase auth to sing in to user.
     * @param context a activity that this function use.
     * @param edPassword user`s password.
     * @param edEmail user`s email.
     * @param pb Progress Bar resource.
     */
    public void login(final FirebaseAuth fba, final Context context, EditText edEmail, EditText edPassword, final ProgressBar pb) {

        //show the progress bar
        progressBar.setVisible ( pb,true );

        String email = input.getInput ( edEmail );
        String password = input.getInput ( edPassword );

        fba.signInWithEmailAndPassword ( email , password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressBar.setVisible ( pb,false );

                if(task.isSuccessful ())
                {
                    if(fba.getCurrentUser ().isEmailVerified ())
                    {
                        //save the user`s email in intent, to get to this user data.
                        String userEmail =  fba.getCurrentUser().getEmail();
                        Map<String,Object> save = new HashMap<> ( );
                        save.put ( "email",(String)userEmail);

                        activityTransition.goTo ( context, TeamListActivity.class, true, save, null  );
                    }
                    else
                    {
                        Toast.makeText ( context,"Email is not verified. Please verify your email address. ",Toast.LENGTH_SHORT ).show ();
                    }

                }
                else
                {
                    Toast.makeText ( context,"Error ! " +task.getException ().getMessage (),Toast.LENGTH_SHORT ).show ();
                }
            }
        } );
    }
}
