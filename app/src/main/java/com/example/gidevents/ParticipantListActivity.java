package com.example.gidevents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParticipantListActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    String eventID;
    CollectionReference participantRef;
    private ArrayList<String> participantList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_list_page);

        db = FirebaseFirestore.getInstance();

        Events eventDetails = (Events) getIntent().getSerializableExtra("eventDetails");
        eventID = eventDetails.getEventID();

        ParticipantsAdapter adapter = new ParticipantsAdapter(this, participantList);
        listView = findViewById(R.id.organizer_participants_listview);
        listView.setAdapter(adapter);

        participantRef = db.collection("Events").document(eventID).collection("participants");

        participantRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    participantList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String participantID = doc.getId();
                        db.collection("Events").document(eventID).collection("participants").document(participantID).get()
                                .addOnSuccessListener(participant -> {
                                    if (participant.exists()) {
                                        String participantName = participant.getString("username");
                                        Log.d("Firestore", String.format("Participant (%s) fetched", participantName));

                                        participantList.add(participantName);
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Unable to fetch participant"));
                    }
                }

            }

        });

        Button back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(v -> {
            finish();
        });
    }
}
