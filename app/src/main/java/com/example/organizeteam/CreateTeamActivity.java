package com.example.organizeteam;

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

import com.example.organizeteam.AuthorizationSystem.IPicture;
import com.example.organizeteam.AuthorizationSystem.UserInput;
import com.example.organizeteam.Core.ActivityTransition;
import com.example.organizeteam.AuthorizationSystem.UserInfo;
import com.example.organizeteam.Core.ConstantNames;
import com.example.organizeteam.Resources.Image;
import com.example.organizeteam.Resources.Team;
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
    UserInfo userInfo = new UserInfo ();
    UserInput userInput = new UserInput ();
    Image image = new Image ();

    Intent intent;

    String userID;
    Map<String, Team> teams;
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

        userID = activityTransition.getData ( intent,ConstantNames.USER_KEY_ID );
        teams = (Map<String, Team>) intent.getSerializableExtra ( ConstantNames.USER_TEAMS );

        if(teams == null)
            teams = new HashMap<> (  );
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
        userInfo.uploadPicture ( image, CreateTeamActivity.this, ConstantNames.TEAM_PATH, id, where, intent, new IPicture () {
            @Override
            public void onUploadImage(String uri) {
                saveAllData ( id, uri );
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
        userInfo.setNewData ( ConstantNames.USER_PATH,userID,ConstantNames.DATA_USER_TEAMS,keyID );

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
        Team team = new Team ( name, description, logoUri, keyID );
        userInfo.setObject ( ConstantNames.TEAM_PATH, keyID, team );

        //Save user`s teams to team list activity.
        teams.put ( keyID, team );
        Map<String, Object> values = new HashMap<> ();
        values.put ( ConstantNames.USER_TEAMS, teams );
        activityTransition.back ( CreateTeamActivity.this, values );
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