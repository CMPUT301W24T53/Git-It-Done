package com.example.gidevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;

import java.util.ArrayList;
import java.util.List;
import android.widget.Filter;
import android.widget.Filterable;

public class AdminEventsAdapter extends ArrayAdapter implements Filterable {

    public Iterables getFilter;
    private List<Events> events;
    private ArrayList<Events> filteredEvents;
    private Filter eventsFilter;

    public AdminEventsAdapter(Context context, List<Events> events) {
        super(context, 0, events);
        //this.events = events;
        //this.filteredEvents = new ArrayList<>(events);
        this.events = new ArrayList<>(events);
        this.filteredEvents = new ArrayList<>(events);
        this.eventsFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Events> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(events);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Events item : events) {
                        // You can adjust these conditions to match any field you want to include in the search
                        if (item.getEventTitle().toLowerCase().contains(filterPattern) ||
                                item.getLocation().toLowerCase().contains(filterPattern) ||
                                item.getEventDate().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredEvents.clear();
                filteredEvents.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_display, parent, false);
        }

        Events event = (Events) getItem(position);
        ImageView posterImageView = convertView.findViewById(R.id.poster);
        TextView titleTextView = convertView.findViewById(R.id.event_title);
        TextView dateTextView = convertView.findViewById(R.id.event_date);
        TextView timeTextView = convertView.findViewById(R.id.event_time);
        TextView locationTextView = convertView.findViewById(R.id.event_location);
        TextView organizerTextView = convertView.findViewById(R.id.event_organizer);
        TextView descriptionTextView = convertView.findViewById(R.id.event_description);

        assert event != null;
        posterImageView.setImageResource(event.getEventPoster());

        titleTextView.setText(event.getEventTitle());
        dateTextView.setText(event.getEventDate());
        timeTextView.setText(event.getEventTime());
        locationTextView.setText(event.getLocation());
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

    @NonNull
    @Override
    public Filter getFilter() {
        return eventsFilter;
    }

//    @Override
//    public int getCount() {
//        return filteredEvents.size();
//    }
//
//    @Override
//    public Events getItem(int position) {
//        return filteredEvents.get(position);
//    }
//
//    public void filter(String text) {
//        text = text.toLowerCase();
//        filteredEvents.clear();
//        if (text.length() == 0) {
//            filteredEvents.addAll(events);
//        } else {
//            for (Events item : events) {
//                if (item.getEventTitle().toLowerCase().contains(text) ||
//                        item.getEventDate().toLowerCase().contains(text) ||
//                        item.getLocation().toLowerCase().contains(text)) {
//                    filteredEvents.add(item);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
}
