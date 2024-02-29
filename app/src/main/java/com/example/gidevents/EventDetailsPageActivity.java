package com.example.gidevents;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_page);

        Events eventDetails = (Events) getIntent().getSerializableExtra("eventDetails");

        TextView title = findViewById(R.id.event_title);
        ImageView poster = findViewById(R.id.poster);
        TextView date = findViewById(R.id.event_date);
        TextView organizer = findViewById(R.id.event_organizer);
        TextView description = findViewById(R.id.event_description);

        title.setText(eventDetails.getEventTitle());
        poster.setImageResource(eventDetails.getEventPoster());
        date.setText(eventDetails.getEventDate());
        organizer.setText((eventDetails.getEventOrganizer()));
        description.setText(eventDetails.getEventDescription());

    }
}
