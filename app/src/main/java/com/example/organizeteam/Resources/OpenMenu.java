package com.example.organizeteam.Resources;

import com.example.organizeteam.R;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class OpenMenu extends AppCompatActivity {

    public void createMenu(final AppCompatActivity act, final DrawerLayout drawerLayout)
    {
        Toolbar toolbar = act.findViewById(R.id.toolbar);
        act.setSupportActionBar ( toolbar);
        act.getSupportActionBar().setTitle("");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle ( this,drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener ( toggle );
        toggle.syncState ();
    }

    public void setCheckedItem(NavigationView navigationView, int id )
    {
        navigationView.setCheckedItem(id);
    }

    public Object getResource(NavigationView navigationView,int id)
    {
        return navigationView.getHeaderView(0).findViewById(id);
    }

    public void closeMenu(DrawerLayout drawerLayout)
    {
        drawerLayout.closeDrawer ( GravityCompat.START );
    }

    public boolean isMenuOpen(DrawerLayout drawerLayout)
    {
        return drawerLayout.isDrawerOpen ( GravityCompat.START );
    }

    public void toggleMenu(DrawerLayout drawerLayout) {
        if(isMenuOpen ( drawerLayout ))
        {
            closeMenu ( drawerLayout );
        }
        else
        {
            super.onBackPressed ();
        }
    }
}
