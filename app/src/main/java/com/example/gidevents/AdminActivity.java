package com.example.gidevents;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference eventRef;
    ApplicationInfo appInfo;
    private ArrayList<Events> eventsList = new ArrayList<>();
    private ListView listView;

//    public void addEvent(Events events){
//        EventsAdapter.add(events);
//        EventsAdapter.notifyDataSetChanged();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main_1);


        Button backBtn = (Button) findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(intent);
        });

        Button browseEventsBtn = (Button) findViewById(R.id.browse_events_button);
        browseEventsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminBrowseEvent.class);
            startActivity(intent);
        });

//        Button browseProfilesBtn = (Button) findViewById(R.id.browse_profiles_button);
//        browseEventsBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(AdminActivity.this, AdminBrowseProfile.class);
//            startActivity(intent);
//        });
    }
}
