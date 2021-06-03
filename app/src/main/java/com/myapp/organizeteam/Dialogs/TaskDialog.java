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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Date;
import com.myapp.organizeteam.Core.Hour;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Submitter;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.DataListener;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.R;
import com.myapp.organizeteam.Activities.SubmitAssignmentActivity;
import com.myapp.organizeteam.Activities.SubmitsListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskDialog extends AppCompatDialogFragment{

    DataExtraction dataExtraction;
    ActivityTransition activityTransition;

    private TextView tv_name, tv_description, tv_date;
    private Button btn_cancel, btn_submit, btn_submittersList, btn_closeTask;

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
        btn_closeTask = view.findViewById(R.id.btn_closeTask);

        Bundle bundle = getArguments ();
        user = (User)bundle.getSerializable(ConstantNames.USER);
        team = (Team) bundle.getSerializable(ConstantNames.TEAM);
        task = (Mission) bundle.getSerializable(ConstantNames.TASK);

        setUI();

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

        btn_closeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_cancel.setEnabled(false);
                final ArrayList<User> usersRejectsTask =  new ArrayList<>();
                dataExtraction.getUsersByConfirmations(ConstantNames.TASK_PATH, team.getKeyID(), task.getKeyID(), Submitter.STATUS_UNCONFIRMED, new ISavable() {
                    @Override
                    public void onDataRead(Object save) {
                        usersRejectsTask.addAll((ArrayList<User>) save);
                        dataExtraction.getUsersByConfirmations(ConstantNames.TASK_PATH, team.getKeyID(), task.getKeyID(), Submitter.STATUS_UNSUBMITTED, new ISavable() {
                            @Override
                            public void onDataRead(Object save) {
                                usersRejectsTask.addAll((ArrayList<User>) save);
                                dataExtraction.getUsersByConfirmations(ConstantNames.TASK_PATH, team.getKeyID(), task.getKeyID(), Submitter.STATUS_WAITING, new ISavable() {
                                    @Override
                                    public void onDataRead(final Object save) {
                                        dataExtraction.deleteData(ConstantNames.TASK_PATH, team.getKeyID(), task.getKeyID(), new DataListener() {
                                            @Override
                                            public void onDataDelete() {
                                                backupTask();
                                                setUserStatus(usersRejectsTask, ConstantNames.DATA_USER_STATUS_UNSUBMITTED);
                                                setUserStatus((ArrayList<User>) save, ConstantNames.DATA_USER_STATUS_SUBMITTED);
                                                ad.cancel();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        return ad;
    }

    private void setUI() {
        if(task.getStatus() == Mission.TIME_IS_UP)
        {
            btn_closeTask.setVisibility(View.VISIBLE);
        }
        else
        {
            btn_closeTask.setVisibility(View.GONE);
        }

        tv_name.setText ( ""+ task.getTaskName());
        tv_description.setText ( ""+ task.getTaskDescription());
        Date date = task.getDate ();
        Hour hour = task.getHour ();
        tv_date.setText ( date.getDay() + "/" + date.getMonth() + "/" + date.getYear() + " , " + hour.getHour() + ":" + hour.getMinute());
    }

    private void backupTask() {
        DatabaseReference meetingDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.TASKS_HISTORY_PATH)
                .child(team.getKeyID())
                .child(task.getKeyID());
        meetingDatabase.setValue(task);
    }

    private void setUserStatus(ArrayList<User> usersList, String status) {
        for (User user : usersList) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_STATUSES_PATH)
                    .child(team.getKeyID())
                    .child(user.getKeyID())
                    .child(ConstantNames.TASK_PATH)
                    .child(status);
            mDatabase.child(task.getKeyID()).setValue(task);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}

