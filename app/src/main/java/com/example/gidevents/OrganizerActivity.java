package com.example.gidevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OrganizerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_main);

        Button checkInBtn = (Button) findViewById(R.id.checkInButton);
        Button enrolledEventsBtn = (Button) findViewById(R.id.enrolledEventsButton);
        Button notifBtn = (Button) findViewById(R.id.notificationButton);
        Button profileEditBtn = (Button) findViewById(R.id.profileEditButton);
        Button browseEventsBtn = (Button) findViewById(R.id.browseEventsButton);

        checkInBtn.setOnClickListener(v -> {
            // TODO: implement activities for organizer
        });

        enrolledEventsBtn.setOnClickListener(v -> {
            // TODO: implement activities for organizer
        });

        notifBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerActivity.this, OrgNotifActivity.class);
            startActivity(intent);
        });

        profileEditBtn.setOnClickListener(v -> {
            // TODO: implement activities for organizer
        });

        browseEventsBtn.setOnClickListener(v -> {
            // TODO: implement activities for organizer
        });
    }


    /**
     * add all these into your organizerActivity when you try to merge.
     */
    private Button btnNewEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        btnNewEvent = findViewById(R.id.btnNewEvent);

        btnNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CreateEventActivity
                Intent intent = new Intent(OrganizerActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });
    }


}

