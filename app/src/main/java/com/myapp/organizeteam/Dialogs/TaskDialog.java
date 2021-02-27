package com.myapp.organizeteam.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Date;
import com.myapp.organizeteam.Core.Hour;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.MyService.APIService;
import com.myapp.organizeteam.MyService.Data;
import com.myapp.organizeteam.MyService.Notification;
import com.myapp.organizeteam.MyService.Sender;
import com.myapp.organizeteam.R;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDialog extends AppCompatDialogFragment{

    DataExtraction dataExtraction;

    private TextView tv_name, tv_description, tv_date;


    private Mission task;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate( R.layout.layout_dialog_task, null);

        builder.setView(view);

        dataExtraction = new DataExtraction();

        tv_name = view.findViewById(R.id.tv_taskNameDialog);
        tv_description = view.findViewById(R.id.tv_taskDescriptionDialog);
        tv_date = view.findViewById(R.id.tv_taskDateDialog);

        Bundle bundle = getArguments ();

        task = (Mission) bundle.getSerializable(ConstantNames.TASK);

        tv_name.setText ( ""+ task.getTaskName());
        tv_description.setText ( ""+ task.getTaskDescription());

        Date date = task.getDate ();
        Hour hour = task.getHour ();
        tv_date.setText ( date.getDay() + "/" + date.getMonth() + "/" + date.getYear() + " , " + hour.getHour() + ":" + hour.getMinute());

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}

