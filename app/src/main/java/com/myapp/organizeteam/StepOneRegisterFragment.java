package com.myapp.organizeteam;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.IRegister;
import com.myapp.organizeteam.Resources.Loading;
import com.myapp.organizeteam.Resources.Stepper;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

public class StepOneRegisterFragment extends Fragment implements Step {

    Button btn_create;
    EditText ed_email, ed_password, ed_confirmPassword;
    ProgressBar pb;

    StepperLayout mStepperLayout;

    Authorization authorization;
    InputManagement input;
    Loading progressBar;
    Stepper stepper;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register_step1, container, false);

        btn_create = v.findViewById(R.id.btn_createAccount);

        ed_email = v.findViewById(R.id.ed_email);
        ed_password = v.findViewById(R.id.ed_password);
        ed_confirmPassword = v.findViewById(R.id.ed_confirmPassword);

        pb = v.findViewById(R.id.pb_verify);

        //allocating memory
        authorization = new Authorization ();
        input = new InputManagement ();
        stepper = new Stepper();
        progressBar = new Loading ( );

        mStepperLayout = stepper.getStepperLayout(container,R.id.stepperLayout);

        progressBar.setVisible ( pb,false );

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!input.isInputValid ( ed_email, ed_password, ed_confirmPassword ))
                    return;

                final String email = input.getInput(ed_email);
                final String password = input.getInput(ed_password);

                //create user in firebase
                authorization.createUser(email, password, new IRegister() {
                    @Override
                    public void onProcess() {
                        progressBar.setVisible(pb,true);
                    }

                    @Override
                    public void onDone(boolean successful,String message) {
                        if(successful)
                        {
                            progressBar.setVisible(pb,false);
                            stepper.goNext(mStepperLayout);
                        }
                    }
                });
            }
        });
        return v;
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        return null;
    }

    @Override
    public void onSelected() {
        //update UI when selected
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }

}
