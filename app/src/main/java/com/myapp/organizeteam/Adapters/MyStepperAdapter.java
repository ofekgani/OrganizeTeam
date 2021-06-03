package com.myapp.organizeteam.Adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.myapp.organizeteam.Core.ConstantNames;
import com.myapp.organizeteam.Fragments.StepFiveRegisterFragment;
import com.myapp.organizeteam.Fragments.StepFourRegisterFragment;
import com.myapp.organizeteam.Fragments.StepOneRegisterFragment;
import com.myapp.organizeteam.Fragments.StepThreeRegisterFragment;
import com.myapp.organizeteam.Fragments.StepTwoRegisterFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import java.io.Serializable;
import java.util.Map;

public class MyStepperAdapter extends AbstractFragmentStepAdapter {

    Map<String,Object> data;
    Context context;

    public MyStepperAdapter(FragmentManager fm, Context context, Map<String,Object> data) {
        super(fm, context);

        this.data =  data;
        this.context = context;
    }

    @Override
    public Step createStep(int position) {

        if(position == 0)
        {
            final StepOneRegisterFragment step = new StepOneRegisterFragment();
            return step;
        }
        else if(position == 1)
        {
            final StepTwoRegisterFragment step = new StepTwoRegisterFragment();
            return step;
        }
        else if(position == 2)
        {
            final StepThreeRegisterFragment step = new StepThreeRegisterFragment();
            if(data != null)
            {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ConstantNames.USER, (Serializable) data);
                step.setArguments(bundle);
            }

            return step;
        }
        else if(position == 3)
        {
            final StepFourRegisterFragment step = new StepFourRegisterFragment();
            return step;
        }
        else
        {
            final StepFiveRegisterFragment step = new StepFiveRegisterFragment();
            if(data != null)
            {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ConstantNames.USER, (Serializable) data);
                step.setArguments(bundle);
            }
            return step;
        }


    }

    @Override
    public int getCount() {
        return 5;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        StepViewModel stepView;
        if(position == 0)
        {
            stepView = new StepViewModel.Builder(context)
                    .setTitle("Create account") //can be a CharSequence instead
                    .create();
        }
        else if(position == 1)
        {
            stepView = new StepViewModel.Builder(context)
                    .setTitle("Email verification") //can be a CharSequence instead
                    .create();
        }
        else if(position == 2)
        {
            stepView = new StepViewModel.Builder(context)
                    .setTitle("Setup profile") //can be a CharSequence instead
                    .create();
        }
        else if(position == 3)
        {
            stepView = new StepViewModel.Builder(context)
                    .setTitle("Add phone") //can be a CharSequence instead
                    .setSubtitle("Optional")
                    .create();
        }
        else
        {
            stepView = new StepViewModel.Builder(context)
                    .setTitle("Join to team") //can be a CharSequence instead
                    .create();
        }
        return stepView;
    }

    public void updateUserData(Map<String,Object> data)
    {
        this.data = data;
    }


}
