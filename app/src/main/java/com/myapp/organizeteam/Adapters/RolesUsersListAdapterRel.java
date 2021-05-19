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
public class RolesUsersListAdapterRel extends RecyclerView.Adapter<RolesUsersListAdapterRel.ExampleViewHolder> {
    private ArrayList<Role> mExampleList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter rolesAdapter;
    private RecycleViewClickListener listener;

    private Context mContext;

    public class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name;
        RecyclerView lv_users;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_rollName);
            lv_users = itemView.findViewById(R.id.lv_usersList);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(listener == null) return;
            listener.onClick(view,getAdapterPosition());
        }
    }

    public RolesUsersListAdapterRel(ArrayList<Role> exampleList, Context context, RecycleViewClickListener listener) {
        mExampleList = exampleList;
        mContext = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_roles_users_list, parent, false);
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

    public interface RecycleViewClickListener
    {
        void onClick(View v, int position);
    }
}
