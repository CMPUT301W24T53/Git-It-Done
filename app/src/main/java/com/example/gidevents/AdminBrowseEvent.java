package com.example.gidevents;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/** This is the activity class for BrowseEvents
 * It displays the browse events page, listing every events in the database for the user to see
 * Once clicked on an event, go to that event's details page
 */

public class AdminBrowseEvent extends AppCompatActivity {
    ApplicationInfo appInfo;
    private ArrayList<Events> eventsList = new ArrayList<>();
    private ArrayList<String> eventTitleList = new ArrayList<>();
    private ListView listView;
    private SearchView searchview;

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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventRef = db.collection("Events");
        searchview = findViewById(R.id.search_bar);

        EventsAdapter adapter = new EventsAdapter(this, eventsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AdminBrowseEvent.this, AdminEventDetailsActivity.class);
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
                        eventTitleList.add(eventTitle);
                        String eventDate = doc.getString("eventDate");
                        String eventTime = doc.getString("eventTime");
                        String location = doc.getString("location");
                        String organizer = doc.getString("organizer");
                        String eventDescription = doc.getString("eventDescription");

                        String eventID = doc.getId();
                        Log.d("Firestore", String.format("Event(%s) fetched", eventTitle));
                        eventsList.add(new Events(eventTitle, eventDate, eventTime, location, organizer, eventDescription, R.drawable.poster1, eventID));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


//        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                AdminBrowseEvent.this.eventTitleList.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
    }

}