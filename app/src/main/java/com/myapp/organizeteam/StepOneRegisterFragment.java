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

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataPass;
import com.myapp.organizeteam.DataManagement.IRegister;
import com.myapp.organizeteam.Resources.Loading;
import com.myapp.organizeteam.Resources.Stepper;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.HashMap;
import java.util.Map;

public class StepOneRegisterFragment extends Fragment implements Step {

    Authorization authorization;
    InputManagement input;
    Loading progressBar;
    Stepper stepper;


    Button btn_create;
    EditText ed_email, ed_confirmPassword, ed_password;
    ProgressBar pb;

    StepperLayout mStepperLayout;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_register_step1, container, false);

        //allocating memory
        authorization = new Authorization ();
        input = new InputManagement ();
        stepper = new Stepper();
        progressBar = new Loading ( );

        btn_create = v.findViewById(R.id.btn_createAccount);
        ed_email = v.findViewById(R.id.ed_email);
        ed_password = v.findViewById(R.id.ed_password);
        ed_confirmPassword = v.findViewById(R.id.ed_confirmPassword);
        pb = v.findViewById(R.id.pb_verify);

        mStepperLayout = stepper.getStepperLayout(container,R.id.stepperLayout);

        progressBar.setVisible ( pb,false );

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if the input is valid
                if(!input.isInputValid ( ed_email, ed_password, ed_confirmPassword ))
                    return;

                final String email = input.getInput(ed_email);
                final String password = input.getInput(ed_password);

                //Create user in firebase
                authorization.createUser(email, password, new IRegister() {
                    @Override
                    public void onProcess() {
                        progressBar.setVisible(pb,true);
                    }

                    @Override
                    public void onDone(boolean successful,String message) {
                        progressBar.setVisible(pb,false);
                        if(successful)
                        {
                            //Save user`s email and password
                            saveUserInformation();

                            //Go to next step
                            stepper.goNext(mStepperLayout);
                        }
                        else
                        {
                            Snackbar.make(v,""+message, BaseTransientBottomBar.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
        return v;
    }

    /**
     * Save user`s email and password into global variable to use to next step
     */
    private void saveUserInformation() {
        FirebaseAuth fba = FirebaseAuth.getInstance();

        User user = new User(null, fba.getCurrentUser().getEmail(),null,null,fba.getCurrentUser().getUid());
        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put(ConstantNames.USER,user);

        //Here all data will save to next use
        DataPass.passData = userInfo;
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
