package com.example.gidevents;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private ArrayList<Events> filteredList = new ArrayList<>();

    private ArrayList<String> eventTitleList = new ArrayList<>();
    private ListView listView;
    private SearchView searchview;

    /** onCreate method for AdminBrowseEvent
     * Connect to database on create
     * Set up onItemClickListener for listview to get to AdminEventDetails
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


        AdminEventsAdapter adapter = new AdminEventsAdapter(this, eventsList);
        listView.setAdapter(adapter);


//        ArrayAdapter<String> arrayAdapter;
//        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, eventTitleList);
        //listView.setAdapter(arrayAdapter);


        // onItemClickListener, goes to that event's detail page
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AdminBrowseEvent.this, AdminEventDetails.class);
                intent.putExtra("eventDetails", eventsList.get(position));
                startActivity(intent);
            }
        });

        // goes to AdminBrowseEvent page
        Button backBtn = (Button) findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminBrowseEvent.this, AdminActivity.class);
            startActivity(intent);
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
                        String eventPoster = doc.getString("eventPoster");
                        String eventID = doc.getId();
                        Log.d("Firestore", String.format("Event(%s) fetched", eventTitle));
                        eventsList.add(new Events(eventTitle, eventDate, eventTime, location, organizer, eventDescription, eventPoster, eventID));
                        filteredList.add(new Events(eventTitle, eventDate, eventTime, location, organizer, eventDescription, eventPoster, eventID));

                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // HAOZE'S CODE
//        SearchView searchBar = findViewById(R.id.search_bar);
//        adapter.getFilter().filter("");
//        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return true;
//            }
//        });

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //adapter.getFilter().filter(query);
                if(eventTitleList.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(AdminBrowseEvent.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                //adapter.notifyDataSetChanged();
                return false;
            }
        });
        }

}