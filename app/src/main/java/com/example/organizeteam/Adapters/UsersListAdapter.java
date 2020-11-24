package com.example.organizeteam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.organizeteam.Core.Team;
import com.example.organizeteam.Core.User;
import com.example.organizeteam.R;
import com.example.organizeteam.Resources.Image;

import java.util.ArrayList;

/**
 * Design item to team list.
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class UsersListAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private int mResource;

    Image image = new Image ();

    public UsersListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super ( context, resource, objects );
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem ( position ).getFullName ();
        String logo = getItem ( position ).getLogo ();

        LayoutInflater inflater = LayoutInflater.from ( mContext );
        convertView = inflater.inflate ( mResource,parent,false );

        TextView tv_name = convertView.findViewById ( R.id.tv_userName );
        ImageView mv_logo = convertView.findViewById ( R.id.mv_userLogo);

        //if to the team has logo, set it to image view resource.
        if( logo != null && !logo.equals ( "" ))
        {
            image.setImageUri ( logo,mv_logo );
        }

        tv_name.setText ( name );

        return convertView;

    }
}
