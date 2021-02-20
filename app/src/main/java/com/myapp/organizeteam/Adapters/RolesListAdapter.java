package com.myapp.organizeteam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.R;

import java.util.ArrayList;

/**
 * Design item to team list.
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class RolesListAdapter extends ArrayAdapter<Role> {

    private Context mContext;
    private int mResource;

    private RoleSelectedListener listener;

    public RolesListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Role> objects) {
        super ( context, resource, objects );
        mContext = context;
        mResource = resource;
        try {
            listener = (RoleSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MyInterface");
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        String name = getItem ( position ).getName ();
        String description = getItem ( position ).getDescription ();

        final LayoutInflater inflater = LayoutInflater.from ( mContext );
        convertView = inflater.inflate ( mResource,parent,false );

        TextView tv_name = convertView.findViewById ( R.id.tv_userName);
        TextView tv_description = convertView.findViewById ( R.id.tv_roleDescription);
        final CheckBox checkBox = convertView.findViewById(R.id.checkBox);

        tv_name.setText ( name );
        tv_description.setText ( description );

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.roleSelected(getItem(position),checkBox.isChecked());
            }
        });

        return convertView;
    }

    public interface RoleSelectedListener
    {
        void roleSelected(Role role, boolean check);
    }
}
