package com.example.organizeteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.organizeteam.DataManagement.Authorization;
import com.example.organizeteam.Core.ActivityTransition;
import com.example.organizeteam.Resources.Image;
import com.example.organizeteam.Core.User;

public class TeamPageActivity extends AppCompatActivity {

    TextView tv_teamName, tv_managerName;
    ImageView mv_teamLogo, mv_managerLogo;

    ActivityTransition activityTransition;
    Image image;

    Intent intent;

    String teamName,teamLogo,managerName, managerLogo;

    User manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_team_page );

        tv_teamName = findViewById ( R.id.tv_teamName );
        mv_teamLogo = findViewById ( R.id.mv_teamLogo );

        tv_managerName = findViewById ( R.id.tv_mangerName );
        mv_managerLogo = findViewById ( R.id.mv_mangerLogo );

        activityTransition = new ActivityTransition ();
        image = new Image ();

       /* intent = getIntent ();

        //Get Team data.
        teamName = activityTransition.getData ( intent, ConstantNames.TEAM_NAME );
        teamLogo = activityTransition.getData ( intent, ConstantNames.TEAM_LOGO );

        image.setImageUri ( teamLogo,mv_teamLogo );
        tv_teamName.setText ( ""+teamName );

        //Get Manager data.
        manager = (User)intent.getSerializableExtra ( ConstantNames.TEAM_HOST );
        managerName = manager.getFullName ();
        managerLogo = manager.getLogo ();

        tv_managerName.setText ( ""+managerName );
        image.setImageUri (managerLogo, mv_managerLogo );*/

    }

    public void oc_signOut(View view) {
        Authorization authorization = new Authorization ();
        authorization.singOut ( this );
    }
}