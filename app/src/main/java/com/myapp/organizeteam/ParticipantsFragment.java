package com.myapp.organizeteam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;

import com.myapp.organizeteam.Adapters.UsersRequestsListAdapter;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.Image;

import java.util.ArrayList;

public class ParticipantsFragment extends Fragment{

    Image image;

    TextView tv_managerName;
    ImageView mv_managerLogo;

    ListView lv_users;
    UsersRequestsListAdapter adapter;

    User manager;
    Team team;

    ArrayList<User> requestsList;
    ArrayList<User> usersList;

    Bundle args;
    LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_participants, container, false);
        this.inflater = inflater;

        image = new Image ();

        tv_managerName = v.findViewById ( R.id.tv_userName);
        mv_managerLogo = v.findViewById ( R.id.mv_userLogo);
        lv_users = v.findViewById(R.id.lv_users);

        args = getArguments();
        team = (Team) args.getSerializable ( ConstantNames.TEAM );
        manager = (User)args.getSerializable ( ConstantNames.TEAM_HOST );

        //Get Manager data.
        updateManagerUI();

        if(isManager())
        {
            createUsersList();
        }

        return v;
    }

        private boolean isManager() {
        User user = (User)args.getSerializable(ConstantNames.USER);
        if(manager.getKeyID().equals(user.getKeyID()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void updateManagerUI() {
        tv_managerName.setText ( ""+manager.getFullName() );
        image.setImageUri (manager.getLogo(), mv_managerLogo );
    }

    private void createUsersList()
    {
        final DataExtraction dataExtraction = new DataExtraction();
        dataExtraction.getAllUsersByTeam(team.getKeyID(), ConstantNames.DATA_REQUEST_TO_JOIN,new ISavable() {
            @Override
            public void onDataRead(Object save) {
                requestsList = (ArrayList<User>)save;
                dataExtraction.getAllUsersByTeam(team.getKeyID(), ConstantNames.DATA_USERS_AT_TEAM, new ISavable() {
                            @Override
                            public void onDataRead(Object save) {
                                usersList = (ArrayList<User>)save;
                                requestsList.addAll(usersList);
                                requestsList.addAll(usersList);
                                setAdapter(requestsList);
                            }
                        });

            }
        });
    }

    private void setAdapter(ArrayList<User> users) {
        adapter = new UsersRequestsListAdapter(inflater.getContext(),R.layout.adapter_users_request_list,users,team.getKeyID());
        lv_users.setAdapter ( adapter );
    }


    /**
     * called any time the users list has change.
     * @param position The item from the list to delete.
     */
    public void updateList(boolean accept, int position) {
        if(requestsList == null) return;
        User user = requestsList.get(position);
        requestsList.remove(position);
        if(accept)
        {
            requestsList.add(user);
        }

        adapter.notifyDataSetChanged();
    }
}
