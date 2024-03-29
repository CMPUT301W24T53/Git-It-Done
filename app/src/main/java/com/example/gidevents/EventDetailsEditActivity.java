package com.example.gidevents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;


public class EventDetailsEditActivity extends AppCompatActivity {

    /**
     * This is the activity class for EventDetails page
     * It displays the selected event's details
     * It also allows the user to click on the Sign up button to sign up for the event
     */
    private FirebaseFirestore db;


    /**
     * Initialize the views, set up an onClickListener for the signUpButton
     *
     * @param savedInstanceState being shut down then this Bundle contains the data it most
     *                 recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_edit_page);
        db = FirebaseFirestore.getInstance();
        Events eventDetails = (Events) getIntent().getSerializableExtra("eventDetails");

        try {


            if (eventDetails != null) {
                TextView title = findViewById(R.id.event_title);
                ImageView poster = findViewById(R.id.poster);
                TextView date = findViewById(R.id.event_date);
                TextView organizer = findViewById(R.id.event_organizer);
                TextView description = findViewById(R.id.event_description);

                title.setText(eventDetails.getEventTitle());
                String posterUrl = eventDetails.getEventPoster();
                // Check if the event poster resource exists.
                Glide.with(this)
                        .load(posterUrl)
                        .placeholder(R.drawable.my_event_icon) // Optional placeholder while image loads
                        .error(R.drawable.my_event_icon)       // Optional error image if load fails
                        .into(poster);                         // Set the ImageView to display the image

                date.setText(eventDetails.getEventDate());
                organizer.setText(eventDetails.getEventOrganizer());
                description.setText(eventDetails.getEventDescription());

            }
        } catch (Exception e) {
            Log.e("EventDetailsPageActivity", "Error setting event details", e);
            Toast.makeText(this, "Error displaying event details.", Toast.LENGTH_LONG).show();
        }

        Button back_button = findViewById(R.id.back_button);
        Button edit_details_button = findViewById(R.id.edit_button);
        Button view_participant_list = findViewById(R.id.view_participant_list);

        back_button.setOnClickListener(v -> {
            finish();
        });

        edit_details_button.setOnClickListener(v -> {
            finish();       // Implement actual event detail editing
        });

        view_participant_list.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsEditActivity.this, ParticipantListActivity.class);
            intent.putExtra("eventDetails", eventDetails);
            startActivity(intent);
        });
    }
}



