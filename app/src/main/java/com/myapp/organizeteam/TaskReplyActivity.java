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
import android.provider.OpenableColumns;
import android.view.View;
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
import com.myapp.organizeteam.Resources.Loading;

public class TaskReplyActivity extends AppCompatActivity {

    ActivityTransition activityTransition;
    InputManagement inputManagement;
    DataExtraction dataExtraction;

    TextView tv_path;
    EditText ed_title, ed_content;

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

        tv_path = findViewById(R.id.tv_filePath);

        ed_title = findViewById(R.id.ed_title);
        ed_content = findViewById(R.id.ed_content);

        intent = getIntent();
        user = (User) intent.getSerializableExtra(ConstantNames.USER);
        submitter = (Submitter) intent.getSerializableExtra(ConstantNames.SUBMITTER);
        mission = (Mission) intent.getSerializableExtra(ConstantNames.TASK);
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void oc_reply(View view) {
        if(uriFile != null)
        {
            // Create a storage reference from our app
            FirebaseStorage storage = FirebaseStorage.getInstance ();
            StorageReference storageRef = storage.getReference();

            final Loading loading = new Loading ();
            final ProgressDialog pd = loading.getProgressDialog ( this,"Upload File... ");

            final StorageReference riversRef = storageRef.child("files/responses/"+mission.getKeyID()+"/"+user.getKeyID());

            riversRef.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final Submitter reply = new Submitter(inputManagement.getInput(ed_title),inputManagement.getInput(ed_content),uri.toString(), getFileName(uriFile), mission.getKeyID(),user.getKeyID());
                            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.TASK_PATH).child(team.getKeyID()).child(mission.getKeyID()).child(ConstantNames.DATA_TASK_USER);
                            mDatabase.child(submitter.getUserID()).child(ConstantNames.DATA_TASK_REPLIES).child(user.getKeyID()).setValue(reply);
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
            final Submitter reply = new Submitter(inputManagement.getInput(ed_title),inputManagement.getInput(ed_content),null, null, mission.getKeyID(),user.getKeyID());
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(ConstantNames.TASK_PATH).child(team.getKeyID()).child(mission.getKeyID()).child(ConstantNames.DATA_TASK_USER);
            mDatabase.child(submitter.getUserID()).child(ConstantNames.DATA_TASK_REPLIES).child(user.getKeyID()).setValue(reply);
            activityTransition.back(TaskReplyActivity.this);
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
}