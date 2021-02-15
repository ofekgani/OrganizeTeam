package com.myapp.organizeteam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.myapp.organizeteam.Adapters.MyStepperAdapter;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.DataPass;
import com.myapp.organizeteam.Resources.Stepper;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity implements StepperLayout.StepperListener {

    private StepperLayout mStepperLayout;

    DataExtraction dataExtraction;
    Stepper stepper;
    Authorization authorization;

    Toolbar toolbar;

    Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        toolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dataExtraction = new DataExtraction();
        authorization = new Authorization();
        stepper = new Stepper();

        intent = getIntent();
        int step = intent.getIntExtra("step",0);
        Map<String,Object> data = (Map<String, Object>) intent.getSerializableExtra(ConstantNames.USER);

        DataPass.passData = data;

        mStepperLayout = findViewById(R.id.stepperLayout);
        mStepperLayout.setAdapter(new MyStepperAdapter(getSupportFragmentManager(), this,data));

        stepper.go(mStepperLayout, step);


    }

    @Override
    public void onCompleted(View completeButton) {
        Toast.makeText(this, "onCompleted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(VerificationError verificationError) {
        Toast.makeText(this, "onError! -> " + verificationError.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        Toast.makeText(this, "onStepSelected! -> " + newStepPosition, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReturn() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        StepThreeRegisterFragment stepThreeRegisterFragment = new StepThreeRegisterFragment();
        if(stepThreeRegisterFragment != null)
            stepThreeRegisterFragment.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                authorization.singOut(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}