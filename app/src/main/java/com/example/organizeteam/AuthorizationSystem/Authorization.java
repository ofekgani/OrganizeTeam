package com.example.organizeteam.AuthorizationSystem;

import android.app.Activity;
import android.app.ActivityOptions;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.organizeteam.Core.ConstantNames;
import com.example.organizeteam.MainActivity;
import com.example.organizeteam.Resources.Loading;
import com.example.organizeteam.Resources.Transformation;
import com.example.organizeteam.TeamListActivity;
import com.example.organizeteam.Resources.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.example.organizeteam.Core.ActivityTransition;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Map;

import androidx.annotation.NonNull;

public class Authorization {

    UserInput input = new UserInput ();
    UserInfo userInfo = new UserInfo ();
    ActivityTransition activityTransition = new ActivityTransition ();
    Transformation transformation = new Transformation ();
    Loading progressBar = new Loading ();

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
                                userInfo.setObject ( ConstantNames.USER_PATH,keyID,user );

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
                    if(fba.getCurrentUser ().isEmailVerified ())
                    {
                        userInfo.getUserInformation ( new IUser () {
                            @Override
                            public void onDataRead(Map<String, Object> data) {
                                activityTransition.goTo ( context,TeamListActivity.class,true,data, null );
                            }
                        } );
                    }
                    else
                    {
                        Toast.makeText ( context,"Email is not verified. Please verify your email address. ",Toast.LENGTH_SHORT ).show ();
                    }

                }
                else
                {
                    input.setError (edEmail, ""+task.getException () );
                }
            }
        } );
    }

    /**
     * Sign out from the system.
     * @param context The activity from which the user sign out.
     */
    public void singOut(Activity context)
    {
        FirebaseAuth.getInstance ().signOut ();
        activityTransition.goTo ( context , MainActivity.class,true,null,null);
    }
}
