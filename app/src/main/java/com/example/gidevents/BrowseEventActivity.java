
package com.example.gidevents;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;

/** This is the activity class for BrowseEvents
 * It displays the browse events page, listing every events in the database for the user to see
 * Once clicked on an event, go to that event's details page
 */
public class BrowseEventActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference eventRef;
    ApplicationInfo appInfo;
    private ArrayList<Events> eventsList = new ArrayList<>();
    private ListView listView;

    /** onCreate method for BrowseEventActivity
     * Connect to database on create
     * Set up onItemClickListener for listview to get to EventDetailsPage
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browze_events_page);
        listView = findViewById(R.id.browze_events_listview);
        appInfo = getApplicationInfo();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("Events");


        EventsAdapter adapter = new EventsAdapter(this, eventsList);
        listView.setAdapter(adapter);
        Button backBtn = (Button) findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> {
            finish();
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BrowseEventActivity.this, EventDetailsPageActivity.class);
                intent.putExtra("eventDetails", eventsList.get(position));
                startActivity(intent);
            }
        });
        //Set up Snapshot listener, populate listview with events in database
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
                        String eventTitle = doc.getString("eventTitle");
                        String eventDate = doc.getString("eventDate");
                        String organizer = doc.getString("evenOrganizer");
                        String eventDescription = doc.getString("eventDescription");
                        String eventID = doc.getId();
                        String time= doc.getString("eventTime");
                        String location= doc.getString("eventLocation");
                        String eventPoster = doc.getString("eventPoster");
                        Log.d("Firestore", String.format("Event(%s) fetched", eventTitle));
                        eventsList.add(new Events(eventTitle, eventDate, organizer,time, location, eventDescription, eventPoster, eventID));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

}

