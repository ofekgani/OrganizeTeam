package com.example.organizeteam.Resources;

import android.app.ActivityOptions;
import android.content.Context;

import com.example.organizeteam.R;

import androidx.appcompat.app.AppCompatActivity;

public class Transformation extends AppCompatActivity {

    /**
     * push up transformation.
     */
    public ActivityOptions pushUp(Context context)
    {
        return ActivityOptions.makeCustomAnimation( context,R.anim.push_up_in, R.anim.push_up_out);
    }

    /**
     * push down transformation.
     */
    public ActivityOptions pushDown(Context context)
    {
        return ActivityOptions.makeCustomAnimation( context,R.anim.push_down_out, R.anim.push_down_in);
    }
}
