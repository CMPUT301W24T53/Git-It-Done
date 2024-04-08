package com.example.gidevents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrganizerActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<Events> eventsList = new ArrayList<>();
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        listView = findViewById(R.id.organizer_events_listview);
        EventsAdapter adapter = new EventsAdapter(this, eventsList);
        listView.setAdapter(adapter);
        /**
         * add all these into your organizerActivity when you try to merge.
         */
        Button btnNewEvent;
        btnNewEvent = findViewById(R.id.btnNewEvent);

        db.collection("Events").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if(querySnapshots != null) {
                    eventsList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String eventID = doc.getId();
                        db.collection("Users").document(userID).collection("CreatedEvents").document(eventID).get()
                                .addOnSuccessListener(createdEvent -> {
                                    if (createdEvent.exists()) {
                                        String eventTitle = doc.getString("eventTitle");
                                        String eventDate = doc.getString("eventDate");
                                        String eventTime = doc.getString("eventTime");
                                        String organizer = doc.getString("organizer");
                                        String location = doc.getString("location");
                                        String eventDescription = doc.getString("eventDescription");
                                        String eventPoster = doc.getString("eventPoster");
                                        Log.d("Firestore", String.format("Event(%s) fetched", eventTitle));

                                        eventsList.add(new Events(eventTitle, eventDate, eventTime, location, organizer, eventDescription, eventPoster, eventID));

                                        adapter.notifyDataSetChanged();

                                        Log.d("events list", "this is events list" + eventsList);
                                    }
                                });
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OrganizerActivity.this, EventDetailsEditActivity.class);
                intent.putExtra("eventDetails", eventsList.get(position));
                startActivity(intent);
            }
        });

        btnNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CreateEventActivity
                Intent intent = new Intent(OrganizerActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });

    }
}

