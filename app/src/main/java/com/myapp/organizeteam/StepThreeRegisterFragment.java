package com.myapp.organizeteam;

import android.Manifest;
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

import com.google.firebase.auth.FirebaseAuth;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.IRegister;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.Resources.Loading;
import com.myapp.organizeteam.Resources.Stepper;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class StepThreeRegisterFragment extends Fragment implements Step {

    StepperLayout mStepperLayout;

    ProgressBar pb;
    Button btn_next;
    EditText ed_name;
    ImageView mv_userLogo;
    DataExtraction dataExtraction;

    Authorization authorization;
    InputManagement input;
    Loading progressBar;
    Stepper stepper;
    Image image;

    Uri imageUriResult;
    boolean isImageUploaded = false, isNameUploaded = false;

    private static final int PERMISSION_CODE = 1001;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register_step3, container, false);

        pb = v.findViewById(R.id.pb_next);
        btn_next = v.findViewById(R.id.btn_next);
        mv_userLogo = v.findViewById(R.id.mv_userLogo);
        ed_name = v.findViewById(R.id.ed_name);

        //allocating memory
        authorization = new Authorization ();
        input = new InputManagement ();
        stepper = new Stepper();
        progressBar = new Loading ( );
        image = new Image();
        dataExtraction = new DataExtraction();

        mStepperLayout = stepper.getStepperLayout(container,R.id.stepperLayout);

        progressBar.setVisible ( pb,false );

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = input.getInput(ed_name);
                FirebaseAuth fba = FirebaseAuth.getInstance ();

                if(input.isInputEmpty(ed_name))
                {
                    input.setError(ed_name,"This field is required.");
                    return;
                }
                if(fba.getCurrentUser() == null) return;

                String email = fba.getCurrentUser().getEmail();

                //upload image to firebase if the user choice image.
                if(imageUriResult != null)
                {
                    uploadPicture (imageUriResult);
                }

                //Create new user with name in firebase realtime.
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
                            isNameUploaded = true;

                            if(imageUriResult == null || isImageUploaded == true)
                            {
                                stepper.goNext(mStepperLayout);
                            }
                        }
                    }
                });
            }
        });

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

    private void uploadPicture(Uri image)
    {
        FirebaseAuth fba = FirebaseAuth.getInstance();
        dataExtraction.uploadPicture ( image, getContext(), ConstantNames.USER_PATH, fba.getCurrentUser().getUid(),fba.getCurrentUser().getEmail(), new ISavable() {
            @Override
            public void onDataRead(Object uri) {
                isImageUploaded = true;
                if(isNameUploaded)
                    stepper.goNext(mStepperLayout);
            }
        } );
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
        //update UI when selected
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }

}
