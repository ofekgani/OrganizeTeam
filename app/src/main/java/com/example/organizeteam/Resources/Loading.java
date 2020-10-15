package com.example.organizeteam.Resources;

import android.view.View;
import android.widget.ProgressBar;

public class Loading {

    /**
     * Set the progress bar resource to visible or not.
     * @param progressBar a progress bar resource.
     * @param visible visible or not.
     */
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
