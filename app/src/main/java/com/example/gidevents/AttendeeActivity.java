package com.example.gidevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AttendeeActivity extends AppCompatActivity {
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
            Intent intent = new Intent(AttendeeActivity.this, NotificationActivity.class);
            startActivity(intent);

        });

        enrolledEventsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AttendeeActivity.this, EnrolledEventsActivity.class);
            startActivity(intent);

        });

        notifBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AttendeeActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        profileEditBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AttendeeActivity.this, ProfileEditActivity.class);
            startActivity(intent);

        });

        browseEventsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AttendeeActivity.this, BrowseEventActivity.class);
            startActivity(intent);
        });
    }
}
