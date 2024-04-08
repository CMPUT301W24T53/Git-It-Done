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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
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
        CollectionReference createdRef = db.collection("Users").document(userID).collection("CreatedEvents");
        CollectionReference eventRef = db.collection("Events");
        /**
         * add all these into your organizerActivity when you try to merge.
         */
        Button btnNewEvent;
        btnNewEvent = findViewById(R.id.btnNewEvent);
        Button backBtn = findViewById(R.id.backBtn);

        createdRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if(querySnapshots != null) {
                    adapter.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String eventID = doc.getId();
                        eventRef.document(eventID).get()
                                .addOnSuccessListener(createdEvent -> {
                                    if (createdEvent.exists()) {
                                        String eventTitle = createdEvent.getString("eventTitle");
                                        String eventDate = createdEvent.getString("eventDate");
                                        String organizer = createdEvent.getString("eventOrganizer");
                                        String location = createdEvent.getString("eventLocation");
                                        String eventDescription = createdEvent.getString("eventDescription");
                                        String eventPoster = createdEvent.getString("eventPoster");

                                        Log.d("Firestore", String.format("Event(%s) fetched", eventTitle));

                                        adapter.add(new Events(eventTitle, eventDate, location, organizer, eventDescription, eventPoster, eventID));

                                    }
                                });
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        adapter.getFilter().filter("");





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OrganizerActivity.this, EventDetailsEditActivity.class);
                intent.putExtra("eventDetails", eventsList.get(position));
                startActivity(intent);
            }
        });

        btnNewEvent.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}

