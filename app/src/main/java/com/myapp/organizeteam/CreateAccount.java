package com.myapp.organizeteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.myapp.organizeteam.Adapters.MyStepperAdapter;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.Stepper;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

public class CreateAccount extends AppCompatActivity implements StepperLayout.StepperListener {

    private StepperLayout mStepperLayout;
    Intent intent;

    DataExtraction dataExtraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        dataExtraction = new DataExtraction();

        intent = getIntent();
        String password = (String)intent.getSerializableExtra("password");

        mStepperLayout = findViewById(R.id.stepperLayout);
        mStepperLayout.setAdapter(new MyStepperAdapter(getSupportFragmentManager(), null, password));

        Authorization authorization = new Authorization();
        final Stepper stepper = new Stepper();

        if(!authorization.isUserConnected() && !authorization.isEmailVerified())
        {
            stepper.go(mStepperLayout,0);
        }
        else if(authorization.isUserConnected() && !authorization.isEmailVerified())
        {
            stepper.go(mStepperLayout,1);
        }
        else if(authorization.isUserConnected() && authorization.isEmailVerified())
        {
            dataExtraction.hasChild(ConstantNames.USER_PATH, authorization.getUserID(), ConstantNames.DATA_USER_NAME, new ISavable() {
                @Override
                public void onDataRead(Object save) {
                    if((boolean)save)
                    {
                        stepper.go(mStepperLayout, 3);
                    }
                    else
                    {
                        stepper.go(mStepperLayout, 2);
                    }
                }
            });
        }
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
}