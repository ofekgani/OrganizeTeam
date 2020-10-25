package com.example.organizeteam.Core;

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
        return editText.getText ().toString ().trim ();
    }

    /**
     * This function use to set error message  to user in editText
     * @param editText the EditText reference you want to set error.
     * @param errorText the text you want to set.
     */
    public void setError(EditText editText, String errorText) {
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

    /**
     * this method use to check if user`s input is valid.
     * @param edName user`s name.
     * @param edEmail user`s email.
     * @param edPassword user`s password.
     * @param edCurrentPassword user`s current password.
     * @return Return true if user`s input is valid, false if not or EditText reference is null.
     */
    public boolean isInputValid(EditText edName, EditText edEmail, EditText edPassword, EditText edCurrentPassword) {
        if(edEmail == null && edName == null)
            return false;
        if(edPassword == null && edCurrentPassword == null)
            return false;

        String name = getInput ( edName );
        String email = getInput ( edEmail );
        String password = getInput ( edPassword );
        String currentPassword = getInput ( edCurrentPassword );

        if(TextUtils.isEmpty ( name ))
        {
            setError ( edName, "Name is Required." );
            return false;
        }
        else if (name.length () >= 16)
        {
            setError ( edName, "Invalid name, maximum length: 16 letters" );
            return false;
        }
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
        else if(edCurrentPassword != null && TextUtils.isEmpty ( password ))
        {
            setError ( edPassword,"Password is Required." );
            return false;
        }
        else if(!currentPassword.equals ( password ))
        {
            edCurrentPassword.setError ( "Your current password is incorrect." );
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
