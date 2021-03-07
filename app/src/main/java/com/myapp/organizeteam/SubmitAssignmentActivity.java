package com.myapp.organizeteam;

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
import android.os.Environment;
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
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("*/*");
                startActivityForResult(fileIntent,190);
            }
        }
        else
        {
            Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            fileIntent.setType("*/*");
            startActivityForResult(fileIntent,190);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == 190)
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
                    Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    fileIntent.setType("*/*");
                    startActivityForResult(fileIntent,190);
                }
                else
                {
                    Toast.makeText (this,"Premission denied...!",Toast.LENGTH_LONG ).show ();
                }

                break;
        }
    }

    public void oc_submit(View view) {
        if(uriFile != null)
        {
            // Create a storage reference from our app
            FirebaseStorage storage = FirebaseStorage.getInstance ();
            StorageReference storageRef = storage.getReference();

            final Loading loading = new Loading ();
            final ProgressDialog pd = loading.getProgressDialog ( this,"Upload Image... ");

            final StorageReference riversRef = storageRef.child("files/tasks"+mission.getKeyID()+"/"+user.getKeyID());

            riversRef.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull final Task<Uri> task) {
                            final Submitter submitter = new Submitter(inputManagement.getInput(ed_title),inputManagement.getInput(ed_content),riversRef.getDownloadUrl().toString(),mission.getKeyID(),user.getKeyID());
                            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_ACTIVITY_PATH).child(team.getKeyID()).child(user.getKeyID()).child(ConstantNames.DATA_USER_TASKS);
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot ds : snapshot.getChildren())
                                    {
                                        if(ds.getValue().equals(mission.getKeyID()))
                                        {
                                            String keyID = ds.getKey();
                                            mDatabase.child(keyID).setValue(submitter);
                                            activityTransition.back(SubmitAssignmentActivity.this,null);
                                            pd.dismiss();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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
            final Submitter submitter = new Submitter(inputManagement.getInput(ed_title),inputManagement.getInput(ed_content),null,mission.getKeyID(),user.getKeyID());
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.USER_ACTIVITY_PATH).child(team.getKeyID()).child(user.getKeyID()).child(ConstantNames.DATA_USER_TASKS);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren())
                    {
                        if(ds.getValue().equals(mission.getKeyID()))
                        {
                            String keyID = ds.getKey();
                            mDatabase.child(keyID).setValue(submitter);
                            activityTransition.back(SubmitAssignmentActivity.this,null);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}