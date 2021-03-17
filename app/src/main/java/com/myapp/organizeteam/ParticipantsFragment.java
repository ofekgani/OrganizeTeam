package com.myapp.organizeteam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Fragment;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.myapp.organizeteam.Core.ConstantNames;

public class ParticipantsFragment extends Fragment{

    TabLayout tab;
    Bundle args,bundle;
    LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_participants, container, false);
        this.inflater = inflater;

        tab = v.findViewById(R.id.tabLayout);
        bundle = new Bundle();
        args = getArguments();
        bundle.putSerializable(ConstantNames.TEAM,args.getSerializable ( ConstantNames.TEAM ));
        bundle.putSerializable(ConstantNames.TEAM_HOST,args.getSerializable ( ConstantNames.TEAM_HOST ));
        bundle.putSerializable(ConstantNames.USERS_LIST, args.getSerializable ( ConstantNames.USERS_LIST ));
        bundle.putSerializable(ConstantNames.REQUESTS_LIST,args.getSerializable ( ConstantNames.REQUESTS_LIST ));

        UsersListFragment fragment = new UsersListFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_containerr, fragment,"usersListFragment").commit();

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0)
                {
                    UsersListFragment fragment = new UsersListFragment();
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containerr, fragment,"usersListFragment").commit();
                    Toast.makeText(inflater.getContext(),"tab 1 selected", Toast.LENGTH_SHORT).show();
                }
                if(tab.getPosition() == 1)
                {
                    bundle.putSerializable(ConstantNames.ROLES_LIST,args.getSerializable ( ConstantNames.ROLES_LIST ));
                    RollsListFragment fragment = new RollsListFragment();
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_containerr, fragment,"rollsListFragment").commit();
                    Toast.makeText(inflater.getContext(),"tab 2 selected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return v;
    }
}
