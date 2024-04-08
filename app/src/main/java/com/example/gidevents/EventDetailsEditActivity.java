package com.example.gidevents;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class EventDetailsEditActivity extends AppCompatActivity {

    /**
     * This is the activity class for EventDetails page
     * It displays the selected event's details
     * It also allows the user to click on the Sign up button to sign up for the event
     */
    private FirebaseFirestore db;
    CollectionReference checkInRef;
    private ArrayList<Map<String, Object>> checkInsList = new ArrayList<>();
    private ListView listView;

    String eventID;


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
        eventID = eventDetails.getEventID();
        try {


            if (eventDetails != null) {
                TextView title = findViewById(R.id.event_title);
                ImageView poster = findViewById(R.id.poster);
                TextView date = findViewById(R.id.event_date);
                TextView organizer = findViewById(R.id.event_organizer);
                TextView description = findViewById(R.id.event_description);
                TextView location = findViewById(R.id.event_location);


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
                //location.setText(eventDetails.getEventLocation());

                description.setText(eventDetails.getEventDescription());

            }
        } catch (Exception e) {
            Log.e("EventDetailsPageActivity", "Error setting event details", e);
            Toast.makeText(this, "Error displaying event details.", Toast.LENGTH_LONG).show();
        }

        Button back_button = findViewById(R.id.back_button);
        Button view_participant_list = findViewById(R.id.view_participant_list);
        Button event_statisitcs = findViewById(R.id.orgEventAttendeeStats);
        // Check Ined User data displayed in this list view
        listView = findViewById(R.id.check_ins_listView);
        CheckInsAdapter adapter = new CheckInsAdapter(this, checkInsList);
        listView.setAdapter(adapter);

        checkInRef = db.collection("Events").document(eventID).collection("participantsCheckIn");

        checkInRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    checkInsList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String participantID = doc.getId();
                        db.collection("Events").document(eventID).collection("participantsCheckIn").document(participantID).get()
                                .addOnSuccessListener(participant -> {
                                    if (participant.exists()) {
                                        Map<String,Object> data = participant.getData(); // Checked In user data
                                        db.collection("Users").document(participantID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                data.put("username", documentSnapshot.get("Username").toString());// Add Username to Map
                                                checkInsList.add(data);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Firestore", "Failed to Get Checked In User UserName");
                                            }
                                        });


                                        Log.d("Firestore", "this is the data" + data);

                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Unable to fetch Checked In User"));
                    }
                }

            }

        });

        back_button.setOnClickListener(v -> {
            finish();
        });


        view_participant_list.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsEditActivity.this, ParticipantListActivity.class);
            intent.putExtra("eventDetails", eventDetails);
            startActivity(intent);
        });
        event_statisitcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailsEditActivity.this, EventStatisticsActivity.class);
                intent.putExtra("eventDetails", eventDetails);
                startActivity(intent);
            }
        });
    }
}


