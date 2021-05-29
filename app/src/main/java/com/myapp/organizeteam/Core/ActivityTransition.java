package com.myapp.organizeteam.Core;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.myapp.organizeteam.R;

import java.io.Serializable;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;


public class ActivityTransition  extends AppCompatActivity {

    /**
     * This function use to go to new activity.
     * @param from The activity from which the user moves to a new activity.
     * @param to The activity that user move on.
     * @param finish Close the previous activity or not.
     * @param values data to save into intent.
     * @param anim Transform between activities.
     */
    public void goTo(Activity from, Class to, boolean finish, Map<String,Object> values, ActivityOptions anim)
    {
        Intent intent = new Intent ( from,to );

        if(values != null)
        {
            for (Map.Entry value : values.entrySet ())
            {
                intent.putExtra ( ""+value.getKey (), (Serializable) value.getValue () );
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

        from.finish();
    }

    public Bundle CreateBundle(Map<String,Object> values)
    {
        Bundle bundle = new Bundle();
        if(values != null)
        {
            for (Map.Entry value : values.entrySet ())
            {
                bundle.putSerializable ( ""+value.getKey (),(Serializable)value.getValue () );
            }
            return bundle;
        }
        return null;
    }

    /**
     * This function use to go to a new activity in order to return to the last activity with a result back.
     * @param from The activity from which the user moves to a new activity.
     * @param to The activity that user move on.
     * @param request The request that you want to send to return result.
     * @param values data to save into intent.
     * @param anim Transform between activities.
     */
    public void goToWithResult(Activity from, Class to,int request,Map<String,Object> values, ActivityOptions anim)
    {
        Intent intent = new Intent ( from,to );

        if(values != null)
        {
            for (Map.Entry value : values.entrySet ())
            {
                intent.putExtra ( ""+value.getKey (), (Serializable) value.getValue () );
            }
        }

        if(anim != null)
        {
            from.startActivityForResult (intent,request,anim.toBundle ());
        }
        else
        {
            from.startActivityForResult (intent,request);
        }
    }

    /**
     * This function use to close the activity.
     * @param activity the activity that you want to close.
     */
    public void back(Activity activity)
    {
        activity.finish ();
    }

    /**
     * This function use to close the activity with result.
     * @param activity the activity that you want to close.
     * @param values the values that you want to use when you call to the request.
     */
    public void back(Activity activity, Map<String,Object> values)
    {
        Intent intent = new Intent (  );

        if(values != null)
        {
            for (Map.Entry value : values.entrySet ())
            {
                intent.putExtra ( ""+value.getKey (), (Serializable) value.getValue () );
            }
        }

        activity.setResult(Activity.RESULT_OK,intent);
        activity.finish ();
    }

    /**
     * This function use to set new data into intent.
     * @param intent The intent you want put the data.
     * @param key The key into which the value will be stored.
     * @param value The value to be stored into the key.
     */
    public void setData(Intent intent,String key, String value){
        intent.putExtra ( key,value );
    }
}
