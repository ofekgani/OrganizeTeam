package com.example.organizeteam;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.organizeteam.Core.ActivityTransition;
import com.example.organizeteam.Core.InputManagement;
import com.example.organizeteam.DataManagement.Authorization;
import com.example.organizeteam.DataManagement.DataExtraction;
import com.example.organizeteam.Resources.Loading;
import com.example.organizeteam.Resources.Transformation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class RegisterStepOneFragment extends Fragment {

    //xml Widgets
    EditText ed_name, ed_email, ed_password, ed_currentPassword;
    ProgressBar pb_singUp;

    Authorization authorization;
    InputManagement input;
    Transformation transformation;
    Loading progressBar;
    ActivityTransition activityTransition;
    DataExtraction dataExtraction;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate ( R.layout.fragment_register_step1, container, false );

        //References
        ed_name = view.findViewById ( R.id.ed_name );
        ed_email = view.findViewById ( R.id.ed_email );
        ed_password = view.findViewById ( R.id.ed_password );
        ed_currentPassword = view.findViewById ( R.id.ed_currentPassword );
        pb_singUp = view.findViewById ( R.id.pb_singUp );

        //allocating memory
        authorization = new Authorization ();
        transformation = new Transformation ();
        activityTransition = new ActivityTransition ();
        input = new InputManagement ();
        progressBar = new Loading ( );
        dataExtraction = new DataExtraction ();

        progressBar.setVisible ( pb_singUp,false );

        Button btn_createAccount = (Button) view.findViewById(R.id.btn_createAccount);
        btn_createAccount.setOnClickListener(new View.OnClickListener ()
        {
            @Override
            public void onClick(View v)
            {
                /*//check if user input is valid.
                if(!input.isInputValid ( ed_name, ed_email, ed_password, ed_currentPassword ))
                    return;

                //create user in firebase
                authorization.createUser ( RegisterActivity.this,ed_email,ed_password,ed_name,pb_singUp );*/

                Fragment temp = new RegisterStepTwoFragment ();
                FragmentTransaction ft = getFragmentManager ().beginTransaction();
                ft.replace(R.id.fragment_container, temp);
                ft.commit();

                ft.addToBackStack(null);
            }
        });

        ImageView mv_back = (ImageView) view.findViewById(R.id.mv_back);
        mv_back.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                /*//start Activity
                activityTransition.goTo ( RegisterActivity.this, MainActivity.class,true,null, null );*/
            }
        });

        return view;
    }
}
