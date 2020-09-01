package com.example.organizeteam;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class User {

    private String fullName;
    private String email;
    private String userID;

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
