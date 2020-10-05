package com.example.organizeteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.organizeteam.AuthorizationSystem.UserInfo;
import com.example.organizeteam.Core.ConstantNames;
import java.util.ArrayList;

import com.example.organizeteam.Core.ActivityTransition;
import  com.example.organizeteam.AuthorizationSystem.Authorization;
import com.example.organizeteam.Resources.Image;
import com.example.organizeteam.Resources.OpenMenu;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class TeamListActivity extends AppCompatActivity {

    ListView listView;
    TextView tv_name;
    ImageView mv_logo, nav_logo;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    ActivityTransition activityTransition;
    Authorization authorization;
    UserInfo userInfo;
    OpenMenu openMenu;
    Image image;

    Uri imageUri;
    String email,name,userID,logo;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_team_list );

        //References
        listView = findViewById ( R.id.lv_teams );
        tv_name = findViewById ( R.id.tv_userName );
        mv_logo = findViewById ( R.id.mv_logo );
        drawerLayout = findViewById ( R.id.drawer_layout );
        navigationView = findViewById ( R.id.nav_view );

        //allocating memory
        activityTransition = new ActivityTransition ();
        authorization = new Authorization ();
        userInfo = new UserInfo ();
        openMenu = new OpenMenu ();
        image = new Image ();

        openMenu.createMenu (this,drawerLayout,navigationView );
        View hView = openMenu.getHeaderView ( navigationView );
        nav_logo = hView.findViewById ( R.id.nav_logo );


        Intent intent = getIntent (  );
        email = activityTransition.getData (intent, ConstantNames.USER_EMAIL );
        name = activityTransition.getData ( intent,ConstantNames.USER_NAME );
        userID = activityTransition.getData ( intent,ConstantNames.USER_KEY_ID );
        logo = activityTransition.getData ( intent,ConstantNames.USER_LOGO );
        image.setImageFromUri ( logo,nav_logo );

        TextView nav_email = hView.findViewById(R.id.nav_email);
        TextView nav_name = hView.findViewById(R.id.nav_name);
        nav_email.setText(""+email);
        nav_name.setText(""+name);


        tv_name.setText ( ""+name );
        
        //load image from firebase
        image.setImageFromUri ( logo,mv_logo );

        createTeamList ();

        navigationView.setCheckedItem(R.id.btn_home);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen ( GravityCompat.START ))
        {
            drawerLayout.closeDrawer ( GravityCompat.START );
        }
        else
        {
            super.onBackPressed ();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE)
        {
            imageUri = data.getData ();
            mv_logo.setImageURI ( imageUri );
            //nav_logo.setImageURI ( data.getData () );
            uploadPicture ();
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
                    pickImageFromGallery();
                }
                else
                {
                    Toast.makeText ( this,"Premission denied...!",Toast.LENGTH_LONG ).show ();
                }
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

    private void uploadPicture()
    {
        userInfo.uploadPicture(imageUri,TeamListActivity.this,userID,email);
    }

    private void pickImageFromGallery()
    {
        Intent intent = new Intent ( Intent.ACTION_PICK  );
        intent.setType ( "image/*" );
        startActivityForResult (intent,IMAGE_PICK_CODE);
    }

    /**
     * Called when a native click event is fired.
     * @param view the view that was fired.
     */
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
                pickImageFromGallery();
            }
        }
        else
        {
            pickImageFromGallery();
        }
    }
}