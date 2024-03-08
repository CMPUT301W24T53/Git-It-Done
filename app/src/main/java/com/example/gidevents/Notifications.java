package com.example.gidevents;

import java.io.Serializable;
import java.util.Date;
public class Notifications implements Serializable {
    private String eventTitle;
    private String notifInfo;
    private String date;

    public Notifications(String title, String info, String date) {
        this.eventTitle = title;
        this.notifInfo = info;
        this.date = date;
    }

    // Getters
    public String getEventTitle() {
        return this.eventTitle;
    }

    public String getNotifInfo() {
        return this.notifInfo;
    }

    public String getDate() {
        return this.date;
    }

    // Setters
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setNotifInfo(String notifInfo) {
         this.notifInfo = notifInfo;
    }

    public void setDate(String date) {
         this.date = date;
    }

}
