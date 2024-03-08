package com.example.gidevents;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class BrowseEventActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference eventRef;
    ApplicationInfo appInfo;
    private ArrayList<Events> eventsList = new ArrayList<>();
    private ListView listView;

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BrowseEventActivity.this, EventDetailsPageActivity.class);
                intent.putExtra("eventDetails", eventsList.get(position));
                startActivity(intent);
            }
        });

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
                        String organizer = doc.getString("organizer");
                        String eventDescription = doc.getString("eventDescription");
                        Log.d("Firestore", String.format("Event(%s) fetched", eventTitle));
                        eventsList.add(new Events(eventTitle, eventDate, organizer, eventDescription, R.drawable.poster1));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

}