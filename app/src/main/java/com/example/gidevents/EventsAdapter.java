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
    private List<Events> filteredEvents;
    public EventsAdapter(Context context, List<Events> events) {
        super(context, 0, events);
        this.events =events;
        this.filteredEvents = new ArrayList<>(events);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_display, parent, false);
        }

        Events event = getItem(position);
        ImageView posterImageView = convertView.findViewById(R.id.poster);
        TextView titleTextView = convertView.findViewById(R.id.event_title);
        TextView dateTextView = convertView.findViewById(R.id.event_date);
        TextView locationTextView = convertView.findViewById(R.id.event_location);
        TextView organizerTextView = convertView.findViewById(R.id.event_organizer);
        TextView descriptionTextView = convertView.findViewById(R.id.event_description);

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

        return convertView;
    }
    @Override
    public int getCount() {
        return filteredEvents.size();
    }

    @Override
    public Events getItem(int position) {
        return filteredEvents.get(position);
    }

    @Override
    public Filter getFilter () {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length()==0) {
                    results.values = events;
                } else {
                    List<Events> filteredList = new ArrayList<>();
                    String filteredText = constraint.toString().toLowerCase().trim();
                    for (Events event : events) {
                        if (event.getEventTitle().toLowerCase().trim().contains(filteredText)) {
                            filteredList.add(event);
                        }
                    }
                    results.values = filteredList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredEvents = (List<Events>) results.values;
                notifyDataSetChanged();
            }
        };
    }



}
