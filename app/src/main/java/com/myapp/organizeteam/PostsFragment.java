package com.myapp.organizeteam;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myapp.organizeteam.Adapters.MeetingsListAdapter;
import com.myapp.organizeteam.Adapters.PostsListAdapter;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Meeting;
import com.myapp.organizeteam.Core.Post;
import com.myapp.organizeteam.Core.Role;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.myapp.organizeteam.DataManagement.Authorization.isManager;

public class PostsFragment extends Fragment{

    DataExtraction dataExtraction;

    ListView lv_postList;
    PostsListAdapter adapter;
    Bundle bundle;

    ArrayList<Post> postsList;
    ArrayList<Role> rolesList;
    LayoutInflater inflater;

    Team team;
    User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_posts, container, false);
        this.inflater = inflater;

        dataExtraction = new DataExtraction();

        lv_postList = v.findViewById(R.id.lv_posts);

        bundle = getArguments();
        team = (Team) bundle.getSerializable ( ConstantNames.TEAM );
        user = (User) bundle.getSerializable ( ConstantNames.USER );
        rolesList = (ArrayList<Role>) bundle.getSerializable ( ConstantNames.USER_ROLES );

        createMeetingsList();

        return v;
    }

    private void createMeetingsList()
    {
        if(isManager)
        {
            dataExtraction.getAllPostsByTeam(team.getKeyID() ,new ISavable() {
                @Override
                public void onDataRead(Object save) {
                    postsList = (ArrayList<Post>)save;
                    setAdapter();
                }
            });
        }
        else
        {
            dataExtraction.getPostsByUser(user.getKeyID(), team.getKeyID(), new ISavable() {
                @Override
                public void onDataRead(Object save) {
                    ArrayList<String> postsID = (ArrayList<String>) save;
                    dataExtraction.getPostsByID(postsID,team.getKeyID(), user.getKeyID() ,new ISavable() {
                        @Override
                        public void onDataRead(Object save) {
                            postsList = (ArrayList<Post>)save;
                            setAdapter();
                        }
                    });
                }
            });

        }

    }

    private void setAdapter() {
        adapter = new PostsListAdapter(inflater.getContext(),R.layout.adapter_posts_list, postsList);
        lv_postList.setAdapter ( adapter );
        adapter.notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(data != null)
        {
            if(resultCode == RESULT_OK && requestCode == 976)
            {
                Post post = (Post) data.getSerializableExtra(ConstantNames.POST);
                postsList.add(post);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void updateList(int position) {
        postsList.remove(position);
        adapter.notifyDataSetChanged();
    }
}
