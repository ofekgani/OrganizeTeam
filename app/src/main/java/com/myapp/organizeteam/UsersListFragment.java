package com.myapp.organizeteam;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.organizeteam.Adapters.UsersListAdapterRel;
import com.myapp.organizeteam.Adapters.UsersRequestsListAdapterRel;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.Resources.FileManage;

import java.util.ArrayList;

import static com.myapp.organizeteam.DataManagement.Authorization.isManager;

public class UsersListFragment extends Fragment{

    FileManage fileManage;

    TextView tv_managerName;
    ImageView mv_managerLogo;

    RecyclerView lv_users;
    RecyclerView lv_requests;
    private RecyclerView.Adapter usersAdapter,requestsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    User manager;
    Team team;

    ArrayList<User> requestsList;
    ArrayList<User> usersList;

    Bundle args;
    LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_list, container, false);
        this.inflater = inflater;

        fileManage = new FileManage();

        tv_managerName = v.findViewById ( R.id.tv_userName);
        mv_managerLogo = v.findViewById ( R.id.mv_userLogo);
        lv_users = v.findViewById(R.id.lv_users);
        lv_requests = v.findViewById(R.id.lv_requests);

        args = getArguments();
        team = (Team) args.getSerializable ( ConstantNames.TEAM );
        manager = (User)args.getSerializable ( ConstantNames.TEAM_HOST );
        usersList = (ArrayList<User>) args.getSerializable ( ConstantNames.USERS_LIST );
        requestsList = (ArrayList<User>) args.getSerializable ( ConstantNames.REQUESTS_LIST );

        //Get Manager data.
        updateManagerUI();

        setAdapter(usersList,requestsList);

        return v;
    }


    private void updateManagerUI() {
        tv_managerName.setText ( ""+manager.getFullName() );
        fileManage.setImageUri (manager.getLogo(), mv_managerLogo );
    }

    private void setAdapter(ArrayList<User> users,ArrayList<User> requests) {
        if(usersList != null)
        {
            lv_users.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(inflater.getContext(),LinearLayoutManager.VERTICAL, false);
            usersAdapter = new UsersListAdapterRel(users,null);
            lv_users.setLayoutManager(mLayoutManager);
            lv_users.setAdapter(usersAdapter);
        }

        if(requestsList != null && isManager)
        {
            lv_requests.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(inflater.getContext(),LinearLayoutManager.VERTICAL, false);
            requestsAdapter = new UsersRequestsListAdapterRel(requests,team.getKeyID(),inflater.getContext());
            lv_requests.setLayoutManager(mLayoutManager2);
            lv_requests.setAdapter(requestsAdapter);
        }
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
            usersList.add(user);
        }

        usersAdapter.notifyDataSetChanged();
        requestsAdapter.notifyDataSetChanged();
    }
}
