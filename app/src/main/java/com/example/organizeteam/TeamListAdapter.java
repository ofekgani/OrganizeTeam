package com.example.organizeteam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Design item to team list.
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class TeamListAdapter extends ArrayAdapter<Team> {

    private Context mContext;
    private int mResource;

    public TeamListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Team> objects) {
        super ( context, resource, objects );
        mContext = context;
        mResource = resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem ( position ).getName ();
        String description = getItem ( position ).getDescription ();

        Team team = new Team ( name,description );

        LayoutInflater inflater = LayoutInflater.from ( mContext );
        convertView = inflater.inflate ( mResource,parent,false );

        TextView tv_name = (TextView)convertView.findViewById ( R.id.tv_nameTeam );
        TextView tv_description = (TextView)convertView.findViewById ( R.id.tv_descriptionTeam );

        tv_name.setText ( name );
        tv_description.setText (description);

        return convertView;

    }
}
