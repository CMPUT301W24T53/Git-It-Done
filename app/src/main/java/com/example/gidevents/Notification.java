package com.example.gidevents;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import java.io.Serializable;


public class Notification implements Serializable {
    private String eventTitle;
    private String notifInfo;


    /**
     * Default constructor
     * @param title The title of the event
     * @param info The details of the notification
     */
    public Notification(String title, String info) {
        this.eventTitle = title;
        this.notifInfo = info;
    }


    // Getters

    /**
     * Returns event title for notification
     * @return Event title
     */
    public String getEventTitle() {
        return this.eventTitle;
    }

    /**
     * Returns the details of the notification
     * @return Notification details
     */
    public String getNotifInfo() {
        return this.notifInfo;
    }

    // Setters

    /**
     * Sets the event title
     * @param eventTitle Title to set
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /**
     * Sets notification details
     * @param notifInfo Notification details
     */
    public void setNotifInfo(String notifInfo) {
        this.notifInfo = notifInfo;
    }



}
