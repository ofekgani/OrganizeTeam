package com.myapp.organizeteam.Core;

public class Hour implements java.io.Serializable{
    private int hour;
    private int minute;

    public Hour(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Hour() {}

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
