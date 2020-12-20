package com.myapp.organizeteam.Resources;

import android.view.View;
import android.view.ViewGroup;

import com.myapp.organizeteam.R;
import com.stepstone.stepper.StepperLayout;

public class Stepper {

    public StepperLayout getStepperLayout(ViewGroup container, int id) {
        View parentView = container.getRootView();
        return parentView.findViewById(id);
    }

    public void goNext(StepperLayout stepperLayout) {
        stepperLayout.setCurrentStepPosition(stepperLayout.getCurrentStepPosition()+1);
    }

    public void goBack(StepperLayout stepperLayout) {
        stepperLayout.setCurrentStepPosition(stepperLayout.getCurrentStepPosition()-1);
    }

    public void go(StepperLayout stepperLayout,int position) {
        stepperLayout.setCurrentStepPosition(position);
    }
}
