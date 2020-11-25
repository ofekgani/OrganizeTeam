package com.myapp.organizeteam.Core;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class User implements java.io.Serializable{

    private String fullName;
    private String email;
    private String logo;
    private String keyID;

    public User(String fullName, String email,String logo,String keyID) {
        this.fullName = fullName;
        this.email = email;
        this.keyID = keyID;
        this.logo = logo;
    }

    public User() {}

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

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

}
