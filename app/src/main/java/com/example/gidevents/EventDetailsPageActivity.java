package com.example.gidevents;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.gidevents.FirebaseTokenHelper;
import com.google.firebase.messaging.FirebaseMessaging;

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
    private DocumentReference userRef;


    public void subscribeToEventNotifs(String eventID) {
        FirebaseMessaging.getInstance().subscribeToTopic(eventID)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                CharSequence name = "Notifications";
                                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                NotificationChannel channel = new NotificationChannel(eventID, name, importance);

                                NotificationManager notificationManager = (NotificationManager) GlobalContext.context.getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.createNotificationChannel(channel);
                            }

                            Log.d("FCM Notifications", "Subscribed to event: " + eventID);
                        } else {
                            Log.e("FCM Notifications", "Failed to subscribe to event: " + eventID);
                        }
                    }
                });
    }


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
                        //participantSignUp(userID,eventID);
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

    TextView eventTitleDisplay;
    Button signUpConfirm;
    String eventID;

    /**
     * This method implements the display of the signup window after the signUpButton is clicked
     * A dialog is shown with EditText for the user to input his info for the event sign up.
     * Set up an onClickListener for the confirm button
     */
    private void signUpWindow(String userID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_up_window, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        eventID = eventDetails.getEventID();
        eventTitleDisplay = dialogView.findViewById(R.id.eventTitleDisplay);
        eventTitleDisplay.setText(eventDetails.getEventTitle());
        EditText usernameInput = dialogView.findViewById(R.id.usernameInput);
        EditText emailInput = dialogView.findViewById(R.id.emailInput);
        EditText phoneNumberInput = dialogView.findViewById(R.id.phoneNumberInput);


        signUpConfirm = dialogView.findViewById(R.id.confirmButton);
        signUpConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef = db.collection("Users").document(userID);
                userRef.get()
                        .addOnSuccessListener(user -> {
                            if (user.exists()) {
                                String fcmToken = user.getString("fcmToken");

                                String username = usernameInput.getText().toString();
                                String email = emailInput.getText().toString();
                                String phoneNumber = phoneNumberInput.getText().toString();
                                addEventToMyEvents(eventID);


                                subscribeToEventNotifs(eventID);

                                participantSignUp(userID, username, email, phoneNumber, fcmToken);
                            }
                        });

                dialog.dismiss();
            }
        });
        dialog.show();
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

    private void participantSignUp(String userID, String username, String email, String phoneNumber, String fcmToken) {

        Map<String, Object> newParticipant = new HashMap<>();
        newParticipant.put("username", username);
        newParticipant.put("email", email);
        newParticipant.put("phoneNumber", phoneNumber);
        newParticipant.put("fcmToken", fcmToken);

        db.collection("Events").document(eventID).collection("participants").document(userID)
                .set(newParticipant)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Participant added with userID: " + userID))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding participant", e));
    }
}
