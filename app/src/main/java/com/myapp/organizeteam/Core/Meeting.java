package com.myapp.organizeteam.Core;

import java.io.Serializable;

public class Meeting implements Serializable {

    private String keyID;
    private String teamID;
    private String meetingName;
    private String meetingDescription;
    private Date date;
    private Hour hour;
    private int status;

    public Meeting(String keyID, String teamID, String meetingName, String meetingDescription, Date date, Hour hour, int status) {
        this.keyID = keyID;
        this.teamID = teamID;
        this.meetingName = meetingName;
        this.meetingDescription = meetingDescription;
        this.date = date;
        this.hour = hour;
        this.status = status;
    }

    public Meeting(){}

    public static int ARRIVAL_CONFIRMATION = 2;
    public static int ARRIVED = 1;
    public static int NO_ANSWER = -1;

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getMeetingDescription() {
        return meetingDescription;
    }

    public void setMeetingDescription(String meetingDescription) {
        this.meetingDescription = meetingDescription;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public static int FLAG_MEETING_BOOKED = 0;
    public static int FLAG_MEETING_STARTED = 1;
}
