package com.myapp.organizeteam.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Date;
import com.myapp.organizeteam.Core.Hour;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.R;
import com.myapp.organizeteam.SubmitAssignmentActivity;
import com.myapp.organizeteam.SubmitsListActivity;

import java.util.HashMap;
import java.util.Map;

public class TaskDialog extends AppCompatDialogFragment{

    DataExtraction dataExtraction;
    ActivityTransition activityTransition;

    private TextView tv_name, tv_description, tv_date;
    private Button btn_cancel, btn_submit, btn_submittersList, btn_responses;

    private Mission task;
    private Team team;
    private User user;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate( R.layout.layout_dialog_task, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view);
        final AlertDialog ad = builder.create();

        dataExtraction = new DataExtraction();
        activityTransition = new ActivityTransition();

        tv_name = view.findViewById(R.id.tv_taskNameDialog);
        tv_description = view.findViewById(R.id.tv_taskDescriptionDialog);
        tv_date = view.findViewById(R.id.tv_taskDateDialog);

        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_submit = view.findViewById(R.id.btn_SubmitAssignment);
        btn_submittersList = view.findViewById(R.id.btn_ShowSubmits);
        btn_responses = view.findViewById(R.id.btn_ShowResponses);

        Bundle bundle = getArguments ();
        user = (User)bundle.getSerializable(ConstantNames.USER);
        team = (Team) bundle.getSerializable(ConstantNames.TEAM);
        task = (Mission) bundle.getSerializable(ConstantNames.TASK);


        tv_name.setText ( ""+ task.getTaskName());
        tv_description.setText ( ""+ task.getTaskDescription());

        Date date = task.getDate ();
        Hour hour = task.getHour ();
        tv_date.setText ( date.getDay() + "/" + date.getMonth() + "/" + date.getYear() + " , " + hour.getHour() + ":" + hour.getMinute());

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.cancel();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> save = new HashMap<>();
                save.put(ConstantNames.USER,user);
                save.put(ConstantNames.TASK,task);
                save.put(ConstantNames.TEAM,team);
                activityTransition.goToWithResult(getActivity(), SubmitAssignmentActivity.class,121,save,null);
            }
        });

        btn_submittersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> save = new HashMap<>();
                save.put(ConstantNames.USER,user);
                save.put(ConstantNames.TASK,task);
                save.put(ConstantNames.TEAM,team);
                activityTransition.goTo(getActivity(), SubmitsListActivity.class,false,save,null);
            }
        });

        btn_responses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return ad;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}

