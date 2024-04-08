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


import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** This is the EventsAdapter class
 * Sets up the adapter
 */
public class AdminEventsAdapter extends ArrayAdapter<Events> implements Filterable {

    private List<Events> eventsList;
    private List<Events> filteredList;
    private Context context;
    private FirebaseFirestore db;
    private DocumentReference userRef;


    public AdminEventsAdapter(Context context, List<Events> events) {
        super(context, 0, events);
        this.context = context;
        this.eventsList = new ArrayList<>(events);
        this.filteredList = new ArrayList<>(events);

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
        TextView timeTextView = convertView.findViewById(R.id.event_time);
        TextView locationTextView = convertView.findViewById(R.id.event_location);
        TextView organizerTextView = convertView.findViewById(R.id.event_organizer);
        TextView descriptionTextView = convertView.findViewById(R.id.event_description);

        assert event != null;

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Events").document(event.getEventTitle());


        titleTextView.setText(event.getEventTitle());
        dateTextView.setText(event.getEventDate());
        timeTextView.setText(event.getEventTime());
        locationTextView.setText(event.getEventLocation());
        organizerTextView.setText(event.getEventOrganizer());
        descriptionTextView.setText(event.getEventDescription());

        return convertView;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Events getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
