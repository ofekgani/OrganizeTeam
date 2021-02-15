package com.myapp.organizeteam.Core;

public class Role implements java.io.Serializable {
    private String keyID;
    private String teamID;
    private String name;
    private String description;

    public Role(String keyID, String teamID, String name, String description) {
        this.keyID = keyID;
        this.teamID = teamID;
        this.name = name;
        this.description = description;
    }

    public Role(){}

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
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
}
