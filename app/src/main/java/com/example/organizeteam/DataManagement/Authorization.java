package com.example.organizeteam.DataManagement;

import android.app.Activity;
import android.app.ActivityOptions;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.organizeteam.Core.ConstantNames;
import com.example.organizeteam.Core.InputManagement;
import com.example.organizeteam.Core.Team;
import com.example.organizeteam.Core.User;
import com.example.organizeteam.MainActivity;
import com.example.organizeteam.Resources.Loading;
import com.example.organizeteam.Resources.Transformation;
import com.example.organizeteam.TeamListActivity;
import com.example.organizeteam.TeamPageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.example.organizeteam.Core.ActivityTransition;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;

public class Authorization {

    InputManagement input = new InputManagement ();
    DataExtraction dataExtraction = new DataExtraction ();
    ActivityTransition activityTransition = new ActivityTransition ();
    Transformation transformation = new Transformation ();
    Loading progressBar = new Loading ();

    /**
     * Extracts all teams from the cloud and saves them to next activity.
     * @param data data to save into intent.
     * @param context The activity from which the user moves to a new activity.
     */
    private void getAllTeams(final Map<String, Object> data, final Activity context) {
        dataExtraction.getTeams ( new ISavable () {
            @Override
            public void onDataRead( Object stat) {
                ArrayList<Team> teamsSave = (ArrayList<Team>)stat;
                data.put ( ConstantNames.TEAMS, teamsSave);

                connect ( context,TeamListActivity.class, data );
            }
        } );
    }

    /**
     * Send the user to new activity.
     * @param context The activity from which the user moves to a new activity.
     * @param to The activity that user move on.
     * @param data data to save into intent.
     */
    private void connect(Activity context, Class to,Map<String, Object> data) {
        activityTransition.goTo ( context, to, true, data, null );
    }

    /**
     * This function check if a user verified its email.
     * @param fba The firebase path to get the user which you want to check.
     * @return return true if email is verified, false if not.
     */
    private boolean isEmailVerified(FirebaseAuth fba) {
        return fba.getCurrentUser ().isEmailVerified ();
    }

    /**
     * create a new user in firebase
     * @param context a activity that this function use.
     * @param edEmail user`s email.
     * @param edPassword user`s password.
     * @param edName user`s name.
     * @param pb Progress Bar resource.
     */
     public void createUser(final Activity context, final EditText edEmail, final EditText edPassword, final EditText edName , final ProgressBar pb) {

         //get to firebase
         final FirebaseAuth fba = FirebaseAuth.getInstance ();

         //get user input
         String password = input.getInput ( edPassword );
         final String email = input.getInput ( edEmail );
         final String name = input.getInput ( edName );

        //show the progress bar
         progressBar.setVisible ( pb,true );

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
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance ().getReference ( ConstantNames.USER_PATH);
                                String keyID = mDatabase.push ().getKey ();
                                User user = new User ( name,email,"none",keyID);
                                dataExtraction.setObject ( ConstantNames.USER_PATH,keyID,user );

                                //save the user`s email in intent, to get to this user data.
                                Toast.makeText ( context,"Register successfully. Please check your email to verification. ",Toast.LENGTH_LONG ).show ();

                                //transform animation
                                ActivityOptions options = transformation.pushUp ( context );

                                //start Activity
                                activityTransition.goTo ( context, MainActivity.class,true,null, options );
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
     * @param context a activity that this function use.
     * @param edPassword user`s password.
     * @param edEmail user`s email.
     * @param pb Progress Bar resource.
     */
    public void login(final Activity context, final EditText edEmail, EditText edPassword, final ProgressBar pb) {

        //get to firebase
        final FirebaseAuth fba = FirebaseAuth.getInstance ();

        //get user input
        String email = input.getInput ( edEmail );
        String password = input.getInput ( edPassword );

        //show the progress bar
        progressBar.setVisible ( pb,true );

        fba.signInWithEmailAndPassword ( email , password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressBar.setVisible ( pb,false );

                if(task.isSuccessful ())
                {
                    connectUserToSystem ( fba, context );

                }
                else
                {
                    input.setError (edEmail, ""+task.getException () );
                }
            }
        } );
    }

    /**
     * This function use to handle all the user`s information and connect him to right activity.
     * @param fba the firebase path to get the user which you want to enter into the system and manage its information
     * @param context The activity from which you perform this function.
     */
    public void connectUserToSystem(FirebaseAuth fba, final Activity context) {
        if(isEmailVerified ( fba ))
        {
            //get all user information
            dataExtraction.getUserInformation ( new ISavable () {
                @Override
                public void onDataRead(Object stat) {

                    //put user`s information into map
                    final Map<String, Object> data = (Map<String, Object>)stat;

                    //get user`s id
                    User user = (User)data.get ( ConstantNames.USER );
                    String id = user.getKeyID ();

                    //check if to user has team
                    dataExtraction.hasChild ( ConstantNames.USER_PATH, id,ConstantNames.DATA_USER_TEAMS, new ISavable () {
                        @Override
                        public void onDataRead(Object exist) {
                            if(!(boolean)exist)
                            {
                                //if to user has not team, get all teams and go to team list activity
                                getAllTeams ( data, context );
                            }
                            else
                            {
                                //if to user has team, go to team page.
                                connect ( context, TeamPageActivity.class,data );
                            }
                        }
                    } );
                }
            } );
        }
        else
        {
            Toast.makeText ( context,"Email is not verified. Please verify your email address. ",Toast.LENGTH_SHORT ).show ();
        }
    }

    /**
     * Sign out from the system.
     * @param context The activity from which the user sign out.
     */
    public void singOut(Activity context)
    {
        FirebaseAuth.getInstance ().signOut ();
        connect ( context, MainActivity.class,null );
    }
}
