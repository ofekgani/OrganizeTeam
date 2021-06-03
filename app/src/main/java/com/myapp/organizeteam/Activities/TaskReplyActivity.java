package com.myapp.organizeteam.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Submitter;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.R;
import com.myapp.organizeteam.Resources.FileManage;
import com.myapp.organizeteam.Resources.Loading;

public class TaskReplyActivity extends AppCompatActivity {

    ActivityTransition activityTransition;
    InputManagement inputManagement;
    DataExtraction dataExtraction;
    FileManage fileManage;

    TextView tv_path;
    EditText ed_title, ed_content;
    CheckBox cb_returnToReform;

    Intent intent;

    User user;
    Submitter submitter;
    Mission mission;
    Team team;

    Uri uriFile;

    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_reply);

        activityTransition = new ActivityTransition();
        inputManagement = new InputManagement();
        dataExtraction = new DataExtraction();
        fileManage = new FileManage();

        tv_path = findViewById(R.id.tv_filePath);

        ed_title = findViewById(R.id.ed_title);
        ed_content = findViewById(R.id.ed_content);
        cb_returnToReform = findViewById(R.id.cb_returnToReform);

        intent = getIntent();
        user = (User) intent.getSerializableExtra(ConstantNames.USER);
        submitter = (Submitter) intent.getSerializableExtra(ConstantNames.SUBMITTER);
        mission = (Mission) intent.getSerializableExtra(ConstantNames.TASK);
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
    }

    public void oc_reply(View view) {

        final String title = inputManagement.getInput(ed_title);
        final String content = inputManagement.getInput(ed_content);

        final String taskID = mission.getKeyID();
        final String userID = user.getKeyID();

        if(uriFile != null)
        {

            // Create a storage reference from our app
            FirebaseStorage storage = FirebaseStorage.getInstance ();
            StorageReference storageRef = storage.getReference();

            final Loading loading = new Loading ();
            final ProgressDialog pd = loading.getProgressDialog ( this,"Upload File... ");

            final StorageReference riversRef = storageRef.child("files/responses/"+taskID+"/"+userID);

            riversRef.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String fileName = fileManage.getFileName(uriFile);

                            saveReply(title, content, taskID, userID, fileName, uri.toString());

                            activityTransition.back(TaskReplyActivity.this);

                            pd.dismiss();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    pd.dismiss ();
                }
            }).addOnProgressListener ( new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    loading.calculatePercent ( taskSnapshot, pd );
                }
            });
        }
        else
        {
            saveReply(title, content, taskID, userID,null, null);

            activityTransition.back(TaskReplyActivity.this);
        }
    }

    private void saveReply(String title, String content, String taskID, String userID,String fileName, String uriPath) {
        final Submitter reply = new Submitter(title, content, uriPath, fileName, Submitter.STATUS_WAITING, taskID, userID);
        setStatusSubmit(taskID);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.TASK_PATH)
                .child(team.getKeyID())
                .child(taskID)
                .child(ConstantNames.DATA_USERS_LIST)
                .child(submitter.getUserID())
                .child(ConstantNames.DATA_TASK_REPLIES)
                .child(userID);
        mDatabase.setValue(reply);
    }

    private void setStatusSubmit(String taskID) {
        DatabaseReference submitterDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.TASK_PATH)
                .child(team.getKeyID()).child(taskID)
                .child(ConstantNames.DATA_USERS_LIST)
                .child(submitter.getUserID())
                .child(ConstantNames.DATA_TASK_CONFIRM);
        if (cb_returnToReform.isChecked()) {
            submitterDatabase.setValue(Submitter.STATUS_UNCONFIRMED);
        } else {
            submitterDatabase.setValue(Submitter.STATUS_CONFIRM);

            DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_STATUSES_PATH)
                    .child(team.getKeyID())
                    .child(user.getKeyID())
                    .child(ConstantNames.TASK_PATH)
                    .child(ConstantNames.DATA_USER_STATUS_CONFIRM);
            userDatabase.child(mission.getKeyID()).setValue(mission.getKeyID());
        }
    }

    public void oc_getPath(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions ( permissions,PERMISSION_CODE );
            }
            else
            {
                fileManage.pickFile(this);
            }
        }
        else
        {
            fileManage.pickFile(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == fileManage.FILE_PICK_CODE)
        {
            uriFile = data.getData();
            tv_path.setText(uriFile.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );

        switch (requestCode)
        {
            case PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    fileManage.pickFile(this);
                }
                else
                {
                    Toast.makeText (this,"Premission denied...!",Toast.LENGTH_LONG ).show ();
                }
                break;
        }
    }
}