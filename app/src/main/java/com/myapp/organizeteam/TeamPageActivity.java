package com.myapp.organizeteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.organizeteam.Adapters.MeetingsListAdapter;
import com.myapp.organizeteam.Adapters.UsersRequestsListAdapter;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.Resources.OpenMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.myapp.organizeteam.DataManagement.Authorization.isManager;

public class TeamPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UsersRequestsListAdapter.AdapterListener, MeetingsListAdapter.AdapterListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    OpenMenu openMenu;
    ImageView nav_logo;
    FloatingActionButton fab_createMeeting,fab_createRole;

    Image image;
    DataExtraction dataExtraction;

    Intent intent;

    User user, manager;
    Team team;
    ArrayList<Role> meetingsPer;
    ArrayList<Role> userRoles;

    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_team_page );

        drawerLayout = findViewById ( R.id.drawer_layout );
        navigationView = findViewById ( R.id.nav_view );
        fab_createMeeting = findViewById(R.id.fb_createMeeting);
        fab_createRole = findViewById(R.id.fb_createRole);

        openMenu = new OpenMenu(R.id.main_toolbar);

        image = new Image ();
        dataExtraction = new DataExtraction();

        intent = getIntent ();
        team = (Team)intent.getSerializableExtra ( ConstantNames.TEAM );

        if((ArrayList<Role>)intent.getSerializableExtra(ConstantNames.USER_ROLES) != null)
        {
            userRoles = (ArrayList<Role>) intent.getSerializableExtra ( ConstantNames.USER_ROLES );
        }
        else
        {
            userRoles = new ArrayList<>();
        }

        if((ArrayList<Role>)intent.getSerializableExtra(ConstantNames.USER_PERMISSIONS_MEETING) != null)
        {
            meetingsPer = (ArrayList<Role>)intent.getSerializableExtra(ConstantNames.USER_PERMISSIONS_MEETING);
        }
        else
        {
            meetingsPer = new ArrayList<>();
        }

        accessPermissions();

        manager = (User)intent.getSerializableExtra(ConstantNames.TEAM_HOST);
        user = (User)intent.getSerializableExtra(ConstantNames.USER);

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
        bundle.putSerializable(ConstantNames.USER_ROLES, intent.getSerializableExtra ( ConstantNames.USER_ROLES ));

        if (savedInstanceState == null) {
            HomeFragment fragment = new HomeFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,"homeFragment").commit();
            openMenu.setCheckedItem(navigationView, R.id.btn_home);
        }

        fab_createMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityTransition activityTransition = new ActivityTransition();

                Map<String,Object> save = new HashMap<>();
                save.put(ConstantNames.TEAM,team);
                save.put(ConstantNames.USER,intent.getSerializableExtra ( ConstantNames.USER ));
                save.put(ConstantNames.USER_PERMISSIONS_MEETING,intent.getSerializableExtra ( ConstantNames.USER_PERMISSIONS_MEETING ));
                activityTransition.goToWithResult(TeamPageActivity.this,CreateMeetingActivity.class,976,save,null);
            }
        });

        fab_createRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityTransition activityTransition = new ActivityTransition();

                Map<String,Object> save = new HashMap<>();
                save.put(ConstantNames.TEAM_KEY_ID,team.getKeyID());
                activityTransition.goTo(TeamPageActivity.this,CreateRoleActivity.class,false,save,null);
            }
        });

        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_ACTIVITY_PATH).child(team.getKeyID()).child(user.getKeyID());
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataExtraction.getAllRolesByUser(user.getKeyID(), team.getKeyID(), new ISavable() {
                    @Override
                    public void onDataRead(Object save) {
                        userRoles = (ArrayList<Role>) save;
                        dataExtraction.getPermissions(team.getKeyID(), userRoles, new ISavable() {
                            @Override
                            public void onDataRead(Object save) {
                                meetingsPer = (ArrayList<Role>) save;
                                accessPermissions();
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference rollDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.ROLE_PATH).child(team.getKeyID());
        rollDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataExtraction.getAllRolesByUser(user.getKeyID(), team.getKeyID(), new ISavable() {
                    @Override
                    public void onDataRead(Object save) {
                        userRoles = (ArrayList<Role>) save;
                        dataExtraction.getPermissions(team.getKeyID(), userRoles, new ISavable() {
                            @Override
                            public void onDataRead(Object save) {
                                meetingsPer = (ArrayList<Role>) save;
                                accessPermissions();
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void accessPermissions() {

        fab_createMeeting.setVisibility(View.GONE);
        fab_createRole.setVisibility(View.GONE);
        if((meetingsPer.size() > 0  && !meetingsPer.isEmpty()) || isManager)
        {
            fab_createMeeting.setVisibility(View.VISIBLE);
        }
        else
        {
            fab_createMeeting.setVisibility(View.GONE);
        }
        if(isManager)
        {
            fab_createRole.setVisibility(View.VISIBLE);
        }
        else
        {
            fab_createRole.setVisibility(View.GONE);
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