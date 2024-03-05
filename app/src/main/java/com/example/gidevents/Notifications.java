package com.example.gidevents;

import java.util.Date;
public class Notifications {
    private String eventTitle;
    private String organizer;
    private String notifInfo;
    private Date date;

    public Notifications(String title, String organizer, String info, Date date) {
        this.eventTitle = title;
        this.organizer = organizer;
        this.notifInfo = info;
        this.date = date;
    }

    // Getters
    public String getEventTitle() {
        return this.eventTitle;
    }

    public String getOrganizer() {
        return this.organizer;
    }

    public String getNotifInfo() {
        return this.notifInfo;
    }

    public Date getDate() {
        return this.date;
    }

    // Setters
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setOrganizer(String organizer) {
         this.organizer = organizer;
    }

    public void setNotifInfo(String notifInfo) {
         this.notifInfo = notifInfo;
    }

    public void setDate(Date date) {
         this.date = date;
    }

}
