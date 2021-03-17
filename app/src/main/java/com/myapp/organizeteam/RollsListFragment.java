package com.myapp.organizeteam;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.organizeteam.Adapters.RolesListAdapterRel;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.Team;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class RollsListFragment extends Fragment{

    RecyclerView lv_roles;

    private RecyclerView.Adapter rolesAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Role> roles;
    Team team;

    Bundle args;
    LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rolls_list, container, false);
        this.inflater = inflater;

        lv_roles = v.findViewById(R.id.lv_roles);

        args = getArguments();
        roles = (ArrayList<Role>) args.getSerializable ( ConstantNames.ROLES_LIST );
        team = (Team) args.getSerializable ( ConstantNames.TEAM );

        setAdapter(roles);

        DatabaseReference rollDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.ROLE_PATH).child(team.getKeyID());
        rollDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(rolesAdapter != null)
                    rolesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return v;
    }

    private void setAdapter(ArrayList<Role> roles) {
        if(roles != null)
        {
            lv_roles.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(inflater.getContext(),LinearLayoutManager.VERTICAL, false);
            rolesAdapter = new RolesListAdapterRel(roles,inflater.getContext());
            lv_roles.setLayoutManager(mLayoutManager);
            lv_roles.setAdapter(rolesAdapter);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(data != null)
        {
            if(resultCode == RESULT_OK && requestCode == 999)
            {
                Role role = (Role) data.getSerializableExtra(ConstantNames.ROLE);
                roles.add(role);
                rolesAdapter.notifyDataSetChanged();
            }
        }
    }
}
