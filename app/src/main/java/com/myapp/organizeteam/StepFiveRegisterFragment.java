package com.myapp.organizeteam;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.IRegister;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.Resources.Loading;
import com.myapp.organizeteam.Resources.Stepper;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Map;

public class StepFiveRegisterFragment extends Fragment implements Step {

    StepperLayout mStepperLayout;

    TextView tv_prevStep, tv_name, tv_phone, tv_email;
    ImageView mv_userLogo;

    Authorization authorization;
    InputManagement input;
    Loading progressBar;
    Stepper stepper;
    Image image;
    DataExtraction dataExtraction;

    Map<String, Object> userData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register_step5, container, false);

        //allocating memory
        authorization = new Authorization ();
        input = new InputManagement ();
        stepper = new Stepper();
        progressBar = new Loading ( );
        image = new Image();
        dataExtraction = new DataExtraction();

        tv_prevStep = v.findViewById(R.id.tv_prevStep);
        tv_name = v.findViewById(R.id.tv_userName);
        tv_email = v.findViewById(R.id.tv_userEmail);
        tv_phone = v.findViewById(R.id.tv_userPhone);
        mv_userLogo = v.findViewById(R.id.mv_userLogo);

        mStepperLayout = stepper.getStepperLayout(container,R.id.stepperLayout);

        Bundle bundle = this.getArguments();

        tv_prevStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepper.goBack(mStepperLayout);
            }
        });

        return v;
    }

    private void updateUserData(User user) {

        if(user.getFullName() != null)
        {
            tv_name.setText(user.getFullName());
        }
        if(user.getEmail() != null)
        {
            tv_email.setText(user.getEmail());
        }
        if(user.getPhone() != null)
        {
            tv_phone.setText(user.getPhone());
        }
        if(user.getLogo() != null && user.getLogo() != "")
        {
            image.setImageUri(user.getLogo(),mv_userLogo);
        }
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        return null;
    }

    @Override
    public void onSelected() {
        //update UI when selected
        userData = DataPass.passData;
        if(userData != null && userData.size() != 0)
        {
            if(userData.get(ConstantNames.USER) != null)
            {
                User user = (User)userData.get(ConstantNames.USER);
                updateUserData(user);
            }
        }
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }
}
