package com.myapp.organizeteam.Core;

import java.io.Serializable;

public class Post implements Serializable {

    private String keyID;
    private String teamID;
    private String postName;
    private String postContent;
    private Date date;
    private Hour hour;

    public Post(String keyID, String teamID, String postName, String postContent, Date date, Hour hour) {
        this.keyID = keyID;
        this.teamID = teamID;
        this.postName = postName;
        this.postContent = postContent;
        this.date = date;
        this.hour = hour;
    }

    public Post() {
    }

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

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
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
}
