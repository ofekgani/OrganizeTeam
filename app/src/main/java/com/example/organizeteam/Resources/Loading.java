package com.example.organizeteam.Resources;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.storage.UploadTask;

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

    public void calculatePercent(UploadTask.TaskSnapshot taskSnapshot, ProgressDialog pd) {
        double progressPercent = (100 * taskSnapshot.getBytesTransferred () /  taskSnapshot.getTotalByteCount ());
        pd.setMessage ( "progress: " + (int)progressPercent + "%");
    }

    public ProgressDialog getProgressDialog(Context context, String title) {
        final ProgressDialog pd = new ProgressDialog ( context );
        pd.setCanceledOnTouchOutside(false);
        pd.setTitle ( ""+title );
        pd.show ();
        return pd;
    }
}
