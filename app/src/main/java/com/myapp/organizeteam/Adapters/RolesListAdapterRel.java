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

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_description;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_roleName);
            tv_description = itemView.findViewById(R.id.tv_roleDescription);
        }
    }

    public RolesListAdapterRel(ArrayList<Role> exampleList) {
        mExampleList = exampleList;
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
        String description = currentItem.getDescription ();
        holder.tv_name.setText ( name );
        holder.tv_description.setText ( description );

    }

    @Override
    public int getItemCount() {
        if(mExampleList.isEmpty())
            return 0;
        return mExampleList.size();
    }


}
