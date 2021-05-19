package com.myapp.organizeteam.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Date;
import com.myapp.organizeteam.Core.Hour;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Submitter;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Design item to team list.
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class TasksListAdapter extends ArrayAdapter<Mission> {

    DataExtraction dataExtraction;

    private Context mContext;
    private int mResource;
    String userID;
    String status;

    public TasksListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Mission> objects, String userID) {
        super ( context, resource, objects );
        mContext = context;
        mResource = resource;
        this.userID = userID;
    }

    public TasksListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Mission> objects) {
        super ( context, resource, objects );
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from ( mContext );
        convertView = inflater.inflate ( mResource,parent,false );

        dataExtraction = new DataExtraction();

        TextView tv_name = convertView.findViewById ( R.id.tv_taskName);
        TextView tv_description = convertView.findViewById ( R.id.tv_taskDescription);
        TextView tv_date = convertView.findViewById ( R.id.tv_taskDate);
        final TextView tv_status = convertView.findViewById ( R.id.tv_taskStatus);

        String name = getItem ( position ).getTaskName ();
        String description = getItem ( position ).getTaskDescription ();
        Date date = getItem ( position ).getDate ();
        Hour hour = getItem ( position ).getHour ();

        final String teamID = getItem(position).getTeamID();
        final String taskID = getItem(position).getKeyID();

        tv_name.setText ( name );
        tv_description.setText (description);

        String meetingDate = date.getDay() + "/" + date.getMonth() + "/" + date.getYear() + " , " + hour.getHour() + ":" + hour.getMinute();
        tv_date.setText(meetingDate);
        if(userID != null)
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ConstantNames.TASK_PATH).child(teamID).child(taskID).child(ConstantNames.DATA_USERS_LIST).child(userID);
            dataExtraction.hasChild(databaseReference, ConstantNames.DATA_TASK_TITLE, new ISavable() {
                @Override
                public void onDataRead(Object save) {
                    if(!(boolean)save)
                    {
                        status = "Not submitted";
                    }
                    else
                    {
                        if(!getItem(position).isRequiredConfirm())
                        {
                            status = "Submitted";
                        }
                        else
                        {
                            dataExtraction.getSubmitter(ConstantNames.TASK_PATH,teamID, taskID, userID, new ISavable() {
                                @Override
                                public void onDataRead(Object save) {
                                    Submitter submitter = (Submitter) save;
                                    if(submitter.getConfirmStatus() == Submitter.STATUS_CONFIRM)
                                    {
                                        status = "Submitted";
                                    }
                                    else if(submitter.getConfirmStatus() == Submitter.STATUS_UNCONFIRMED)
                                    {
                                        status = "Returned";
                                    }
                                    else
                                    {
                                        status = "Waiting for confirm";
                                    }
                                    tv_status.setText(""+status);
                                }
                            });
                        }
                    }
                    tv_status.setText(""+status);
                }
            });
        }
        else
        {
            tv_status.setText("");
        }

        return convertView;
    }
}
