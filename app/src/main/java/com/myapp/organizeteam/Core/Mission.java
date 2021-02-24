package com.myapp.organizeteam.Core;

import java.io.Serializable;

public class Mission implements Serializable {

    private String keyID;
    private String teamID;
    private String taskName;
    private String taskDescription;
    private Date date;
    private Hour hour;


    public Mission(String keyID, String teamID, String taskName, String taskDescription, Date date, Hour hour) {
        this.teamID = teamID;
        this.keyID = keyID;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.date = date;
        this.hour = hour;
    }

    public Mission(){}

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Hour getHour() {
        return hour;
    }

    public void setHour(Hour hour) {
        this.hour = hour;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }
}
