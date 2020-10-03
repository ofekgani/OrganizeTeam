package com.example.organizeteam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.organizeteam.AuthorizationSystem.UserInfo;
import com.example.organizeteam.Core.ConstantNames;
import java.util.ArrayList;

import com.example.organizeteam.Core.ActivityTransition;
import  com.example.organizeteam.AuthorizationSystem.Authorization;
import com.squareup.picasso.Picasso;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class TeamListActivity extends AppCompatActivity {

    ListView listView;
    TextView tv_name, tv_email;
    ImageView mv_logo;
    
    ActivityTransition activityTransition;
    Authorization authorization;
    UserInfo userInfo;

    Uri imageUri;
    String email,name,userID,logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_team_list );

        //References
        listView = findViewById ( R.id.lv_teams );
        tv_name = findViewById ( R.id.tv_userName );
        tv_email = findViewById ( R.id.tv_userEmail );
        mv_logo = findViewById ( R.id.mv_logo );

        //allocating memory
        activityTransition = new ActivityTransition ();
        authorization = new Authorization ();
        userInfo = new UserInfo ();

        Intent intent = getIntent (  );
        email = activityTransition.getData (intent, ConstantNames.USER_EMAIL );
        name = activityTransition.getData ( intent,ConstantNames.USER_NAME );
        userID = activityTransition.getData ( intent,ConstantNames.USER_KEY_ID );
        logo = activityTransition.getData ( intent,ConstantNames.USER_LOGO );

        tv_name.setText ( ""+name );
        tv_email.setText ( ""+email );
        
        //load image from firebase
        Picasso.get ().load(logo).into(mv_logo);

        createTeamList ();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if(isImageResult ( requestCode, resultCode, data ))
        {
            setImage ( data );
            uploadPicture();
        }
    }

    private void createTeamList() {
        Team electroBunny =new Team ( "electro bunny", "the best team ever" );
        Team desertEagles = new Team ( "Desert Eagles", "the worst team ever" );

        ArrayList<Team> teamList = new ArrayList<> (  );
        teamList.add ( electroBunny );
        teamList.add ( desertEagles );

        TeamListAdapter adapter = new TeamListAdapter(this,R.layout.team_adapter_view_layout,teamList);
        listView.setAdapter ( adapter );
    }

    private void setImage(@Nullable Intent data) {
        imageUri = data.getData ();
        mv_logo.setImageURI ( imageUri );
    }

    private boolean isImageResult(int requestCode, int resultCode, @Nullable Intent data) {
        return requestCode == 1 && resultCode==RESULT_OK && data != null && data.getData () != null;
    }

    private void uploadPicture()
    {
        userInfo.uploadPicture(imageUri,TeamListActivity.this,userID,email);
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_singOut(View view) {
        //sign out
        authorization.singOut ( TeamListActivity.this );
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
    public void oc_chooseImage(View view) {
        activityTransition.goToGallery (this);
    }
}