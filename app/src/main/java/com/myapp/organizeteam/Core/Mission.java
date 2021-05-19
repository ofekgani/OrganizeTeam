package com.myapp.organizeteam.Core;

import java.io.Serializable;

public class Mission implements Serializable {

    private String keyID;
    private String teamID;
    private String taskName;
    private String taskDescription;
    private boolean requiredConfirm;
    int status;
    private Date date;
    private Hour hour;

    public Mission(String keyID, String teamID, String taskName, String taskDescription, boolean requiredConfirm, int status, Date date, Hour hour) {
        this.keyID = keyID;
        this.teamID = teamID;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.requiredConfirm = requiredConfirm;
        this.date = date;
        this.hour = hour;
    }

    public static int TIME_IS_UP = 0;
    public static int TASK_IS_ACTIVE = 1;

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

    public boolean isRequiredConfirm() {
        return requiredConfirm;
    }

    public void setRequiredConfirm(boolean requiredConfirm) {
        this.requiredConfirm = requiredConfirm;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
