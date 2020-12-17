package com.myapp.organizeteam.Adapters;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.myapp.organizeteam.StepFourRegisterFragment;
import com.myapp.organizeteam.StepOneRegisterFragment;
import com.myapp.organizeteam.StepThreeRegisterFragment;
import com.myapp.organizeteam.StepTwoRegisterFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class MyStepperAdapter extends AbstractFragmentStepAdapter {

    public MyStepperAdapter(FragmentManager fm, Context context) {
        super(fm, context);
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
            return step;
        }
        else
        {
            final StepFourRegisterFragment step = new StepFourRegisterFragment();
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
        else
        {
            stepView = new StepViewModel.Builder(context)
                    .setTitle("Add phone") //can be a CharSequence instead
                    .setSubtitle("Optional")
                    .create();
        }
        return stepView;
    }
}
