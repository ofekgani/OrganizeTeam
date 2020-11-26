package com.myapp.organizeteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    ActivityTransition activityTransition;
    Authorization authorization;
    DataExtraction dataExtraction;
    Image image;

    Intent intent;
    User user;

    TextView tv_userName, tv_userEmail;
    ImageView mv_logo;

    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        tv_userEmail = findViewById(R.id.tv_userEmail);
        tv_userName = findViewById(R.id.tv_userName);
        mv_logo = findViewById(R.id.mv_userLogo);

         activityTransition = new ActivityTransition();
         authorization = new Authorization();
         dataExtraction = new DataExtraction();
         image = new Image();

        intent = getIntent();
        user = (User)intent.getSerializableExtra ( ConstantNames.USER );

        tv_userName.setText(""+user.getFullName());
        tv_userEmail.setText(""+user.getEmail());

        image.setImageUri ( user.getLogo(),mv_logo );
    }

    public void oc_joinToTeam(View view) {
        //get all teams
        authorization.getAllTeams(new ISavable() {
            @Override
            public void onDataRead(Object save) {
                ArrayList<Team> teamList = (ArrayList<Team>)save;
                Map<String,Object> data = new HashMap<>();

                data.put(ConstantNames.TEAMS_LIST,teamList);
                data.put(ConstantNames.USER,user);
                activityTransition.goTo(WelcomeActivity.this,SelectTeamActivity.class,false,data,null);
            }
        });
    }

    public void oc_createTeam(View view) {
        Map<String,Object> values = new HashMap<> (  );
        values.put ( ConstantNames.USER,user);

        activityTransition.goTo( this,CreateTeamActivity.class, false, values ,null );
    }

    public void oc_signOut(View view) {
        authorization.singOut(this);
    }

    public void oc_chooseImage(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission ( Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_DENIED)
            {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions ( permissions,PERMISSION_CODE );
            }
            else
            {
                image.pickImageFromGallery(this);
            }
        }
        else
        {
            image.pickImageFromGallery(this);
        }
    }

    private void uploadPicture(Uri image)
    {
        dataExtraction.uploadPicture ( image, WelcomeActivity.this, ConstantNames.USER_PATH, user.getKeyID(), user.getEmail(), intent, new ISavable() {
            @Override
            public void onDataRead(Object uri) {

            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if(image.imageSelectedFromGallery ( requestCode, resultCode ))
        {
            Uri imageUri = image.getImageUri ( data );
            image.cropImage ( imageUri,this );
        }
        else if(image.isImageCropped ( requestCode, resultCode ))
        {
            Uri imageUriResultCrop = image.getCropOutput ( data );
            image.setImageUri ( imageUriResultCrop.toString (),mv_logo );

            uploadPicture (imageUriResultCrop);

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
                    Toast.makeText ( this,"Premission denied...!",Toast.LENGTH_LONG ).show ();
                }

                break;
        }
    }
}