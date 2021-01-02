package com.myapp.organizeteam.Core;

import android.text.TextUtils;
import android.widget.EditText;

public class InputManagement {

    /**
     * This function use to get input from EditText.
     * @param editText editText reference that you want get from input.
     * @return Return the input from editText to string.
     */
    public String getInput(EditText editText)
    {
        if(editText == null) return null;
        return editText.getText ().toString ().trim ();
    }

    /**
     * This function use to set error message  to user in editText
     * @param editText the EditText reference you want to set error.
     * @param errorText the text you want to set.
     */
    public void setError(EditText editText, String errorText) {
        if(editText == null) return;
        editText.setError ( ""+errorText );
    }

    /**
     * this method use to check if user`s input is valid.
     * @param edEmail user`s name.
     * @param edPassword user`s password.
     * @return Return true if user`s input is valid, false if not or EditText reference is null.
     */
    public boolean isInputValid(EditText edEmail, EditText edPassword) {
        if(edEmail == null && edPassword == null)
            return false;

        String email = getInput ( edEmail );
        String password = getInput ( edPassword );

        if(TextUtils.isEmpty ( email ))
        {
            setError ( edEmail,"Email is Required." );
            return false;
        }
        else if(TextUtils.isEmpty ( password ))
        {
            setError ( edPassword, "Password is Required." );
            return false;
        }
        return true;
    }

    public boolean isInputValid(EditText edEmail, EditText edPassword, EditText edConfirmPassword) {
        if(edEmail == null && edPassword == null)
            return false;
        if(edConfirmPassword == null)
            return false;

        String email = getInput ( edEmail );
        String password = getInput ( edPassword );
        String confirmPassword = getInput ( edConfirmPassword );

        if(TextUtils.isEmpty ( email ))
        {
            setError ( edEmail,"Email is Required." );
            return false;
        }
        else if(TextUtils.isEmpty ( password ))
        {
            setError ( edPassword, "Password is Required." );
            return false;
        }
        else if(!confirmPassword.equals ( password ))
        {
            edConfirmPassword.setError ( "Your confirm password is incorrect." );
            return false;
        }
        return true;
    }

    /**
     * Check if the input in edit text is empty or not.
     * @param editText editText reference that you want get from input.
     * @return return true if the input empty, false if not.
     */
    public boolean isInputEmpty(EditText editText)
    {
        return TextUtils.isEmpty ( getInput ( editText ) );
    }
}
