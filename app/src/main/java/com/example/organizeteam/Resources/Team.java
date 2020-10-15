package com.example.organizeteam.Resources;


/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class Team implements java.io.Serializable
{
    private String name;
    private String description;
    private String logo;
    private String keyID;

    public Team(String name, String description, String logo, String keyID) {
        this.name = name;
        this.description = description;
        this.keyID = keyID;
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
