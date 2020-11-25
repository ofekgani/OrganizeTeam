package com.myapp.organizeteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.Core.ConstantNames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.Resources.OpenMenu;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Adapters.TeamListAdapter;
import com.myapp.organizeteam.Core.User;
import com.google.android.material.navigation.NavigationView;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class TeamListActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    ListView listView;
    TextView tv_name;
    ImageView mv_logo, nav_logo;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    ActivityTransition activityTransition;
    Authorization authorization;
    DataExtraction dataExtraction;
    OpenMenu openMenu;
    Image image;

    Intent intent;

    Uri imageUri, imageUriResultCrop;
    String email,name,userID,logo;

    Map<String, Team> teams;
    User user;

    ArrayList<Team> teamList;
    TeamListAdapter adapter;

    private static final int PERMISSION_CODE = 1001;
    private static final int TEAM_CREATE_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_team_list );

        //References
        listView = findViewById ( R.id.lv_teams );
        tv_name = findViewById ( R.id.tv_userName );
        mv_logo = findViewById ( R.id.mv_userLogo);
        drawerLayout = findViewById ( R.id.drawer_layout );
        navigationView = findViewById ( R.id.nav_view );

        //allocating memory
        activityTransition = new ActivityTransition ();
        authorization = new Authorization ();
        dataExtraction = new DataExtraction ();
        openMenu = new OpenMenu ();
        image = new Image ();

        openMenu.createMenu (this,drawerLayout );
        nav_logo = (ImageView) openMenu.getResource ( navigationView,R.id.nav_logo );
        navigationView.setNavigationItemSelectedListener ( this );
        TextView nav_email = (TextView) openMenu.getResource ( navigationView,R.id.nav_email );
        TextView nav_name =(TextView) openMenu.getResource ( navigationView,R.id.nav_name );

        intent = getIntent (  );
        user = (User) intent.getSerializableExtra ( ConstantNames.USER);
        email = user.getEmail ();
        name = user.getFullName ();
        userID = user.getKeyID ();
        logo = user.getLogo ();
        teamList = (ArrayList<Team>)intent.getSerializableExtra ( ConstantNames.TEAMS_LIST);

        nav_email.setText(""+email);
        nav_name.setText(""+name);
        tv_name.setText ( ""+name );
        image.setImageUri ( logo,mv_logo );
        image.setImageUri ( logo,nav_logo );

        createTeamList ();

        openMenu.setCheckedItem(navigationView,R.id.btn_home);
    }

    @Override
    public void onBackPressed() {
        openMenu.toggleMenu (drawerLayout);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId ())
        {
            case R.id.btn_signOut:
                authorization.singOut ( this );
                break;

            case R.id.btn_create_team:
                Map<String,Object> values = new HashMap<> (  );
                values.put ( ConstantNames.USER_KEY_ID,userID );
                values.put ( ConstantNames.USER_TEAM,teams );

                activityTransition.goToWithResult ( this,CreateTeamActivity.class, TEAM_CREATE_CODE, values ,null );
                break;
        }
        openMenu.closeMenu ( drawerLayout );
        return true;
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
            image.setImageUri ( imageUriResultCrop.toString (),nav_logo );

            uploadPicture (imageUriResultCrop);

        }
        else if(requestCode == TEAM_CREATE_CODE && resultCode == Activity.RESULT_OK)
        {
            teamList = (ArrayList<Team>)intent.getSerializableExtra ( ConstantNames.TEAMS_LIST);
            adapter.notifyDataSetChanged();
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

    private void createTeamList() {
        listView.setOnItemClickListener ( this );
        listView.setChoiceMode ( ListView.CHOICE_MODE_SINGLE );

        adapter = new TeamListAdapter(this,R.layout.team_adapter_view_layout,teamList);
        listView.setAdapter ( adapter );
    }

    private void uploadPicture(Uri image)
    {
        dataExtraction.uploadPicture ( image, TeamListActivity.this, ConstantNames.USER_PATH, userID, email, intent, new ISavable() {
            @Override
            public void onDataRead(Object uri) {

            }
        } );
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
                image.pickImageFromGallery(this);
            }
        }
        else
        {
            image.pickImageFromGallery(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final String teamName = teamList.get ( i ).getName ();
        final String teamLogo = teamList.get ( i ).getLogo ();
        String hostID = teamList.get ( i ).getHost ();

        dataExtraction.getUserDataByID( hostID, new ISavable () {
            @Override
            public void onDataRead(Object data) {
                Map<String, Object> save = (Map<String, Object>)data;
                save.put ( ConstantNames.TEAM_NAME,teamName );
                save.put ( ConstantNames.TEAM_LOGO,teamLogo );
                activityTransition.goTo ( TeamListActivity.this, TeamPageActivity.class,false,save,null );
            }
        } );


    }
}