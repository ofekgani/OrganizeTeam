package com.myapp.organizeteam.DataManagement;

import android.app.Activity;
import android.app.ActivityOptions;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.MainActivity;
import com.myapp.organizeteam.Resources.Loading;
import com.myapp.organizeteam.Resources.Transformation;
import com.myapp.organizeteam.TeamPageActivity;
import com.myapp.organizeteam.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.myapp.organizeteam.Core.ActivityTransition;


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
     */
    public void getAllTeams(final ISavable iSavable) {
        dataExtraction.getTeams ( new ISavable () {
            @Override
            public void onDataRead( Object stat) {
                ArrayList<Team> teamsSave = (ArrayList<Team>)stat;
                iSavable.onDataRead(teamsSave);
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
     * @return return true if email is verified, false if not.
     */
    public boolean isEmailVerified() {
        FirebaseAuth fba = FirebaseAuth.getInstance ();
        if(fba.getCurrentUser() == null) return false;
        return fba.getCurrentUser ().isEmailVerified ();
    }

    public boolean isUserConnected() {
        FirebaseAuth fba = FirebaseAuth.getInstance ();
        if(fba.getCurrentUser() == null) return false;
        return true;
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
                                createNewUser(name, email, new IRegister() {
                                    @Override
                                    public void onProcess() {

                                    }

                                    @Override
                                    public void onDone(boolean successful, String message) {

                                    }
                                });

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

    public void createUser(String email, String password, final IRegister iRegister) {

        //get to firebase
        final FirebaseAuth fba = FirebaseAuth.getInstance ();

        iRegister.onProcess();

        fba.createUserWithEmailAndPassword ( email,password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //if creating a user is successful than, send email verification .
                if(task.isSuccessful ())
                {
                    //send email verification and check if this task is successful.
                    fba.getCurrentUser ().sendEmailVerification ().addOnCompleteListener ( new OnCompleteListener<Void> () {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                iRegister.onDone(task.isSuccessful (),null);
                            }
                            else
                            {
                                iRegister.onDone(task.isSuccessful (),task.getException().toString());
                            }
                        }
                    } );
                }
                else
                {
                    iRegister.onDone(task.isSuccessful (),task.getException().toString());
                }
            }
        } );
    }

    public void createNewUser(String name, String email, final IRegister iRegister) {
         FirebaseAuth fba = FirebaseAuth.getInstance ();
         String keyID = fba.getCurrentUser().getUid();
         User user = new User ( name,email,"",keyID);
         dataExtraction.setObject(ConstantNames.USER_PATH, keyID, user, new IRegister() {
             @Override
             public void onProcess() {
                 iRegister.onProcess();
             }

             @Override
             public void onDone(boolean successful, String message) {
                iRegister.onDone(successful,message);
             }
         });
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
                    connectUserToSystem ( context );

                }
                else
                {
                    input.setError (edEmail, ""+task.getException () );
                }
            }
        } );
    }

    public void login(final String email, String password, final IRegister iRegister) {

        //get to firebase
        final FirebaseAuth fba = FirebaseAuth.getInstance ();

        //show the progress bar
        iRegister.onProcess();

        fba.signInWithEmailAndPassword ( email , password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful ())
                {
                    iRegister.onDone(task.isSuccessful(),null);

                }
                else
                {
                    iRegister.onDone(task.isSuccessful(),task.getException().toString());
                }
            }
        } );
    }

    /**
     * This function use to handle all the user`s information and connect him to right activity.
     * @param context The activity from which you perform this function.
     */
    public void connectUserToSystem(final Activity context) {
        if(isEmailVerified ( ))
        {
            //get all user information
            dataExtraction.getCurrentUserData(new ISavable () {
                @Override
                public void onDataRead(Object stat) {

                    //put user`s information into map
                    final Map<String, Object> data = (Map<String, Object>)stat;

                    //get user`s id
                    final User user = (User)data.get ( ConstantNames.USER );
                    String id = user.getKeyID ();

                    //check if to user has team
                    dataExtraction.hasChild ( ConstantNames.USER_PATH, id,ConstantNames.DATA_USER_TEAMS, new ISavable () {
                        @Override
                        public void onDataRead(Object exist) {
                            if(!(boolean)exist)
                            {
                                connect ( context, WelcomeActivity.class,data );
                            }
                            else
                            {
                                //if to user has team, go to team page.
                                final Team team = (Team) data.get ( ConstantNames.TEAM );
                                dataExtraction.getUserDataByID(team.getHost(), new ISavable() {
                                    @Override
                                    public void onDataRead(Object save) {
                                        data.put(ConstantNames.TEAM_HOST,save);
                                        connect ( context, TeamPageActivity.class,data );
                                    }
                                });

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
        FirebaseAuth fba = FirebaseAuth.getInstance();
        String userID = fba.getCurrentUser().getUid();
        dataExtraction.deleteToken(userID);

        FirebaseAuth.getInstance ().signOut ();
        connect ( context, MainActivity.class,null );
    }

    public void singOut()
    {
        FirebaseAuth fba = FirebaseAuth.getInstance();
        if(fba.getCurrentUser() == null) return;
        String userID = fba.getCurrentUser().getUid();
        dataExtraction.deleteToken(userID);

        FirebaseAuth.getInstance ().signOut ();
    }
}
