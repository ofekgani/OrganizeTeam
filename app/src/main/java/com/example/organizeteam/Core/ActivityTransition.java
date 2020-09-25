package com.example.organizeteam.Core;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;

import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityTransition  extends AppCompatActivity {
    public void goTo(Context from, Class to, boolean finish, Map<String,Object> values, ActivityOptions anim)
    {
        Intent intent = new Intent ( from,to );

        if(values != null)
        {
            for (Map.Entry value : values.entrySet ())
            {
                intent.putExtra ( ""+value.getKey (), (String) value.getValue () );
            }
        }

        if(anim != null)
        {
           from.startActivity(intent,anim.toBundle () );
        }
        else
        {
            from.startActivity(intent);
        }

        if(!finish)
            return;

        finish();
    }
}
