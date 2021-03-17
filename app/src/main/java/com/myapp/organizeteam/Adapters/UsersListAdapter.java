package com.myapp.organizeteam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.R;
import com.myapp.organizeteam.Resources.FileManage;

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

    private UserSelectedListener listener;

    FileManage fileManage = new FileManage();

    public UsersListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super ( context, resource, objects );
        mContext = context;
        mResource = resource;
        try {
            listener = (UserSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MyInterface");
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        String name = getItem ( position ).getFullName ();
        String logo = getItem ( position ).getLogo ();

        final LayoutInflater inflater = LayoutInflater.from ( mContext );
        convertView = inflater.inflate ( mResource,parent,false );

        TextView tv_name = convertView.findViewById ( R.id.tv_userName);
        ImageView mv_logo = convertView.findViewById ( R.id.mv_userLogo);
        final CheckBox checkBox = convertView.findViewById(R.id.cb_createMeeting);

        //if to the team has logo, set it to image view resource.
        if( logo != null && !logo.equals ( "" ))
        {
            fileManage.setImageUri ( logo,mv_logo );
        }

        tv_name.setText ( name );

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.userSelected(getItem(position),checkBox.isChecked());
            }
        });

        return convertView;
    }

    public interface UserSelectedListener
    {
        void userSelected(User user, boolean check);
    }
}
