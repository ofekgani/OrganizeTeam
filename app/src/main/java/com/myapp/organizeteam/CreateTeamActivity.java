package com.myapp.organizeteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.Core.Team;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class CreateTeamActivity extends AppCompatActivity {

    EditText ed_name, ed_description;
    ImageView mv_logo;

    ActivityTransition activityTransition = new ActivityTransition ();
    DataExtraction dataExtraction = new DataExtraction ();
    InputManagement userInput = new InputManagement ();
    Image image = new Image ();

    Intent intent;

    User user;
    String userID;
    Uri imageUri, imageUriResultCrop;

    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_create_team );

        ed_name = findViewById ( R.id.ed_teamName );
        ed_description = findViewById ( R.id.ed_teamDescription );
        mv_logo = findViewById ( R.id.mv_teamLogo );

        intent = getIntent (  );
        user = (User)intent.getSerializableExtra(ConstantNames.USER);
        userID = user.getKeyID();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if(image.imageSelectedFromGallery ( requestCode, resultCode ))
        {
            imageUri = image.getImageUri ( data );
            image.cropImage ( imageUri,this );
        }
        else if(image.isImageCropped ( requestCode, resultCode ))
        {
            imageUriResultCrop = image.getCropOutput ( data );
            image.setImageUri ( imageUriResultCrop.toString (),mv_logo );
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
        }
    }

    private void uploadPicture(Uri image,String where,final String id)
    {
        dataExtraction.uploadPicture ( image, CreateTeamActivity.this, ConstantNames.TEAM_PATH, id, where, new ISavable() {
            @Override
            public void onDataRead(Object uri) {
                saveAllData ( id, (String) uri );
                activityTransition.setData (intent, ConstantNames.USER_LOGO,(String) uri);
            }
        } );
    }

    public void oc_createTeam(View view) {

        if(userInput.isInputEmpty ( ed_name ))
        {
            userInput.setError ( ed_name,"This field is valid!" );
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance ().getReference ( ConstantNames.TEAM_PATH);
        String keyID = mDatabase.push ().getKey ();

        //Add the team to user`s teams.
        dataExtraction.setNewData ( ConstantNames.USER_PATH,userID,ConstantNames.DATA_USER_TEAMS,keyID );

        if(imageUriResultCrop != null)
        {
            uploadPicture (imageUriResultCrop,keyID,keyID);
        }
        else
        {
            saveAllData ( keyID, "" );
        }

    }

    private void saveAllData(String keyID, String logoUri) {

        String name = userInput.getInput ( ed_name );
        String description = userInput.getInput ( ed_description );

        //Save the new team into firebase
        final Team team = new Team ( name, description, logoUri, userID, keyID );
        dataExtraction.setObject ( ConstantNames.TEAM_PATH, keyID, team );



        dataExtraction.getUserDataByID(team.getHost(), new ISavable() {
            @Override
            public void onDataRead(Object save) {
                Map<String,Object> values = new HashMap<>();
                values.put(ConstantNames.TEAM,team);
                values.put(ConstantNames.USER,user);
                values.put(ConstantNames.TEAM_HOST,save);
                //Save user`s teams to team list activity.
                activityTransition.goTo ( CreateTeamActivity.this,TeamPageActivity.class,true,values,null);
            }
        });

    }

    public void oc_back(View view) {
        activityTransition.back (this);
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
}