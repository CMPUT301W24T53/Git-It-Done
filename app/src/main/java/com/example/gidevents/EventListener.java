package com.example.gidevents;

public interface EventListener {
    public void onAttendeeLimitReceived(int attendeeLimit);
    public void onAttendeeCountReceived(int attendeeCount);
}
