package com.example.gidevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This Class defines the overall function for the Attendee Activity
 */
public class AttendeeActivity extends AppCompatActivity {
    /**
     * Runs on creation of the activity and holds most functionality
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
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
            Intent intent = new Intent(AttendeeActivity.this, ScanQRCodeActivity.class);
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
