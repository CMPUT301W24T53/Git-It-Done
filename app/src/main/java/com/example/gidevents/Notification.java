package com.example.gidevents;


import java.io.Serializable;


public class Notification implements Serializable {
    private String eventTitle;
    private String notifInfo;
    private String date;
    private String notifType;


    /**
     * Default constructor
     * @param title The title of the event
     * @param info The details of the notification
     * @param date The date the notification was sent
     */
    public Notification(String title, String info, String date, String type) {
        this.eventTitle = title;
        this.notifInfo = info;
        this.date = date;
        this.notifType = type;
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

    /**
     * Returns the date the notification was sent
     * @return Notification date
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Returns the type of notification that was sent
     * May be removed if users cannot select which notifications to receive and opt out of
     * @return Notification type (currently planned to be `reminder`, `update`, and `promotion`)
     */
    public String getNotifType() {
        return this.notifType;
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

    /**
     * Sets the date the notification was sent
     * @param date Notification send date
     */
    public void setDate(String date) {
        this.date = date;
    }
}
