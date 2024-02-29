package com.example.gidevents;

import java.io.Serializable;

public class Events implements Serializable {
    private String eventTitle;
    private String eventDate;
    private String organizer;
    private String eventDescription;
    private int eventPoster;

    public Events (String title, String date, String organizer, String description, int eventPoster) {
        this.eventTitle = title;
        this.eventDate = date;
        this.organizer = organizer;
        this.eventDescription = description;
        this.eventPoster = eventPoster;
    }
    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventOrganizer() {
        return organizer;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public int getEventPoster() {
        return eventPoster;
    }
    // Setters
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventOrganizer(String eventOrganizer) {
        this.organizer = eventOrganizer;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setEventPoster(int posterAddress) {
        this.eventPoster = posterAddress;
    }
}
