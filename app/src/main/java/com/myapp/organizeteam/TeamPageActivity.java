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
import com.myapp.organizeteam.Adapters.UsersRequestsListAdapterRel;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.FileManage;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.Resources.OpenMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.myapp.organizeteam.DataManagement.Authorization.isManager;

public class TeamPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UsersRequestsListAdapterRel.AdapterListener, MeetingsListAdapter.AdapterListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    OpenMenu openMenu;
    ImageView nav_logo;
    FloatingActionButton fab_createMeeting,fab_createRole, fab_createTask;

    FileManage fileManage;
    DataExtraction dataExtraction;

    Intent intent;

    User user, manager;
    Team team;
    ArrayList<Role> meetingsPer,tasksPer;
    ArrayList<Role> userRoles,roles;

    ArrayList<User> users,requests;

    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_team_page );

        drawerLayout = findViewById ( R.id.drawer_layout );
        navigationView = findViewById ( R.id.nav_view );
        fab_createMeeting = findViewById(R.id.fb_createMeeting);
        fab_createRole = findViewById(R.id.fb_createRole);
        fab_createTask = findViewById(R.id.fb_createTask);

        openMenu = new OpenMenu(R.id.main_toolbar);

        fileManage = new FileManage();
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

        if((ArrayList<Role>)intent.getSerializableExtra(ConstantNames.USER_PERMISSIONS_TASK) != null)
        {
            tasksPer = (ArrayList<Role>)intent.getSerializableExtra(ConstantNames.USER_PERMISSIONS_TASK);
        }
        else
        {
            tasksPer = new ArrayList<>();
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
        fileManage.setImageUri ( team.getLogo(),nav_logo );

        bundle = new Bundle();
        bundle.putSerializable(ConstantNames.TEAM_HOST, (User)intent.getSerializableExtra ( ConstantNames.TEAM_HOST ));
        bundle.putSerializable(ConstantNames.TEAM, (Team)intent.getSerializableExtra ( ConstantNames.TEAM ));
        bundle.putSerializable(ConstantNames.USER, (User)intent.getSerializableExtra ( ConstantNames.USER ));
        bundle.putSerializable(ConstantNames.USER_ROLES, userRoles);

        if (savedInstanceState == null) {
            MeetingsFragment fragment = new MeetingsFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,"homeFragment").commit();
            openMenu.setCheckedItem(navigationView, R.id.btn_meetings);
        }

        fab_createMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityTransition activityTransition = new ActivityTransition();

                Map<String,Object> save = new HashMap<>();
                save.put(ConstantNames.TEAM,team);
                save.put(ConstantNames.USER,intent.getSerializableExtra ( ConstantNames.USER ));
                save.put(ConstantNames.USER_PERMISSIONS,meetingsPer);
                activityTransition.goToWithResult(TeamPageActivity.this,CreateMeetingActivity.class,976,save,null);
            }
        });

        fab_createRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityTransition activityTransition = new ActivityTransition();

                Map<String,Object> save = new HashMap<>();
                save.put(ConstantNames.TEAM_KEY_ID,team.getKeyID());
                activityTransition.goToWithResult(TeamPageActivity.this,CreateRoleActivity.class,999,save,null);
            }
        });

        fab_createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityTransition activityTransition = new ActivityTransition();

                Map<String,Object> save = new HashMap<>();
                save.put(ConstantNames.TEAM,team);
                save.put(ConstantNames.USER,intent.getSerializableExtra ( ConstantNames.USER ));
                save.put(ConstantNames.USER_PERMISSIONS,tasksPer);
                activityTransition.goToWithResult(TeamPageActivity.this,CreateTaskActivity.class,979,save,null);
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
                                Map<String,Object> permissions = (Map<String, Object>) save;
                                meetingsPer = (ArrayList<Role>) permissions.get(ConstantNames.USER_PERMISSIONS_MEETING);
                                tasksPer = (ArrayList<Role>) permissions.get(ConstantNames.USER_PERMISSIONS_TASK);
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
                                Map<String,Object> permissions = (Map<String, Object>) save;
                                meetingsPer = (ArrayList<Role>) permissions.get(ConstantNames.USER_PERMISSIONS_MEETING);
                                tasksPer = (ArrayList<Role>) permissions.get(ConstantNames.USER_PERMISSIONS_TASK);
                                accessPermissions();
                            }
                        });
                    }
                });

                dataExtraction.getAllRolesByTeam(team.getKeyID(), new ISavable() {
                    @Override
                    public void onDataRead(Object save) {
                        roles = (ArrayList<Role>)save;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.TEAM_PATH).child(team.getKeyID()).child(ConstantNames.DATA_USERS_LIST);
        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataExtraction.getAllUsersByTeam(team.getKeyID(), ConstantNames.DATA_USERS_LIST, new ISavable() {
                    @Override
                    public void onDataRead(Object save) {
                        users = (ArrayList<User>) save;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference joinRequestsDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.TEAM_PATH).child(team.getKeyID()).child(ConstantNames.DATA_REQUEST_TO_JOIN);
        joinRequestsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataExtraction.getAllUsersByTeam(team.getKeyID(), ConstantNames.DATA_REQUEST_TO_JOIN, new ISavable() {
                    @Override
                    public void onDataRead(Object save) {
                        requests = (ArrayList<User>) save;
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
        fab_createTask.setVisibility(View.GONE);

        if(isManager)
        {
            fab_createMeeting.setVisibility(View.VISIBLE);
            fab_createRole.setVisibility(View.VISIBLE);
            fab_createTask.setVisibility(View.VISIBLE);
            return;
        }

        if(meetingsPer != null)
        {
            if(meetingsPer.size() > 0 && !meetingsPer.isEmpty())
            {
                fab_createMeeting.setVisibility(View.VISIBLE);
            }
            else
            {
                fab_createMeeting.setVisibility(View.GONE);
            }
        }

        if (tasksPer != null)
        {
            if(tasksPer.size() > 0  && !tasksPer.isEmpty())
            {
                fab_createTask.setVisibility(View.VISIBLE);
            }
            else
            {
                fab_createTask.setVisibility(View.GONE);
            }
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
                bundle.putSerializable(ConstantNames.USERS_LIST, users);
                bundle.putSerializable(ConstantNames.REQUESTS_LIST, requests);
                bundle.putSerializable(ConstantNames.ROLES_LIST, roles);
                ParticipantsFragment toFragment = new ParticipantsFragment();
                toFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, toFragment,"participantsFragment").commit();

                openMenu.setCheckedItem(navigationView,R.id.btn_participants);
                break;

            case R.id.btn_meetings:
                MeetingsFragment fragment = new MeetingsFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,"homeFragment").commit();

                openMenu.setCheckedItem(navigationView,R.id.btn_meetings);
                break;

            case R.id.btn_tasks:
                TasksFragment tasksFragment = new TasksFragment();
                tasksFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, tasksFragment,"tasksFragment").commit();

                openMenu.setCheckedItem(navigationView,R.id.btn_tasks);
        }
        openMenu.closeMenu ( drawerLayout );
        return true;
    }

    @Override
    public void updateList(boolean accept, int position) {
        UsersListFragment usersListFragment = (UsersListFragment) getFragmentManager().findFragmentByTag("usersListFragment");
        if(usersListFragment != null)
            usersListFragment.updateList(accept, position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        MeetingsFragment meetingsFragment = (MeetingsFragment) getFragmentManager().findFragmentByTag("homeFragment");
        if(meetingsFragment != null)
            meetingsFragment.onActivityResult(requestCode,resultCode,data);

        RollsListFragment rollsListFragment = (RollsListFragment) getFragmentManager().findFragmentByTag("rollsListFragment");
        if(rollsListFragment != null)
            rollsListFragment.onActivityResult(requestCode,resultCode,data);

        TasksFragment tasksFragment = (TasksFragment) getFragmentManager().findFragmentByTag("tasksFragment");
        if(tasksFragment != null)
            tasksFragment.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void updateList(int position) {
        MeetingsFragment meetingsFragment = (MeetingsFragment) getFragmentManager().findFragmentByTag("homeFragment");
        if(meetingsFragment != null)
            meetingsFragment.updateList(position);
    }
}