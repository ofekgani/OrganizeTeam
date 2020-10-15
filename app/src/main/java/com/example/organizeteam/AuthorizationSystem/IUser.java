package com.example.organizeteam.AuthorizationSystem;

import java.util.Map;

/**
 * This interface use to get data from firebase.
 */
public interface IUser {
    void onDataRead(Map<String,Object> save);
}
