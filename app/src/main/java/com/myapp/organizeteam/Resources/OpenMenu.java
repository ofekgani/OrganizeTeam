package com.myapp.organizeteam.Resources;

import com.myapp.organizeteam.R;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class OpenMenu extends AppCompatActivity {

    int toolBar;

    public OpenMenu(int toolBar)
    {
        this.toolBar = toolBar;
    }

    public void createMenu(final AppCompatActivity act, final DrawerLayout drawerLayout)
    {
        Toolbar toolbar = act.findViewById(toolBar);
        act.setSupportActionBar ( toolbar);

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
}
