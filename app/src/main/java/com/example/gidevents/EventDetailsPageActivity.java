package com.example.gidevents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;

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

    /**
     * Initialize the views, set up an onClickListener for the signUpButton
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_page);
        db = FirebaseFirestore.getInstance();

        eventDetails = (Events) getIntent().getSerializableExtra("eventDetails");

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

        signUpButton = findViewById(R.id.sign_up_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpWindow();
            }
        });
    }
    TextView eventTitleDisplay;
    Button signUpConfirm;
    String eventID;

    /**
     * This method implements the display of the signup window after the signUpButton is clicked
     * A dialog is shown with EditText for the user to input his info for the event sign up.
     * Set up an onClickListener for the confirm button
     */
    private void signUpWindow() {
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
                String username = usernameInput.getText().toString();
                String email = emailInput.getText().toString();
                String phoneNumber = phoneNumberInput.getText().toString();

                participantSignUp(username, email, phoneNumber);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * This method complete the user event sign up, sends the user info to the database
     * Once the confirm button is clicked, the inputs from the EditTexts are read and send
     * to the database, to the participants collection under the selected event
     * @param username is the user input username for sign up
     * @param email is the user input email for sign up
     * @param phoneNumber is the user input phoneNumber for sign up
     */
    private void participantSignUp(String username, String email, String phoneNumber) {
        Map<String, Object> newParticipant = new HashMap<>();
        newParticipant.put("username", username);
        newParticipant.put("email", email);
        newParticipant.put("phoneNumber", phoneNumber);

        db.collection("Events").document(eventID).collection("participants")
                .add(newParticipant)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String participantID = documentReference.getId();
                        Log.d("Firestore", "New participant added with ID: " +eventID);
                    }
                });

    }
}
