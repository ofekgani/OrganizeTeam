package com.myapp.organizeteam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.DataListener;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Dialogs.RequestJoinDialog;
import com.myapp.organizeteam.R;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.TeamPageActivity;

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
    private String teamID;

    ArrayList<User> users;

    private DataExtraction dataExtraction;

    private AdapterListener listener;

    Image image = new Image ();

    public UsersListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects,String teamID) {
        super ( context, resource, objects );
        mContext = context;
        mResource = resource;
        try {
            listener = (AdapterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MyInterface");
        }
        this.teamID = teamID;
        users = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        String name = getItem ( position ).getFullName ();
        String logo = getItem ( position ).getLogo ();

        final String userID = getItem(position).getKeyID();

        final LayoutInflater inflater = LayoutInflater.from ( mContext );
        convertView = inflater.inflate ( mResource,parent,false );

        TextView tv_name = convertView.findViewById ( R.id.tv_userName );
        ImageView mv_logo = convertView.findViewById ( R.id.mv_userLogo);
        final ImageButton btn_confirm = convertView.findViewById(R.id.mb_confirm);
        final ImageButton btn_dismiss = convertView.findViewById(R.id.mb_dismiss);

        dataExtraction = new DataExtraction();
        dataExtraction.hasChild(ConstantNames.USER_PATH, userID, ConstantNames.DATA_REQUEST_TO_JOIN, new ISavable() {
            @Override
            public void onDataRead(Object save) {
                if(!(boolean)save)
                {
                    btn_confirm.setVisibility(View.GONE);
                    btn_dismiss.setVisibility(View.GONE);
                }
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptJoinRequest(userID);
                removeJoinRequest(userID,true,position);
            }
        });

        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeJoinRequest(userID,false,position);
            }
        });

        //if to the team has logo, set it to image view resource.
        if( logo != null && !logo.equals ( "" ))
        {
            image.setImageUri ( logo,mv_logo );
        }

        tv_name.setText ( name );

        return convertView;

    }

    private void acceptJoinRequest(String userID) {
        dataExtraction.pushNewData(ConstantNames.TEAM_PATH,teamID,ConstantNames.DATA_USERS_AT_TEAM,userID);
        dataExtraction.setNewData(ConstantNames.USER_PATH,userID,ConstantNames.DATA_USER_TEAM,teamID);
    }

    private void removeJoinRequest(final String userID,final boolean accept, final int position) {
        dataExtraction.deleteValue(ConstantNames.TEAM_PATH, teamID, ConstantNames.DATA_REQUEST_TO_JOIN, userID, new DataListener() {
            @Override
            public void onDataDelete() {
                dataExtraction.deleteData(ConstantNames.USER_PATH, userID, ConstantNames.DATA_REQUEST_TO_JOIN, new DataListener() {
                    @Override
                    public void onDataDelete() {
                        listener.updateList(accept,position);
                    }
                });
            }
        });

    }

    public interface AdapterListener
    {
        void updateList(boolean accept, int position);
    }
}
