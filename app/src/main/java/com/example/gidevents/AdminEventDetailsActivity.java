package com.example.gidevents;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AdminEventDetailsActivity extends AppCompatActivity implements DeleteEventFragment.AddCityDialogListener{


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference eventRef;
    ApplicationInfo appInfo;
    private ArrayList<Events> eventsList = new ArrayList<>();
    private String id;
    private String old_title;
    Events eventDetailsNew;


    public void deleteEvent() {

        eventRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {

                for (QueryDocumentSnapshot doc : querySnapshots) {
                    String eventTitle = doc.getString("eventTitle");
                    if (Objects.equals(eventTitle, old_title)) {
                        id = doc.getId();
                    }
                }
                db.collection("Events").document(id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Log.d("Firestore", "City fetched");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Firestore", "Error deleting document", e);
                            }
                        });
//                            pos = adapter.getPosition(eventDetailsNew);
//                            eventsList.remove(pos);
//                            adapter.notifyDataSetChanged();
            }
        });

        Intent intent = new Intent(AdminEventDetailsActivity.this, BrowseEventActivity.class);
        startActivity(intent);
    }

    /** onCreate method for AdminEventDetailsActivity
     * Connect to database on create
     * Set up OnClickListener for delete button which deletes an event
     * Set up OnClickListener for back button to go to BrowseEventActivity page
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_details);

        appInfo = getApplicationInfo();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("Events");


        try {
            Events eventDetails = (Events) getIntent().getSerializableExtra("eventDetails");
            eventDetailsNew = eventDetails;
            // eventDetails same as event_details_new

            if (eventDetails != null) {
                TextView title = findViewById(R.id.event_title);;
                ImageView poster = findViewById(R.id.poster);
                TextView date = findViewById(R.id.event_date);
                TextView organizer = findViewById(R.id.event_organizer);
                TextView description = findViewById(R.id.event_description);

                title.setText(eventDetails.getEventTitle());
                old_title = eventDetails.getEventTitle();

                // Check if the event poster resource exists
                int posterResource = eventDetails.getEventPoster();
                if (isValidResource(posterResource)) {
                    poster.setImageResource(posterResource);
                } else {
                    poster.setImageResource(R.drawable.my_event_icon);
                }
                date.setText(eventDetails.getEventDate());
                organizer.setText(eventDetails.getEventOrganizer());
                description.setText(eventDetails.getEventDescription());
            } else {
                Log.e("EventDetailsPageActivity", "No event details were provided.");
                finish(); // End the activity since there's no data to display
            }
        } catch (Exception e) {
            Log.e("EventDetailsPageActivity", "Error setting event details", e);
            Toast.makeText(this, "Error displaying event details.", Toast.LENGTH_LONG).show();
        }

        // goes to BrowseEventActivity page
        Button backBtn = (Button) findViewById(R.id.back_button);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminEventDetailsActivity.this, BrowseEventActivity.class);
            startActivity(intent);
        });

        // deletes an event
        // goes to BrowseEventActivity page which displays the new event list after deletion
        Button deleteBtn = (Button) findViewById(R.id.delete_event_button);
        deleteBtn.setOnClickListener(v -> {
                    DeleteEventFragment addCityFragment = new DeleteEventFragment();
                    addCityFragment.show(getSupportFragmentManager(), "Delete event");
        });
    }

    // Helper method to check if a resource ID is valid
    private boolean isValidResource(int resId) {
        try {
            // Attempting to obtain the resource will throw if it doesn't exist
            getResources().getResourceName(resId);
            return true;
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }


}
