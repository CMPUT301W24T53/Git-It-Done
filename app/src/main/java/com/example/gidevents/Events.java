package com.example.gidevents;

import java.io.Serializable;

/** This is the class that defines Events
 */
public class Events implements Serializable {
    private String eventTitle;
    private String eventDate;
    private String eventTime;
    private String eventLocation;
    private String eventOrganizer;
    private String eventDescription;
    private String eventID;
    private String eventPoster;

    /** No-argument constructor for Firebase */
    public Events() {
    }
    /** This is the constructor for Events class
     * @param title is the title of the event
     * @param date is the date of the event
     * @param organizer is the organizer of the event
     * @param description the description of event
     * @param eventPoster poster of event
     * @param eventID event ID
     */

    public Events (String title, String date, String time, String location, String organizer, String description, String eventPoster, String eventID) {
      
        this.eventTitle = title;
        this.eventDate = date;
        this.eventTime = time;
        this.eventLocation = location;
        this.eventOrganizer = organizer;
        this.eventDescription = description;
        this.eventPoster = eventPoster;
        this.eventID = eventID;
    }


    /** Getter of event title
     * @return String of event title
     */
    public String getEventTitle() {
        return eventTitle;
    }

    /** Getter of event date
     * @return String of event date
     */
    public String getEventDate() {
        return eventDate;
    }

    /** Getter of event time
     * @return String of event time
     */
    public String getEventTime() { return eventTime; }

    /** Getter of event location
     * @return String of event location
     */
    public String getEventLocation() { return eventLocation; }

    /** Getter of event organizer
     * @return the string of event organizer
     */
    public String getEventOrganizer() {
        return eventOrganizer;
    }

    /** Getter of event description
     * @return event description in String
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /** Getter of event poster
     * Not working for now
     * @return the event poster
     */
    public String getEventPoster() {
        return eventPoster;
    }

    /** Getter of event ID
     * generated by organizer (or database)
     * @return the event id
     */
    public String getEventID() {return eventID;}

    // Setters

    /** Setter of event title
     * @param eventTitle is the string of event title
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /** Setter of event date
     *
     * @param eventDate is the string of event date
     */
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    /** Setter of event organizer
     *
     * @param eventOrganizer string of the organizer name
     */
    public void setEventOrganizer(String eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }


    /** Setter of event description
     *
     * @param eventDescription description of events
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /** Setter of event poster
     * not working for now
     * @param posterAddress the address of the poster
     */
    public void setEventPoster(String posterAddress) {
        this.eventPoster = posterAddress;
    }

    /** Setter of event ID
     * used to set the event ID after generated by organizer or firebase
     * @param eventID the event ID
     */
    public void setEventID(String eventID) {this.eventID = eventID;}
}
