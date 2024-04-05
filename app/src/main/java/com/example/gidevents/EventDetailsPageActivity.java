package com.example.gidevents;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the activity class for EventDetails page
 * It displays the selected event's details
 * It also allows the user to click on the Sign up button to sign up for the event
 */
public class EventDetailsPageActivity extends AppCompatActivity {
    Button signUpButton;
    Events eventDetails;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String userID;



    /**
     * Initialize the views, set up an onClickListener for the signUpButton
     *
     * @param savedInstanceState being shut down then this Bundle contains the data it most
     *                 recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_page);
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
           userID = user.getUid();
        }


        eventDetails = (Events) getIntent().getSerializableExtra("eventDetails");


        try {
            Events eventDetails = (Events) getIntent().getSerializableExtra("eventDetails");

            if (eventDetails != null) {
                TextView title = findViewById(R.id.event_title);
                ImageView poster = findViewById(R.id.poster);
                TextView date = findViewById(R.id.event_date);
                TextView organizer = findViewById(R.id.event_organizer);
                TextView location = findViewById(R.id.event_location);
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
                location.setText(eventDetails.getEventLocation());
                description.setText(eventDetails.getEventDescription());

                signUpButton = findViewById(R.id.sign_up_button);
                Button backBtn = (Button) findViewById(R.id.back_button);
                backBtn.setOnClickListener(v -> {
                    finish();
                });
                String eventID = eventDetails.getEventID();
                signUpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addEventToMyEvents(eventID);
                        participantSignUp(userID,eventID);
                        Toast.makeText(getApplicationContext(), "Sign up successful", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e("EventDetailsPageActivity", "No event details were provided.");
                finish(); // End the activity since there's no data to display
            }
        } catch (Exception e) {
            Log.e("EventDetailsPageActivity", "Error setting event details", e);
            Toast.makeText(this, "Error displaying event details.", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Checks if the given resource identifier is valid by attempting to retrieve the resource name.
     * If the resource name is not found, it means the resource identifier is invalid.
     *
     * @param resId The resource identifier to validate.
     * @return True if the resource identifier is valid, false otherwise.
     */
    private boolean isValidResource(int resId) {
        try {
            // Attempting to obtain the resource will throw if it doesn't exist
            getResources().getResourceName(resId);
            return true;
        } catch (Resources.NotFoundException e) {
            return false;
        }

    }


    /**
     * This method now add the eventID of the current event to the User's MyEvents collection
     * @param eventID the ID of the current event
     */
    private void addEventToMyEvents (String eventID) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser!= null) {
            String userID = currentUser.getUid();
            Map <String, Object> event = new HashMap<>();
            event.put ("eventID", eventID);
            db.collection("Users").document(userID).collection("MyEvents").document(eventID)
                    .set(event)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("EventSignUp", "EventID added to MyEvents");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("EventSignUp", "Failed to add EventID to MyEvents" + e);
                    });
        } else {
            Log.d("EventSignUp", "No User found");
        }

    }



    /**
     * This method complete the user event sign up, sends the user info to the database
     * Add the user ID to the "participants" collection under the event that the user is signing up
     */
    private void participantSignUp(String userID, String eventID) {
        Map<String, Object> emptyData = new HashMap<>();
        db.collection("Events").document(eventID).collection("participants").document(userID)
                .set(emptyData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Participant added with userID: " + userID))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding participant", e));
    }
}
