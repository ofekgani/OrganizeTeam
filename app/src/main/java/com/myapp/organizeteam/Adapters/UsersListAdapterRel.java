package com.myapp.organizeteam.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.R;
import com.myapp.organizeteam.Resources.Image;

import java.util.ArrayList;

/**
 * Design item to team list.
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class UsersListAdapterRel extends RecyclerView.Adapter<UsersListAdapterRel.ExampleViewHolder> {
    private ArrayList<User> mExampleList;

    Image image = new Image ();

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        ImageView mv_logo;
        CheckBox checkBox;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_userName);
            mv_logo = itemView.findViewById(R.id.mv_userLogo);
            checkBox = itemView.findViewById(R.id.cb_createMeeting);
        }
    }

    public UsersListAdapterRel(ArrayList<User> exampleList) {
        mExampleList = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_users_list, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, final int position) {
        holder.checkBox.setVisibility(View.INVISIBLE);
        User currentItem = mExampleList.get(position);

        String name = currentItem.getFullName ();
        String logo = currentItem.getLogo ();

        //if to the team has logo, set it to image view resource.
        if( logo != null && !logo.equals ( "" ))
        {
            image.setImageUri ( logo,holder.mv_logo );
        }

        holder.tv_name.setText ( name );
    }

    @Override
    public int getItemCount() {
        if(mExampleList.isEmpty())
            return 0;
        return mExampleList.size();
    }
}
