package com.example.gidevents;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
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

public class NotifActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String userID;
    private CollectionReference notifRef;
    private CollectionReference userEventsRef;
    FirebaseFirestore db;
    private NotificationsAdapter notifAdapter;
    private ArrayList<Notification> notifList = new ArrayList<Notification>();
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif_list_display);
        listView = findViewById(R.id.notif_listview);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        notifRef = db.collection("Notifications");
        userEventsRef = db.collection("Users").document(userID).collection("myEvents");

        notifAdapter = new NotificationsAdapter(this, notifList);
        listView.setAdapter(notifAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NotifActivity.this, NotifDetailsActivity.class);
                intent.putExtra("notifDetails", notifList.get(position));
                startActivity(intent);
            }
        });

        notifRef.addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.e("Firestore Notifications", "Error fetching notifications: " + error.getMessage());
                return;
            }

            if (querySnapshot != null) {
                notifList.clear(); // Clear existing notifications

                for (DocumentSnapshot doc : querySnapshot) {
                    String eventID = doc.getString("eventID");
                    String eventTitle = doc.getString("eventTitle"); // Ensure field names are correct
                    String details = doc.getString("details");

                    if (eventID != null && eventTitle != null && details != null) {
                        notifList.add(new Notification(eventTitle, details));
                    }
                }

                // Update the UI after fetching notifications
                notifAdapter.notifyDataSetChanged();
                Log.d("Firestore Notifications", "Notifications updated");
            }
        });

        Button backBtn = (Button) findViewById(R.id.back_button);

        backBtn.setOnClickListener(v -> {
            finish();
        });

    }
}
