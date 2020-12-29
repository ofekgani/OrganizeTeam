package com.myapp.organizeteam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;
import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Core.InputManagement;
import com.myapp.organizeteam.Core.User;
import com.myapp.organizeteam.DataManagement.Authorization;
import com.myapp.organizeteam.DataManagement.DataExtraction;
import com.myapp.organizeteam.DataManagement.IRegister;
import com.myapp.organizeteam.DataManagement.ISavable;
import com.myapp.organizeteam.MyService.Data;
import com.myapp.organizeteam.Resources.Image;
import com.myapp.organizeteam.Resources.Loading;
import com.myapp.organizeteam.Resources.Stepper;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StepFourRegisterFragment extends Fragment implements Step {

    StepperLayout mStepperLayout;

    CountryCodePicker ccp;
    TelephonyManager telephonyManager;
    EditText ed_phone;
    TextView tv_skipButton, tv_prevStep;
    Button btn_next;
    ProgressBar pb;

    String codeVerification, keyID;
    PhoneAuthProvider.ForceResendingToken tokenVerification;

    Authorization authorization;
    InputManagement input;
    Loading progressBar;
    Stepper stepper;
    Image image;
    DataExtraction dataExtraction;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register_step4, container, false);

        ccp = v.findViewById(R.id.ccp);
        ed_phone = v.findViewById(R.id.ed_phone);
        btn_next = v.findViewById(R.id.btn_next);
        pb = v.findViewById(R.id.pb_next);
        tv_skipButton = v.findViewById(R.id.tv_btn_skip);
        tv_prevStep = v.findViewById(R.id.tv_prevStep);

        //allocating memory
        authorization = new Authorization ();
        input = new InputManagement ();
        stepper = new Stepper();
        progressBar = new Loading ( );
        image = new Image();
        dataExtraction = new DataExtraction();

        keyID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mStepperLayout = stepper.getStepperLayout(container,R.id.stepperLayout);

        setCountryNumber();

        progressBar.setVisible(pb,false);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullPhone = getPhoneNumber();
                sendVerificationCodeToUser(fullPhone);
            }
        });

        tv_skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepper.goNext(mStepperLayout);
            }
        });

        tv_prevStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepper.goBack(mStepperLayout);
            }
        });

        return v;
    }

    private void createDialogVerification(String code) {
        final EditText ed_pinCode = new EditText(getContext());
        ed_pinCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        if(code != null)
        {
            ed_pinCode.setText(""+code);
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Verify phone number")
                .setView(ed_pinCode)
                .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = ed_pinCode.getText().toString();
                if(input.isInputEmpty(ed_pinCode) || code.length() < 6)
                {
                    input.setError(ed_pinCode,"Wrong OTP...");
                    ed_pinCode.requestFocus();
                    return;
                }
                verifyCode(code);
                alertDialog.dismiss();
            }
        });
    }

    private String getPhoneNumber() {
        String phoneNumber = ed_phone.getText().toString().trim();
        return "+"+ccp.getSelectedCountryCode()+phoneNumber;
    }

    private void setCountryNumber() {
        telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);

        String countryNum = telephonyManager.getNetworkCountryIso();
        ccp.setCountryForNameCode(countryNum);
    }

    private void sendVerificationCodeToUser(String phone)
    {
        mCallbacks.onCodeAutoRetrievalTimeOut(codeVerification);
        if(tokenVerification == null)
        {
            authorization.sendVerifyPhoneNumber(getActivity(),phone,mCallbacks);
        }
        else
        {
            authorization.sendVerifyPhoneNumber(getActivity(),phone,mCallbacks,tokenVerification);
            tokenVerification = null;
        }

        progressBar.setVisible(pb,true);
    }

    private void verifyCode(String code) {
        authorization.signInWithPhoneNumber(codeVerification, code, new IRegister() {
            @Override
            public void onProcess() {
                progressBar.setVisible(pb,true);
            }

            @Override
            public void onDone(boolean successful, String message) {
                progressBar.setVisible(pb,false);
                if(successful)
                {
                    dataExtraction.getUserDataByID(keyID,new ISavable() {
                        @Override
                        public void onDataRead(Object save) {
                            final User user = (User)save;
                            user.setPhone(getPhoneNumber());
                            dataExtraction.setObject(ConstantNames.USER_PATH, user.getKeyID(), user, new IRegister() {
                                @Override
                                public void onProcess() {
                                    progressBar.setVisible(pb,true);
                                }

                                @Override
                                public void onDone(boolean successful, String message) {
                                    progressBar.setVisible(pb,false);
                                    if(successful)
                                    {
                                        Map<String,Object> data = DataPass.passData;
                                        data.put(ConstantNames.USER,user);
                                        DataPass.passData = data;
                                        stepper.goNext(mStepperLayout);
                                    }
                                }
                            });
                        }
                    });
                }
                else
                {
                    createDialogVerification(null);
                }
            }
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
    {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null)
            {
                progressBar.setVisible(pb,true);
                createDialogVerification(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            progressBar.setVisible(pb,false);
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            progressBar.setVisible(pb,false);

            codeVerification = s;
            tokenVerification = forceResendingToken;

            createDialogVerification(null);
        }
    };

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
