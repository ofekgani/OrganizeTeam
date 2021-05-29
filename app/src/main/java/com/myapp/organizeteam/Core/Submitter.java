package com.myapp.organizeteam.Core;

import java.io.Serializable;

public class Submitter implements Serializable {
    String title;
    String content;
    String fileUrl;
    String fileName;
    int confirmStatus;
    String taskID;
    String userID;

    public Submitter(String title, String content, String fileUrl, String fileName, int confirmStatus, String taskID, String userID) {
        this.title = title;
        this.content = content;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.confirmStatus = confirmStatus;
        this.taskID = taskID;
        this.userID = userID;
    }

    public Submitter() { }

    public static final int STATUS_CONFIRM = 1;
    public static final int STATUS_UNCONFIRMED = 0;
    public static final int STATUS_WAITING = -1;
    public static final int STATUS_UNSUBMITTED = -2;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirm(int confirm) {
        confirmStatus = confirm;
    }
}
