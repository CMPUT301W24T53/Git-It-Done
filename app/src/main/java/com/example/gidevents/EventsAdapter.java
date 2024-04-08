package com.example.gidevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/** This is the EventsAdapter class
 * Sets up the adapter
 */
public class EventsAdapter extends ArrayAdapter<Events> implements Filterable {
    private List<Events> events;
    public EventsAdapter(Context context, List<Events> events) {
        super(context, 0, events);
        this.events = events;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_display, parent, false);
        }
        else{
            view = convertView;
        }

        Events event = getItem(position);
        ImageView posterImageView = view.findViewById(R.id.poster);
        TextView titleTextView = view.findViewById(R.id.event_title);
        TextView dateTextView = view.findViewById(R.id.event_date);
        TextView locationTextView = view.findViewById(R.id.event_location);
        TextView organizerTextView = view.findViewById(R.id.event_organizer);
        TextView descriptionTextView = view.findViewById(R.id.event_description);

        assert event != null;
        Glide.with(getContext())
                .load(event.getEventPoster())
                .placeholder(R.drawable.my_event_icon) // Optional placeholder while image loads
                .error(R.drawable.my_event_icon)       // Optional error image if load fails
                .into(posterImageView);                         // Set the ImageView to display the image
        titleTextView.setText(event.getEventTitle());
        dateTextView.setText(event.getEventDate());
        locationTextView.setText(event.getEventLocation());
        organizerTextView.setText(event.getEventOrganizer());
        descriptionTextView.setText(event.getEventDescription());

        return view;
    }
}
