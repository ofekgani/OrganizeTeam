package com.example.organizeteam.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.organizeteam.Core.ConstantNames;
import com.example.organizeteam.Core.Team;
import com.example.organizeteam.DataManagement.DataExtraction;
import com.example.organizeteam.DataManagement.ISavable;
import com.example.organizeteam.MyService.APIService;
import com.example.organizeteam.MyService.Data;
import com.example.organizeteam.MyService.Notification;
import com.example.organizeteam.MyService.Sender;
import com.example.organizeteam.R;
import com.example.organizeteam.SelectTeamActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestJoinDialog extends AppCompatDialogFragment{

    DataExtraction dataExtraction;

    private TextView tv_name, tv_description;

    private String userName,userID,hostToken;

    private Team team;

    private DialogListener listener;

    private final String CHANNEL_ID = "1";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate( R.layout.layout_dialog_join_requset, null);

        builder.setView(view)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Nothing
                    }
                })
                .setPositiveButton("Request to join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendNotification(view);
                        updateRequest(view);
                    }
                });

        dataExtraction = new DataExtraction();

        tv_name = view.findViewById(R.id.tv_teamNameDialog);
        tv_description = view.findViewById(R.id.tv_teamDescriptionDialog);

        Bundle bundle = getArguments ();

        userName = bundle.getString ( ConstantNames.USER_NAME );
        userID =  bundle.getString ( ConstantNames.USER_KEY_ID );
        hostToken =  bundle.getString ( ConstantNames.HOST_TOKEN );

        team = (Team) bundle.getSerializable(ConstantNames.TEAM);

        tv_name.setText ( ""+ team.getName());
        tv_description.setText ( ""+ team.getDescription());

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    /**
     * Update the join request in firebase
     */
    private void updateRequest(final View view) {
        if(team.getName() == null) return;

        //Check if the join request already to this team.
        dataExtraction.isValueExist(ConstantNames.USER_PATH, userID,ConstantNames.DATA_REQUEST_TO_JOIN, team.getKeyID(),new ISavable() {
            @Override
            public void onDataRead(Object save) {
                if(!(boolean)save)
                {
                    listener.applyRequest(team.getKeyID(),team.getName(),team.getLogo());

                    //Set to user the his join request at firebase
                    dataExtraction.setNewData(ConstantNames.USER_PATH,userID,ConstantNames.DATA_REQUEST_TO_JOIN,team.getKeyID());

                    //Add to team the id of the user at join request field at firebase.
                    dataExtraction.pushNewData(ConstantNames.TEAM_PATH,team.getKeyID(),ConstantNames.DATA_REQUEST_TO_JOIN,userID);
                }
                else
                {
                    Toast.makeText(view.getContext(),"This team already got join request.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendNotification(final View view) {
        APIService apiService = getAPIService(view);

        Data data = new Data (userID,userName + " want to join to " + tv_name.getText ()+ " .","Sent request",team.getHost());
        Sender sender = new Sender (data,hostToken);

        //Send notification to device
        apiService.sendNotification ( sender ).enqueue ( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(view.getContext(),""+response,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(view.getContext(),""+t,Toast.LENGTH_LONG).show();
            }
        } );
    }

    private APIService getAPIService(View view) {
        Notification notification = new Notification ();

        notification.createNotificationChannel (view.getContext (),CHANNEL_ID,"Join Request","Handle join request notifications",5);

        return notification.createClient ();
    }

    public interface DialogListener
    {
        void applyRequest(String teamID, String teamName,String teamLogo);
    }
}

