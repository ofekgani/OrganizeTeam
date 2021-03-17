package com.myapp.organizeteam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.R;
import com.myapp.organizeteam.Resources.FileManage;

import java.util.ArrayList;

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

    FileManage fileManage = new FileManage();

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
        String logo = getItem ( position ).getLogo ();

        LayoutInflater inflater = LayoutInflater.from ( mContext );
        convertView = inflater.inflate ( mResource,parent,false );

        TextView tv_name = convertView.findViewById ( R.id.tv_nameTeam );
        TextView tv_description = convertView.findViewById ( R.id.tv_descriptionTeam );
        ImageView mv_logo = convertView.findViewById ( R.id.mv_teamLogo1);

        //if to the team has logo, set it to image view resource.
        if( logo != null && !logo.equals ( "" ))
        {
            fileManage.setImageUri ( logo,mv_logo );
        }

        tv_name.setText ( name );
        tv_description.setText (description);

        return convertView;

    }
}
