package com.myapp.organizeteam.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.R;
import com.myapp.organizeteam.Resources.Loading;
import com.myapp.organizeteam.Resources.Stepper;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

public class StepTwoRegisterFragment extends Fragment implements Step {

    StepperLayout mStepperLayout;

    ProgressBar pb;
    Button btn_verify;
    ImageView mv_verify;
    TextView tv_statusVerification;

    Authorization authorization;
    InputManagement input;
    Loading progressBar;
    Stepper stepper;

    //Helper variable to check if the user can go next by press on next button or not.
    boolean verified = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register_step2, container, false);

        //allocating memory
        authorization = new Authorization ();
        input = new InputManagement ();
        stepper = new Stepper();
        progressBar = new Loading ( );

        pb = v.findViewById(R.id.pb_verify);
        btn_verify = v.findViewById(R.id.btn_verify);
        mv_verify = v.findViewById(R.id.mv_emailVerify);
        tv_statusVerification = v.findViewById(R.id.tv_statusVerification);

        mStepperLayout = stepper.getStepperLayout(container,R.id.stepperLayout);

        progressBar.setVisible ( pb,false );

        updateUI();

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!verified)
                {
                    updateUI();
                }
                else
                {
                    stepper.goNext(mStepperLayout);
                }

            }
        });

        return v;
    }

    /**
     * update the ui when the current user`s email is verified and update verification status.
     */
    private void updateUI() {

        FirebaseAuth fba = FirebaseAuth.getInstance();

        if(fba.getCurrentUser() == null) return;
        fba.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (authorization.isEmailVerified()) {
                    btn_verify.setText("Next");
                    tv_statusVerification.setText("Email is verified.");
                    mv_verify.setImageResource(R.drawable.mark_icon);

                    //The user can go next now
                    if (!verified) verified = true;
                }
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
