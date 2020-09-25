package com.example.organizeteam.Resources;

import android.view.View;
import android.widget.ProgressBar;

public class Loading {

    public void setVisible(ProgressBar progressBar,boolean visible)
    {
        if(visible)
        {
            progressBar.setVisibility( View.VISIBLE );
        }
        else
        {
            progressBar.setVisibility( View.GONE );
        }

    }
}
