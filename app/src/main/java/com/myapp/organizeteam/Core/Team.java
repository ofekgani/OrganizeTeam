package com.myapp.organizeteam.Core;


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
    private String hostID;
    private String keyID;

    public Team(String name, String description, String logo, String hostID, String keyID) {
        this.name = name;
        this.description = description;
        this.keyID = keyID;
        this.hostID = hostID;
        this.logo = logo;
    }

    public Team(){}

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

    public String getHost() {
        return hostID;
    }

    public void setHost(String hostID) {
        this.hostID = hostID;
    }
}
