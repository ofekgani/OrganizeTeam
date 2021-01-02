package com.myapp.organizeteam.DataManagement;

public interface IRegister {
    void onProcess(); //All actions that will occur in the action process
    void onDone(boolean successful,String message); //All actions that occur in an action when the action is completed
}
