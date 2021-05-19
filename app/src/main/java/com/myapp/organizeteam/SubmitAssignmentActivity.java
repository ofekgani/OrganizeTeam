package com.myapp.organizeteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;
import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Submitter;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.Resources.FileManage;
import com.myapp.organizeteam.Resources.Loading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

public class SubmitAssignmentActivity extends AppCompatActivity {

    ActivityTransition activityTransition;
    InputManagement inputManagement;
    DataExtraction dataExtraction;
    FileManage fileManage;

    TextView tv_path;
    EditText ed_title, ed_content;

    Intent intent;
    User user;
    Mission mission;
    Team team;

    Uri uriFile;

    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_assignment);

        activityTransition = new ActivityTransition();
        inputManagement = new InputManagement();
        dataExtraction = new DataExtraction();
        fileManage = new FileManage();

        tv_path = findViewById(R.id.tv_filePath);

        ed_title = findViewById(R.id.ed_title);
        ed_content = findViewById(R.id.ed_content);

        intent = getIntent();
        user = (User) intent.getSerializableExtra(ConstantNames.USER);
        mission = (Mission) intent.getSerializableExtra(ConstantNames.TASK);
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
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

    public void oc_submit(View view) {
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

            final StorageReference riversRef = storageRef.child("files/tasks/"+taskID+"/"+userID);

            riversRef.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            int status;
                            if(mission.isRequiredConfirm())
                            {
                                status = Submitter.STATUS_WAITING;
                            }
                            else
                            {
                                status = Submitter.STATUS_CONFIRM;
                            }
                            final Submitter submitter = new Submitter(title,content,uri.toString(), fileManage.getFileName(uri), status, taskID,userID);

                            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.TASK_PATH)
                                    .child(team.getKeyID())
                                    .child(mission.getKeyID())
                                    .child(ConstantNames.DATA_USERS_LIST)
                                    .child(user.getKeyID());
                            mDatabase.setValue(submitter);

                            setUserStatus(status);

                            activityTransition.back(SubmitAssignmentActivity.this,null);
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
            int status;
            if(mission.isRequiredConfirm())
            {
                status = Submitter.STATUS_WAITING;
            }
            else
            {
                status = Submitter.STATUS_CONFIRM;
            }
            final Submitter submitter = new Submitter(title,content,null, null,status, taskID,userID);

            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.TASK_PATH)
                    .child(team.getKeyID())
                    .child(mission.getKeyID())
                    .child(ConstantNames.DATA_USERS_LIST)
                    .child(user.getKeyID());
            mDatabase.setValue(submitter);

            setUserStatus(status);

            activityTransition.back(SubmitAssignmentActivity.this,null);
        }
    }

    private void setUserStatus(int status) {
        if (status == Submitter.STATUS_CONFIRM) {
            DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_STATUSES_PATH)
                    .child(team.getKeyID())
                    .child(user.getKeyID())
                    .child(ConstantNames.TASK_PATH)
                    .child(ConstantNames.DATA_USER_STATUS_CONFIRM);
            userDatabase.child(mission.getKeyID()).setValue(mission.getKeyID());
        }
    }
}