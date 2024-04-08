package com.example.gidevents;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

//ToDo: Implement Notifications
/**
 * Runs the Activity for seeing a map of all check in locations for an event, and shows how many people are currently checked in to the event.
 */
public class EventStatisticsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Events eventDetails;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private QuerySnapshot Participants;
    private DocumentReference eventDb;
    private ArrayList<ArrayList<Double>> locations;
    private long numCheckIns;
    private TextView checkins;
    private Button backbttn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_statistics_page);
        checkins = findViewById(R.id.eventStatsTextStat);
        backbttn = findViewById(R.id.event_stats_back_button);
        eventDetails = (Events) getIntent().getSerializableExtra("eventDetails");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        locations = new ArrayList<>();
        eventDb = db.collection("Events").document(eventDetails.getEventID());
        eventDb.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot event = task.getResult();
                if (event.get("numCheckIns") != null) {
                    numCheckIns = event.getLong("numCheckIns");
                }
                else {
                    numCheckIns = 0;
                }
            }
        });
        eventDb.collection("participantsCheckIn").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Participants = task.getResult();
                for (QueryDocumentSnapshot document : Participants){
                    if (document.get("geoLocationLat") != null & document.get("geoLocationLon") != null) {
                        ArrayList<Double> location = new ArrayList<>();
                        location.add((Double) document.get("geoLocationLat"));
                        location.add((Double) document.get("geoLocationLon"));
                        locations.add(location);
                        numCheckIns += 1;
                    }
                }
                HashMap<String, Object> data = new HashMap<>();
                data.put("numCheckIns", numCheckIns);
                eventDb.set(data, SetOptions.merge());
                String text = " " + numCheckIns + " ";
                checkins.setText(text);
            }
        });
        backbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        float latTotal = 0;
        float lonTotal = 0;
        float locTotal = locations.size();

        for(ArrayList<Double> location : locations){
            latTotal += location.get(0);
            lonTotal += location.get(1);
            LatLng marker = new LatLng(location.get(0), location.get(1));
            mMap.addMarker(new MarkerOptions().position(marker));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latTotal/locTotal,lonTotal/locTotal)));
    }
}