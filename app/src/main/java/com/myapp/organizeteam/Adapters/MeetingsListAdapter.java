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
public class MeetingsListAdapter extends ArrayAdapter<Meeting> {

    private Context mContext;
    private int mResource;

    private MeetingsListAdapter.AdapterListener listener;

    public MeetingsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Meeting> objects) {
        super ( context, resource, objects );
        mContext = context;
        mResource = resource;
        try {
            listener = (MeetingsListAdapter.AdapterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MyInterface");
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from ( mContext );
        convertView = inflater.inflate ( mResource,parent,false );

        String name = getItem ( position ).getMeetingName ();
        String description = getItem ( position ).getMeetingDescription ();
        Date date = getItem ( position ).getDate ();
        Hour hour = getItem ( position ).getHour ();

        final String meetingID = getItem(position).getKeyID();
        final String teamID = getItem(position).getTeamID();

        TextView tv_name = convertView.findViewById ( R.id.tv_meetingName);
        TextView tv_description = convertView.findViewById ( R.id.tv_meetingDescription);
        TextView tv_date = convertView.findViewById ( R.id.tv_meetingDate);
        final TextView btn_endMeeting = convertView.findViewById ( R.id.tv_btn_endMeeting);

        tv_name.setText ( name );
        tv_description.setText (description);

        String meetingDate = date.getDay() + "/" + date.getMonth() + "/" + date.getYear() + " , " + hour.getHour() + ":" + hour.getMinute();
        tv_date.setText(meetingDate);

        btn_endMeeting.setVisibility(View.GONE);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference(ConstantNames.MEETINGS_PATH).child(teamID).child(meetingID).child(ConstantNames.DATA_MEETING_STATUS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                DataExtraction dataExtraction = new DataExtraction();
                dataExtraction.hasChild(ConstantNames.MEETINGS_PATH, teamID, meetingID, new ISavable() {
                    @Override
                    public void onDataRead(Object save) {
                        if((boolean)save)
                        {
                            int status = snapshot.getValue(Integer.class);
                            if(status == 0)
                            {
                                btn_endMeeting.setVisibility(View.GONE);
                            }
                            else if(status == 1 && isManager)
                            {
                                btn_endMeeting.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_endMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.updateList(position);
                DataExtraction dataExtraction = new DataExtraction();
                dataExtraction.deleteData(ConstantNames.MEETINGS_PATH, teamID, meetingID, new DataListener() {
                    @Override
                    public void onDataDelete() {

                    }
                });
            }
        });

        return convertView;
    }

    public interface AdapterListener
    {
        void updateList(int position);
    }
}
