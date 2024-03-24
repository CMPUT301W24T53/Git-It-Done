package com.example.gidevents;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotifActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private CollectionReference notifRef;
    FirebaseFirestore notifDB;
    private NotificationsAdapter notifAdapter;
    private ArrayList<Notification> notifList = new ArrayList<Notification>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif_list_display);
        listView = findViewById(R.id.notif_listview);
        mAuth = FirebaseAuth.getInstance();
        notifDB = FirebaseFirestore.getInstance();
        notifRef = notifDB.collection("Notifications");

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

        notifRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Log.e("NotifDB", error.toString());
                    return;
                }

                if(querySnapshot != null) {
                    notifList.clear();
                    for(QueryDocumentSnapshot doc : querySnapshot) {
                        String eventTitle = doc.getId();
                        String details = doc.getString("details");
                        String notifDate = doc.getString("notifDate");
                        String notifType = doc.getString("notifType");

                        Log.d("NotifDB", String.format("Event: %s fetched", eventTitle));
                        notifList.add(new Notification(eventTitle, details, notifDate, notifType));
                    }
                    notifAdapter.notifyDataSetChanged();
                    Log.d("NotifDB", "NotifAdapter should be updated");
                }
            }
        });



        Button backBtn = (Button) findViewById(R.id.back_button);

        backBtn.setOnClickListener(v -> {
            finish();
        });

    }
}
