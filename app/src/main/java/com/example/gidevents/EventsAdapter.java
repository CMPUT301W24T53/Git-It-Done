package com.example.gidevents;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class EventsAdapter extends ArrayAdapter<Events> {
    public EventsAdapter(Context context, List<Events> events) {
        super(context, 0, events);
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
        TextView organizerTextView = convertView.findViewById(R.id.event_organizer);
        Log.d("EventsAdapter", "Organizer for position " + position + ": " + event.getEventOrganizer());
        TextView descriptionTextView = convertView.findViewById(R.id.event_description);

        assert event != null;
        posterImageView.setImageResource(event.getEventPoster());

        titleTextView.setText(event.getEventTitle());
        dateTextView.setText(event.getEventDate());
        organizerTextView.setText(event.getEventOrganizer());
        descriptionTextView.setText(event.getEventDescription());

        return convertView;
    }

}
