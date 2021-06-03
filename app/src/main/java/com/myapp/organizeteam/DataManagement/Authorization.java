package com.myapp.organizeteam.DataManagement;

import android.app.Activity;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.Activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.myapp.organizeteam.Core.ActivityTransition;


import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

public class Authorization {

    DataExtraction dataExtraction = new DataExtraction ();
    ActivityTransition activityTransition = new ActivityTransition ();

    public static boolean isManager;

    /**
     * Send the user to new activity.
     * @param context The activity from which the user moves to a new activity.
     * @param to The activity that user move on.
     * @param data data to save into intent.
     */
    private void sendTo(Activity context, Class to, Map<String, Object> data) {
        activityTransition.goTo ( context, to, true, data, null );
    }

    /**
     * This function check if a user verified its email.
     * @return return true if email is verified, false if not.
     */

    /**
     * Get the user keyID by the current user on the system.
     * @return Return UID by the current user on the system.
     */
    public String getUserID()
    {
        FirebaseAuth fba = FirebaseAuth.getInstance();
        return fba.getCurrentUser().getUid();
    }

    /**
     * Checking if the current user verified his email.
     * @return Return true if the current user verified his email and false if not.
     */
    public boolean isEmailVerified() {
        FirebaseAuth fba = FirebaseAuth.getInstance ();
        if(fba.getCurrentUser() == null) return false;
        return fba.getCurrentUser ().isEmailVerified ();
    }

    /**
     * This function create new user on firebase authentication by email and password.
     * This function call to interface that manage the actions when user creating and actions when user created.
     * @param email The email with which will create the user.
     * @param password The password with which will create the user.
     * @param iRegister Interface that manage the actions when user creating and actions when user created.
     */
    public void createUser(String email, String password, final IRegister iRegister) {

        //get reference to firebase authentication
        final FirebaseAuth fba = FirebaseAuth.getInstance ();

        //Calls to action when user creation is process
        iRegister.onProcess();

        fba.createUserWithEmailAndPassword ( email,password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //if creating a user is successful than, send email verification .
                if(task.isSuccessful ())
                {
                    //Send email verification
                    fba.getCurrentUser ().sendEmailVerification ().addOnCompleteListener ( new OnCompleteListener<Void> () {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                //Calls to action when user creation is complete
                                iRegister.onDone(task.isSuccessful (),null);
                            }
                            else
                            {
                                //Calls to action when user creation is failed and save the fail message.
                                iRegister.onDone(task.isSuccessful (),task.getException().getLocalizedMessage());
                            }
                        }
                    } );
                }
                else
                {
                    //Calls to action when user creation is failed and save the fail message.
                    iRegister.onDone(task.isSuccessful (),task.getException().getLocalizedMessage());
                }
            }
        } );
    }

    /**
     * Connect to system with email and password.
     * @param email The email of user.
     * @param password The password of user.
     * @param iRegister Interface that manage the actions when user connect to system and actions when user connected.
     */
    public void login(final String email, String password, final IRegister iRegister) {

        //Get reference to firebase authentication
        final FirebaseAuth fba = FirebaseAuth.getInstance ();

        //Calls to action when user creation is process
        iRegister.onProcess();

        //Connect the user to the system
        fba.signInWithEmailAndPassword ( email , password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful ())
                {
                    //Calls to action when user creation is complete.
                    iRegister.onDone(task.isSuccessful(),null);

                }
                else
                {
                    //Calls to action when user creation is failed and save the fail message.
                    iRegister.onDone(task.isSuccessful (),task.getException().getLocalizedMessage());
                }
            }
        } );
    }

    /**
     * This function create new reference of user on firebase realtime.
     * @param name The name of user.
     * @param email The email of user.
     * @param iRegister Interface that manage the actions when user creating and actions when user created.
     */
    public void createNewUser(String name, String email, final IRegister iRegister) {

        //Get reference to firebase authentication
        FirebaseAuth fba = FirebaseAuth.getInstance ();

        //Get current user`s Uid from firebase authentication
        String keyID = fba.getCurrentUser().getUid();

        //Set all information on new object to set into firebase realtime
        User user = new User ( name,email,null,null,keyID);

        //Set the object into firebase realtime
        dataExtraction.setObject(ConstantNames.USER_PATH, keyID, user, new IRegister() {
            @Override
            public void onProcess() {
                iRegister.onProcess();
            }

            @Override
            public void onDone(boolean successful, String message) {
                if(successful)
                {
                    //Calls to action when user creation is complete.
                    iRegister.onDone(successful,null);

                }
                else
                {
                    //Calls to action when user creation is failed and save the fail message.
                    iRegister.onDone(successful,message);
                }
            }
        });
    }

    /**
     * This function receive phone and send to this phone sms verification.
     * @param activity the activity will which send sms verification.
     * @param phone The phone to which it will send verification.
     * @param mCallbacks An interface through which we can manage all operations in different occurrences.
     */
    public void sendVerifyPhoneNumber(final Activity activity, String phone, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks)
    {
        FirebaseAuth fba = FirebaseAuth.getInstance();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fba)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    /**
     * This function receive a phone and force to send SMS verification to the phone.
     * @param activity the activity will which send sms verification.
     * @param phone The phone to which it will send verification.
     * @param mCallbacks An interface through which we can manage all operations in different occurrences.
     * @param token phone`s token to force send verification.
     */
    public void sendVerifyPhoneNumber(final Activity activity, String phone, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks, PhoneAuthProvider.ForceResendingToken token)
    {
        FirebaseAuth fba = FirebaseAuth.getInstance();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fba)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    /**
     * Get a receiver code and a verification code for a phone and connect to the system in case they are both equal.
     * @param codeVerification Verification code to check authentication with phone.
     * @param code Code to check if it is the same as verification code
     * @param iRegister An interface that manages the operations during the verification process and upon completion.
     */
    public void signInWithPhoneNumber(String codeVerification, String code, final IRegister iRegister)
    {
        if(codeVerification == null) return;

        iRegister.onProcess();

        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(codeVerification,code);
        FirebaseAuth fba = FirebaseAuth.getInstance();
        fba.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            iRegister.onDone(true,null);
                        }
                        else
                        {
                            iRegister.onDone(false,task.getException().getLocalizedMessage());
                        }
                    }
                });
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
        sendTo( context, MainActivity.class,null );
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
