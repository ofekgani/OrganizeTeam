package com.myapp.organizeteam.Core;

import java.util.ArrayList;

public class Role implements java.io.Serializable {
    private String keyID;
    private String teamID;
    private String name;
    private String description;
    private ArrayList<String> users;

    public Role(String keyID, String teamID, String name, String description, ArrayList<String> users) {
        this.keyID = keyID;
        this.teamID = teamID;
        this.name = name;
        this.description = description;
        this.users = users;
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

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public void addUser(String userID)
    {
        if(users == null) return;
        users.add(userID);
    }

    public void removeUser(String userID)
    {
        if(users == null) return;
        users.remove(userID);
    }
}
