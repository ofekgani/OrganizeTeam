package com.myapp.organizeteam.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.R;
import com.myapp.organizeteam.Resources.FileManage;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    ActivityTransition activityTransition;
    FileManage fileManage;
    InputManagement inputManagement;
    DataExtraction dataExtraction;

    EditText ed_name;
    ImageView mv_logo;

    Intent intent;
    User user;

    String imageUri;

    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        activityTransition = new ActivityTransition();
        fileManage = new FileManage();
        inputManagement = new InputManagement();
        dataExtraction = new DataExtraction();

        ed_name = findViewById(R.id.ed_name);
        mv_logo = findViewById(R.id.mv_userLogo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        intent = getIntent();
        user = (User) intent.getSerializableExtra(ConstantNames.USER);

        imageUri = user.getLogo();

        ed_name.setText(""+user.getFullName());
        fileManage.setImageUri(imageUri,mv_logo);

        mv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission ( Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_DENIED)
                    {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions ( permissions,PERMISSION_CODE );
                    }
                    else
                    {
                        fileManage.pickImageFromGallery(SettingsActivity.this);
                    }
                }
                else
                {
                    fileManage.pickImageFromGallery(SettingsActivity.this);
                }
            }
        });
    }

    public void oc_confirm(View view) {
        if(inputManagement.isInputEmpty(ed_name)) return;

        user.setFullName(inputManagement.getInput(ed_name));
        user.setLogo(imageUri);

        dataExtraction.setNewData(ConstantNames.USER_PATH,user.getKeyID(),ConstantNames.DATA_USER_NAME,user.getFullName());
        dataExtraction.setNewData(ConstantNames.USER_PATH,user.getKeyID(),ConstantNames.DATA_USER_LOGO,user.getLogo());

        Map<String, Object> save = new HashMap<>();
        save.put(ConstantNames.USER,user);
        activityTransition.back(SettingsActivity.this,save);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(fileManage.imageSelectedFromGallery ( requestCode, resultCode ))
        {
            Uri imageUri = fileManage.getImageUri ( data );
            fileManage.cropImage ( imageUri,this );
        }
        else if(fileManage.isImageCropped ( requestCode, resultCode ))
        {
            Uri imageUriResultCrop = fileManage.getCropOutput ( data );
            imageUri = imageUriResultCrop.toString ();
            fileManage.setImageUri (imageUri ,mv_logo );
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
                    fileManage.pickImageFromGallery(this);
                }
                else
                {
                    Toast.makeText ( this,"Premission denied...!",Toast.LENGTH_LONG ).show ();
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}