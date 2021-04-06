package com.myapp.organizeteam.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Date;
import com.myapp.organizeteam.Core.Hour;
import com.myapp.organizeteam.Core.Meeting;
import com.myapp.organizeteam.Core.Post;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.DataListener;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.R;

import java.util.ArrayList;

import static com.myapp.organizeteam.DataManagement.Authorization.isManager;

/**
 * Design item to team list.
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class PostsListAdapter extends ArrayAdapter<Post> {

    private Context mContext;
    private int mResource;

    private PostsListAdapter.AdapterListener listener;

    public PostsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Post> objects) {
        super ( context, resource, objects );
        mContext = context;
        mResource = resource;
        try {
            listener = (PostsListAdapter.AdapterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MyInterface");
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from ( mContext );
        convertView = inflater.inflate ( mResource,parent,false );

        String name = getItem ( position ).getPostName ();
        String description = getItem ( position ).getPostContent ();
        Date date = getItem ( position ).getDate ();
        Hour hour = getItem ( position ).getHour ();

        TextView tv_name = convertView.findViewById ( R.id.tv_postName);
        TextView tv_description = convertView.findViewById ( R.id.tv_postDescription);
        TextView tv_date = convertView.findViewById ( R.id.tv_postDate);

        tv_name.setText ( name );
        tv_description.setText (description);

        String meetingDate = date.getDay() + "/" + date.getMonth() + "/" + date.getYear() + " , " + hour.getHour() + ":" + hour.getMinute();
        tv_date.setText(meetingDate);

        return convertView;
    }

    public interface AdapterListener
    {
        void updateList(int position);
    }
}
