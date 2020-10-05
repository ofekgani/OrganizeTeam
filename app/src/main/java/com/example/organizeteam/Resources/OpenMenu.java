package com.example.organizeteam.Resources;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;

import com.example.organizeteam.AuthorizationSystem.Authorization;
import com.example.organizeteam.R;
import com.example.organizeteam.TeamListActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class OpenMenu extends AppCompatActivity {

    Authorization authorization = new Authorization ();

    public void createMenu(final AppCompatActivity act, final DrawerLayout drawerLayout, NavigationView navigationView)
    {
        Toolbar toolbar = act.findViewById(R.id.toolbar);
        act.setSupportActionBar ( toolbar);
        act.getSupportActionBar().setTitle("");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle ( this,drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener ( toggle );
        toggle.syncState ();

        navigationView.setNavigationItemSelectedListener ( new NavigationView.OnNavigationItemSelectedListener () {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId ())
                {
                    case R.id.btn_signOut:
                        authorization.singOut ( act );
                        break;
                }
                drawerLayout.closeDrawer ( GravityCompat.START );
                return true;
            }
        } );
    }

    public View getHeaderView(NavigationView navigationView) {
        return navigationView.getHeaderView(0);
    }
}
