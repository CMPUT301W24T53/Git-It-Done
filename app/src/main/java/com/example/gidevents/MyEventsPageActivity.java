package com.example.gidevents;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.ListView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This is the class for MyEvents page, it displays all the events that the current user signed up for
 * It is the same format as the BrowseEventsPage
 */
public class MyEventsPageActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference eventRef;
    private ArrayList<Events> eventsList = new ArrayList<>();
    private ListView listView;
    private TextView MyEventsText;

    /**
     * onCreate method of the MyEventsPage Activity
     * Added code to check which events are in the current users MyEvents collection
     * display those events in MyEvents collection, since those are what the user signed up to attend
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browze_events_page);
        listView = findViewById(R.id.browze_events_listview);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("Events");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        MyEventsText = findViewById(R.id.browze_events_text);
        MyEventsText.setText("My Events");
        Button back_button =  findViewById(R.id.back_button);


        EventsAdapter adapter = new EventsAdapter(this, eventsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyEventsPageActivity.this, EventDetailsPageActivity.class);
                intent.putExtra("eventDetails", eventsList.get(position));
                startActivity(intent);
            }
        });

        //Set up Snapshot listener, populate listview with events in MyEvents
        String userID = currentUser.getUid();
        eventRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    eventsList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String eventID = doc.getId();
                        db.collection("Users").document(userID).collection("MyEvents").document(eventID).get()
                                .addOnSuccessListener(myEvent -> {
                                    if (myEvent.exists()) {
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
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Unable to fetch event"));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        SearchView searchBar = findViewById(R.id.search_bar);
        adapter.getFilter().filter("");
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        back_button.setOnClickListener(v -> {
            finish();
        });
    }
}
