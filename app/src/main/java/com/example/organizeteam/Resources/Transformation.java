package com.example.organizeteam.Resources;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.view.View;

import com.example.organizeteam.MainActivity;
import com.example.organizeteam.R;

import androidx.appcompat.app.AppCompatActivity;

public class Transformation extends AppCompatActivity {

    public ActivityOptions pushUp(Context context)
    {
        return ActivityOptions.makeCustomAnimation( context,R.anim.push_up_in, R.anim.push_up_out);
    }

    public ActivityOptions pushDown(Context context)
    {
        return ActivityOptions.makeCustomAnimation( context,R.anim.push_down_out, R.anim.push_down_in);
    }
}
