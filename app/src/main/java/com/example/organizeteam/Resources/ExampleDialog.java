package com.example.organizeteam.Resources;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.organizeteam.Core.ConstantNames;
import com.example.organizeteam.Core.Team;
import com.example.organizeteam.MainActivity;
import com.example.organizeteam.MyService.APIService;
import com.example.organizeteam.MyService.Client;
import com.example.organizeteam.MyService.Data;
import com.example.organizeteam.MyService.Notification;
import com.example.organizeteam.MyService.Sender;
import com.example.organizeteam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExampleDialog extends AppCompatDialogFragment{

    private TextView tv_name;
    private TextView tv_description;

    private String userName,userID,hostID,hostToken;

    private final String CHANNEL_ID = "1";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate( R.layout.layout_dialog_joint_requset, null);

        builder.setView(view)
                .setTitle("Join to team")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Nothing
                    }
                })
                .setPositiveButton("Request to join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Notification notification = new Notification ();

                        notification.createNotificationChannel (view.getContext (),CHANNEL_ID,"Join Request","Handle join request notifications",4);

                        APIService apiService = notification.createClient ();

                        Data data = new Data (userID,userName + " want to join to " + tv_name.getText ()+ " .","Sent request",hostID);
                        Sender sender = new Sender (data,hostToken);

                        //Send notification to device
                        apiService.sendNotification ( sender ).enqueue ( new Callback<ResponseBody> () {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        } );
                    }
                });

        tv_name = view.findViewById(R.id.tv_teamNameDialog);
        tv_description = view.findViewById(R.id.tv_teamDescriptionDialog);

        Bundle bundle = getArguments ();
        tv_name.setText ( ""+bundle.getString ( ConstantNames.TEAM_NAME ) );
        tv_description.setText ( ""+bundle.getString ( ConstantNames.TEAM_DESCRIPTION ) );
        userName =  bundle.getString ( ConstantNames.USER_NAME );
        userID =  bundle.getString ( ConstantNames.USER_KEY_ID );
        hostID =  bundle.getString ( ConstantNames.HOST_ID );
        hostToken =  bundle.getString ( ConstantNames.HOST_TOKEN );
        tv_description.setMovementMethod(new ScrollingMovementMethod ());
        return builder.create();
    }
}

