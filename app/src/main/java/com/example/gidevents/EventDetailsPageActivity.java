package com.example.gidevents;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_page);

        try {
            Events eventDetails = (Events) getIntent().getSerializableExtra("eventDetails");

            if (eventDetails != null) {
                TextView title = findViewById(R.id.event_title);
                ImageView poster = findViewById(R.id.poster);
                TextView date = findViewById(R.id.event_date);
                TextView organizer = findViewById(R.id.event_organizer);
                TextView description = findViewById(R.id.event_description);

                title.setText(eventDetails.getEventTitle());
                // Check if the event poster resource exists
                int posterResource = eventDetails.getEventPoster();
                if (isValidResource(posterResource)) {
                    poster.setImageResource(posterResource);
                } else {
                    poster.setImageResource(R.drawable.my_event_icon);
                }
                date.setText(eventDetails.getEventDate());
                organizer.setText(eventDetails.getEventOrganizer());
                description.setText(eventDetails.getEventDescription());
            } else {
                Log.e("EventDetailsPageActivity", "No event details were provided.");
                finish(); // End the activity since there's no data to display
            }
        } catch (Exception e) {
            Log.e("EventDetailsPageActivity", "Error setting event details", e);
            Toast.makeText(this, "Error displaying event details.", Toast.LENGTH_LONG).show();
        }
    }

    // Helper method to check if a resource ID is valid
    private boolean isValidResource(int resId) {
        try {
            // Attempting to obtain the resource will throw if it doesn't exist
            getResources().getResourceName(resId);
            return true;
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }
}