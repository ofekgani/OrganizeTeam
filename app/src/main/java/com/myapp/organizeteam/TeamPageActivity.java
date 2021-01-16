package com.myapp.organizeteam;

import androidx.annotation.NonNull;
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
import com.myapp.organizeteam.Adapters.UsersListAdapter;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.Resources.OpenMenu;

import java.util.ArrayList;

public class TeamPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UsersListAdapter.AdapterListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    OpenMenu openMenu;
    ImageView nav_logo;

//    TextView tv_teamName, tv_managerName;
//    ImageView mv_teamLogo, mv_managerLogo;
//    ListView lv_users;
//
//    ActivityTransition activityTransition;
    Image image;
//
    Intent intent;
//    UsersListAdapter adapter;
//
//    String teamName,teamLogo,managerName, managerLogo;
//
    User user;
    User manager;
    Team team;
//
//    ArrayList<User> requestsList;
//    ArrayList<User> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_team_page );

        drawerLayout = findViewById ( R.id.drawer_layout );
        navigationView = findViewById ( R.id.nav_view );

        openMenu = new OpenMenu(R.id.main_toolbar);

//        tv_teamName = findViewById ( R.id.tv_teamName );
//        mv_teamLogo = findViewById ( R.id.mv_teamLogo );
//
//        tv_managerName = findViewById ( R.id.tv_mangerName );
//        mv_managerLogo = findViewById ( R.id.mv_mangerLogo );
//
//        lv_users = findViewById(R.id.lv_usersList);
//
//        activityTransition = new ActivityTransition ();
        image = new Image ();
//
        intent = getIntent ();
        team = (Team)intent.getSerializableExtra ( ConstantNames.TEAM );
//
//        //Get Team data.
//        updateTeamUI();
//
//        //Get Manager data.
//        updateManagerUI();
//
//        if(isManager())
//        {
//            createUsersList();
//        }

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar (toolbar);

        openMenu.createMenu(this,drawerLayout);
        navigationView.setNavigationItemSelectedListener ((NavigationView.OnNavigationItemSelectedListener) this);

        nav_logo = (ImageView) openMenu.getResource ( navigationView,R.id.nav_logo );
        TextView nav_name = (TextView) openMenu.getResource ( navigationView,R.id.nav_name );

        nav_name.setText(team.getName());
        image.setImageUri ( team.getLogo(),nav_logo );

        if (savedInstanceState == null) {
            HomeFragment fragment = new HomeFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
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
                Bundle bundle = new Bundle();
                bundle.putSerializable(ConstantNames.TEAM_HOST, (User)intent.getSerializableExtra ( ConstantNames.TEAM_HOST ));
                bundle.putSerializable(ConstantNames.TEAM, (Team)intent.getSerializableExtra ( ConstantNames.TEAM ));
                bundle.putSerializable(ConstantNames.USER, (User)intent.getSerializableExtra ( ConstantNames.USER ));

                ParticipantsFragment toFragment = new ParticipantsFragment();
                toFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, toFragment,"participantsFragment").commit();

                openMenu.setCheckedItem(navigationView,R.id.btn_participants);
                break;

            case R.id.btn_home:
                HomeFragment fragment = new HomeFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                openMenu.setCheckedItem(navigationView,R.id.btn_home);
                break;
        }
        openMenu.closeMenu ( drawerLayout );
        return true;
    }


//    private boolean isManager() {
//        user = (User)intent.getSerializableExtra(ConstantNames.USER);
//        if(manager.getKeyID().equals(user.getKeyID()))
//        {
//            Toast.makeText(this,"You are the manager!",Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        else
//        {
//            Toast.makeText(this,"You are not the manager!",Toast.LENGTH_SHORT).show();
//            return false;
//        }
//    }
//
//    private void updateManagerUI() {
//        manager = (User)intent.getSerializableExtra ( ConstantNames.TEAM_HOST );
//        if(manager != null)
//        {
//            managerName = manager.getFullName ();
//            managerLogo = manager.getLogo ();
//        }
//        tv_managerName.setText ( ""+managerName );
//        image.setImageUri (managerLogo, mv_managerLogo );
//    }
//
//    private void updateTeamUI() {
//        team = (Team)intent.getSerializableExtra ( ConstantNames.TEAM );
//        if(team != null)
//        {
//            teamName = team.getName();
//            teamLogo = team.getLogo();
//        }
//        image.setImageUri ( teamLogo,mv_teamLogo );
//        tv_teamName.setText ( ""+teamName );
//    }
//
//    private void createUsersList()
//    {
//        final DataExtraction dataExtraction = new DataExtraction();
//        dataExtraction.getAllUsersByTeam(team.getKeyID(), ConstantNames.DATA_REQUEST_TO_JOIN,new ISavable() {
//            @Override
//            public void onDataRead(Object save) {
//                requestsList = (ArrayList<User>)save;
//                dataExtraction.getAllUsersByTeam(team.getKeyID(), ConstantNames.DATA_USERS_AT_TEAM, new ISavable() {
//                            @Override
//                            public void onDataRead(Object save) {
//                                usersList = (ArrayList<User>)save;
//                                requestsList.addAll(usersList);
//                                setAdapter(requestsList);
//                            }
//                        });
//
//            }
//        });
//    }
//
//    private void setAdapter(ArrayList<User> users) {
//        adapter = new UsersListAdapter(this,R.layout.adapter_users_list,users,team.getKeyID());
//        lv_users.setAdapter ( adapter );
//    }
//
//    public void oc_signOut(View view) {
//        Authorization authorization = new Authorization ();
//        authorization.singOut ( this );
//    }
//
//    /**
//     * called any time the users list has change.
//     * @param position The item from the list to delete.
//     */
    @Override
    public void updateList(boolean accept, int position) {
        ParticipantsFragment participantsFragment = (ParticipantsFragment) getFragmentManager().findFragmentByTag("participantsFragment");
        if(participantsFragment != null)
            participantsFragment.updateList(accept, position);
    }

    public void oc_createMeeting(View view) {
        ActivityTransition activityTransition = new ActivityTransition();
        activityTransition.goTo(TeamPageActivity.this,CreateMeetingActivity.class,false,null,null);
    }
}