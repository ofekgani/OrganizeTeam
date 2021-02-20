package com.myapp.organizeteam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.DataListener;
import com.myapp.organizeteam.R;
import com.myapp.organizeteam.Resources.Image;

import java.util.ArrayList;

/**
 * Design item to team list.
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class UsersRequestsListAdapterRel extends RecyclerView.Adapter<UsersRequestsListAdapterRel.ExampleViewHolder> {
    private ArrayList<User> mExampleList;
    private String teamID;

    private DataExtraction dataExtraction;

    private AdapterListener listener;

    Image image = new Image ();

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        ImageView mv_logo;
        ImageButton btn_confirm;
        ImageButton btn_dismiss;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_userName);
            mv_logo = itemView.findViewById(R.id.mv_userLogo);
            btn_confirm = itemView.findViewById(R.id.mb_confirm);
            btn_dismiss = itemView.findViewById(R.id.mb_dismiss);
        }
    }

    public UsersRequestsListAdapterRel(ArrayList<User> exampleList,String teamID,Context context) {
        mExampleList = exampleList;
        try {
            listener = (AdapterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MyInterface");
        }
        this.teamID = teamID;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_users_request_list, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ExampleViewHolder holder, final int position) {
        User currentItem = mExampleList.get(position);

        String name = currentItem.getFullName ();
        String logo = currentItem.getLogo ();

        final String userID = currentItem.getKeyID();

        dataExtraction = new DataExtraction();

        holder.btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btn_confirm.setEnabled(false);
                holder.btn_dismiss.setEnabled(false);
                acceptJoinRequest(userID);
                removeJoinRequest(userID,true,position);
            }
        });

        holder.btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btn_confirm.setEnabled(false);
                holder.btn_dismiss.setEnabled(false);
                removeJoinRequest(userID,false,position);
            }
        });

        //if to the team has logo, set it to image view resource.
        if( logo != null && !logo.equals ( "" ))
        {
            image.setImageUri ( logo,holder.mv_logo );
        }

        holder.tv_name.setText ( name );
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
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
