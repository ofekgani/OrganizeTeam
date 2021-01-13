package com.myapp.organizeteam;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.IRegister;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Dialogs.RequestJoinDialog;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.Resources.Loading;
import com.myapp.organizeteam.Resources.Stepper;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class StepThreeRegisterFragment extends Fragment implements Step {

    Authorization authorization;
    InputManagement input;
    Loading progressBar;
    Stepper stepper;
    Image image;

    StepperLayout mStepperLayout;

    ProgressBar pb;
    Button btn_next;
    EditText ed_name;
    ImageView mv_userLogo;
    DataExtraction dataExtraction;

    //Helper variables to save and update user`s information
    Map<String,Object> userData;
    User user;
    Uri imageUriResult;

    //Helper variables to manage upload process to firebase
    boolean isImageUploaded, isNameUploaded;

    private static final int PERMISSION_CODE = 1001;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_register_step3, container, false);

        //allocating memory
        authorization = new Authorization ();
        input = new InputManagement ();
        stepper = new Stepper();
        progressBar = new Loading ( );
        image = new Image();
        dataExtraction = new DataExtraction();

        pb = v.findViewById(R.id.pb_next);
        btn_next = v.findViewById(R.id.btn_next);
        mv_userLogo = v.findViewById(R.id.mv_userLogo);
        ed_name = v.findViewById(R.id.ed_name);

        mStepperLayout = stepper.getStepperLayout(container,R.id.stepperLayout);

        progressBar.setVisible ( pb,false );

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check if input is valid
                if(input.isInputEmpty(ed_name))
                {
                    input.setError(ed_name,"This field is required.");
                    return;
                }

                progressBar.setVisible ( pb,true );

                final FirebaseAuth fba = FirebaseAuth.getInstance ();
                if(fba.getCurrentUser() == null) return;

                final String email = fba.getCurrentUser().getEmail();
                final String name = input.getInput(ed_name);

                //upload image to firebase if the user choice image.
                if(imageUriResult != null)
                {
                    uploadPicture (imageUriResult);
                }

                //Check if user exist on firebase realtime
                dataExtraction.hasChild(ConstantNames.USER_PATH, user.getKeyID(), new ISavable() {
                    @Override
                    public void onDataRead(Object exist) {
                        if(!(boolean)exist)
                        {
                            //Create new user with name in firebase realtime
                            authorization.createNewUser(name, email, new IRegister() {
                                @Override
                                public void onProcess() {
                                    progressBar.setVisible ( pb,true );
                                }

                                @Override
                                public void onDone(boolean successful, String message) {
                                    progressBar.setVisible ( pb,false );

                                    if(successful)
                                    {
                                        //go to next step
                                        next();
                                    }
                                    else
                                    {
                                        Snackbar.make(v,""+message, BaseTransientBottomBar.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            //if user exist, update just user`s name and logo
                            dataExtraction.setNewData(ConstantNames.USER_PATH,user.getKeyID(),ConstantNames.DATA_USER_NAME,name);

                            //go to next step
                            next();
                        }
                    }
                });
            }
        });

        //go to gallery and change user`s logo
        mv_userLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions ( permissions,PERMISSION_CODE );
                    }
                    else
                    {
                        image.pickImageFromGallery(StepThreeRegisterFragment.this);
                    }
                }
                else
                {
                    image.pickImageFromGallery(StepThreeRegisterFragment.this);
                }
            }
        });

        return v;
    }

    /**
     * Save user`s information and go to next step.
     */
    private void next() {
        //Update action status
        isNameUploaded = true;

        //save user`s data
        if(imageUriResult == null || isImageUploaded == true) {
            if (imageUriResult != null) {
                userData.put(ConstantNames.USER, getUserData(imageUriResult.toString()));
            } else if (user.getLogo() != null) {
                userData.put(ConstantNames.USER, getUserData(user.getLogo()));
            }
            else
            {
                userData.put(ConstantNames.USER, getUserData(null));
            }

            //Save user`s data into global variable
            DataPass.passData = userData;

            //Go to next step
            dataExtraction.hasChild(ConstantNames.USER_PATH, user.getKeyID(), ConstantNames.DATA_USER_PHONE, new ISavable() {
                @Override
                public void onDataRead(Object exist) {
                    if((boolean)exist)
                    {
                        stepper.go(mStepperLayout,4);
                    }
                    else
                    {
                        stepper.goNext(mStepperLayout);
                    }
                }
            });

        }
    }

    /**
     * Get new user object with current user`s information.
     * @param imageUri image url to save user`s logo into object.
     * @return Return new user object with update information.
     */
    private User getUserData(String imageUri) {
        String email = null;
        String keyID = null;
        String phone = null;
        if(user != null)
        {
            if(user.getEmail() != null)
                email = user.getEmail();
            if(user.getKeyID() != null)
                keyID = user.getKeyID();
            if(user.getPhone() != null);
                phone = user.getPhone();
        }

        return new User(input.getInput(ed_name), email, phone, imageUri, keyID);
    }

    /**
     * Upload image to firebase storage by image uri and save user`s information
     * @param image
     */
    private void uploadPicture(Uri image)
    {
        if(user == null) return;
        if(user.getEmail() != null && user.getKeyID() != null)
        {
            dataExtraction.uploadPicture ( image, getContext(), ConstantNames.USER_PATH, user.getKeyID(),user.getEmail(), new ISavable() {
                @Override
                public void onDataRead(Object uri) {
                    isImageUploaded = true;
                    if(isNameUploaded)
                    {
                        userData.put(ConstantNames.USER,getUserData(imageUriResult.toString()));
                        DataPass.passData = userData;

                        dataExtraction.hasChild(ConstantNames.USER_PATH, user.getKeyID(), ConstantNames.DATA_USER_PHONE, new ISavable() {
                            @Override
                            public void onDataRead(Object exist) {
                                if((boolean)exist)
                                {
                                    stepper.go(mStepperLayout,4);
                                }
                                else
                                {
                                    stepper.goNext(mStepperLayout);
                                }
                            }
                        });
                    }
                }
            } );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null) return;
        if(resultCode == RESULT_OK && requestCode == image.IMAGE_PICK_CODE)
        {
            Uri imageUri = image.getImageUri(data);
            image.cropImage(imageUri,getContext(),StepThreeRegisterFragment.this);
        }
        else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK)
        {
            Uri imageUriResultCrop = image.getCropOutput(data);
            image.setImageUri(imageUriResultCrop.toString(),mv_userLogo);
            imageUriResult = imageUriResultCrop;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );

        switch (requestCode)
        {
            case PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    image.pickImageFromGallery(this);
                }
                else
                {
                    Toast.makeText ( getContext(),"Premission denied...!",Toast.LENGTH_LONG ).show ();
                }

                break;
        }
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        return null;
    }

    @Override
    public void onSelected() {

        isImageUploaded = false; isNameUploaded = false;

        //update UI when selected
        if (DataPass.passData != null) {
            userData = DataPass.passData;
            if(userData.get(ConstantNames.USER) != null)
            {
                user = (User)userData.get(ConstantNames.USER);
                if(user.getFullName() != null)
                {
                    ed_name.setText(user.getFullName());
                }
                if(user.getLogo() != null && user.getLogo() != "")
                {
                    image.setImageUri(user.getLogo(),mv_userLogo);
                }
            }
        }
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }
}
