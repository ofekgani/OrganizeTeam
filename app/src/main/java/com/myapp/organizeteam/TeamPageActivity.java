package com.myapp.organizeteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.myapp.organizeteam.Adapters.MeetingsListAdapter;
import com.myapp.organizeteam.Adapters.UsersListAdapter;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.Resources.OpenMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeamPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UsersListAdapter.AdapterListener, MeetingsListAdapter.AdapterListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    OpenMenu openMenu;
    ImageView nav_logo;

    Image image;

    Intent intent;

    Team team;

    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_team_page );

        drawerLayout = findViewById ( R.id.drawer_layout );
        navigationView = findViewById ( R.id.nav_view );

        openMenu = new OpenMenu(R.id.main_toolbar);

        image = new Image ();

        intent = getIntent ();
        team = (Team)intent.getSerializableExtra ( ConstantNames.TEAM );

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar (toolbar);

        openMenu.createMenu(this,drawerLayout);
        navigationView.setNavigationItemSelectedListener ((NavigationView.OnNavigationItemSelectedListener) this);

        nav_logo = (ImageView) openMenu.getResource ( navigationView,R.id.nav_logo );
        TextView nav_name = (TextView) openMenu.getResource ( navigationView,R.id.nav_name );

        nav_name.setText(team.getName());
        image.setImageUri ( team.getLogo(),nav_logo );

        bundle = new Bundle();
        bundle.putSerializable(ConstantNames.TEAM_HOST, (User)intent.getSerializableExtra ( ConstantNames.TEAM_HOST ));
        bundle.putSerializable(ConstantNames.TEAM, (Team)intent.getSerializableExtra ( ConstantNames.TEAM ));
        bundle.putSerializable(ConstantNames.USER, (User)intent.getSerializableExtra ( ConstantNames.USER ));

        if (savedInstanceState == null) {
            HomeFragment fragment = new HomeFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,"homeFragment").commit();
            openMenu.setCheckedItem(navigationView, R.id.btn_home);
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId ())
        {
            case R.id.btn_signOut:
                Authorization authorization = new Authorization();
                authorization.singOut ( this );
                break;

            case R.id.btn_participants:
                ParticipantsFragment toFragment = new ParticipantsFragment();
                toFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, toFragment,"participantsFragment").commit();

                openMenu.setCheckedItem(navigationView,R.id.btn_participants);
                break;

            case R.id.btn_home:
                HomeFragment fragment = new HomeFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,"homeFragment").commit();

                openMenu.setCheckedItem(navigationView,R.id.btn_home);
                break;
        }
        openMenu.closeMenu ( drawerLayout );
        return true;
    }

    @Override
    public void updateList(boolean accept, int position) {
        ParticipantsFragment participantsFragment = (ParticipantsFragment) getFragmentManager().findFragmentByTag("participantsFragment");
        if(participantsFragment != null)
            participantsFragment.updateList(accept, position);
    }

    public void oc_createMeeting(View view) {
        ActivityTransition activityTransition = new ActivityTransition();

        Map<String,Object> save = new HashMap<>();
        save.put(ConstantNames.TEAM,team);
        save.put(ConstantNames.USER,(User)intent.getSerializableExtra ( ConstantNames.USER ));
        activityTransition.goToWithResult(TeamPageActivity.this,CreateMeetingActivity.class,976,save,null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        HomeFragment homeFragment = (HomeFragment) getFragmentManager().findFragmentByTag("homeFragment");
        if(homeFragment != null)
            homeFragment.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void updateList(int position) {
        HomeFragment homeFragment = (HomeFragment) getFragmentManager().findFragmentByTag("homeFragment");
        if(homeFragment != null)
            homeFragment.updateList(position);
    }
}