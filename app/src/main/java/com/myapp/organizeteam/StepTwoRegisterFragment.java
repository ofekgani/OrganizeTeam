package com.myapp.organizeteam;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.IRegister;
import com.myapp.organizeteam.Resources.Loading;
import com.myapp.organizeteam.Resources.Stepper;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

public class StepTwoRegisterFragment extends Fragment implements Step {

    StepperLayout mStepperLayout;

    ProgressBar pb;
    Button btn_verify;
    EditText ed_password;

    Authorization authorization;
    InputManagement input;
    Loading progressBar;
    Stepper stepper;

    String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register_step2, container, false);

        pb = v.findViewById(R.id.pb_verify);
        btn_verify = v.findViewById(R.id.btn_verify);
        ed_password = container.findViewById(R.id.ed_password);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            password = bundle.getString("password");
        }

        //allocating memory
        authorization = new Authorization ();
        input = new InputManagement ();
        stepper = new Stepper();
        progressBar = new Loading ( );

        mStepperLayout = stepper.getStepperLayout(container,R.id.stepperLayout);

        progressBar.setVisible ( pb,false );

        updateUI();

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI();
            }
        });

        return v;
    }

    private void updateUI() {
        String password;
        Log.i("IDOEFES","- "+input.getInput(ed_password));
        final FirebaseAuth fba = FirebaseAuth.getInstance ();

        if(fba.getCurrentUser() == null)
            return;
        if(input.getInput(ed_password) == null || input.getInput(ed_password).equals(""))
        {
            if(this.password != null && !this.password.equals(""))
            {
                password = this.password;
            }
            else
            {
                return;
            }
        }
        else
        {
            password = input.getInput(ed_password);
        }
        String email = fba.getCurrentUser().getEmail();

        if(password == null) return;
        authorization.login(email, password, new IRegister() {
            @Override
            public void onProcess() {
                progressBar.setVisible ( pb,true );
                if(authorization.isEmailVerified())
                {
                    btn_verify.setText("Next");
                }
            }

            @Override
            public void onDone(boolean successful, String message) {
                progressBar.setVisible ( pb,false );
            }
        });

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
