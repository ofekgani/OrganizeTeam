package com.myapp.organizeteam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.R;

import java.util.ArrayList;

/**
 * Design item to team list.
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class RolesListAdapterRel extends RecyclerView.Adapter<RolesListAdapterRel.ExampleViewHolder> {
    private ArrayList<Role> mExampleList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter rolesAdapter;

    private Context mContext;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        RecyclerView lv_users;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_rollName);
            lv_users = itemView.findViewById(R.id.lv_usersList);
        }
    }

    public RolesListAdapterRel(ArrayList<Role> exampleList, Context context) {
        mExampleList = exampleList;
        mContext = context;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_roles_list, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ExampleViewHolder holder, final int position) {
        Role currentItem = mExampleList.get(position);

        String name = currentItem.getName ();
        holder.tv_name.setText ( name );

        DataExtraction dataExtraction = new DataExtraction();
        dataExtraction.getUsersByKeys(currentItem.getUsers(), new ISavable() {
            @Override
            public void onDataRead(Object save) {

                holder.lv_users.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(mContext);
                rolesAdapter = new UsersListAdapterRel((ArrayList<User>) save,null);
                holder.lv_users.setLayoutManager(mLayoutManager);
                holder.lv_users.setAdapter(rolesAdapter);

            }
        });

    }

    @Override
    public int getItemCount() {
        if(mExampleList.isEmpty())
            return 0;
        return mExampleList.size();
    }


}
