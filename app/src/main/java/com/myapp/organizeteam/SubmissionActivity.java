package com.myapp.organizeteam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.organizeteam.Core.ActivityTransition;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.Mission;
import com.myapp.organizeteam.Core.Submitter;
import com.myapp.organizeteam.Core.Team;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.Resources.FileManage;

import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class SubmissionActivity extends AppCompatActivity {

    FileManage fileManage;
    ActivityTransition activityTransition;

    TextView tv_title, tv_content, tv_userName, tv_fileName;
    ImageView mv_userLogo, mv_fileDownload;
    Toolbar toolbar;

    Intent intent;

    Mission mission;
    User user,userSubmitter;
    Submitter submitter;
    Team team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);

        fileManage = new FileManage();
        activityTransition = new ActivityTransition();

        tv_userName = findViewById(R.id.tv_userName);
        tv_fileName = findViewById(R.id.tv_fileName);
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);

        mv_userLogo = findViewById(R.id.mv_userLogo);
        mv_fileDownload = findViewById(R.id.mv_fileDownload);

        toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        intent = getIntent();
        mission = (Mission) intent.getSerializableExtra(ConstantNames.TASK);
        user = (User) intent.getSerializableExtra(ConstantNames.USER);
        submitter = (Submitter) intent.getSerializableExtra(ConstantNames.SUBMITTER);
        userSubmitter = (User) intent.getSerializableExtra(ConstantNames.USER_SUBMITTER);
        team = (Team) intent.getSerializableExtra(ConstantNames.TEAM);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(""+mission.getTaskName());

        tv_userName.setText(""+userSubmitter.getFullName());
        fileManage.setImageUri(userSubmitter.getLogo(),mv_userLogo);

        tv_title.setText(""+submitter.getTitle());
        tv_content.setText(""+submitter.getContent());
        tv_fileName.setText(""+submitter.getFileName());

        mv_fileDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileManage.downloadFile(SubmissionActivity.this,submitter.getFileName(),DIRECTORY_DOWNLOADS,submitter.getFileUrl());
            }
        });
    }

    public void oc_reply(View view) {
        Map<String,Object> save = new HashMap<>();
        save.put(ConstantNames.SUBMITTER,submitter);
        save.put(ConstantNames.USER,user);
        save.put(ConstantNames.TASK,mission);
        save.put(ConstantNames.TEAM,team);
        activityTransition.goTo(SubmissionActivity.this,TaskReplyActivity.class,false,save,null);
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

    public void oc_showResponses(View view) {
        Map<String,Object> save = new HashMap<>();
        save.put(ConstantNames.USER,user);
        save.put(ConstantNames.USER_SUBMITTER,userSubmitter);
        save.put(ConstantNames.TASK,mission);
        save.put(ConstantNames.TEAM,team);
        activityTransition.goTo(SubmissionActivity.this,ResponsesListActivity.class,false,save,null);
    }
}