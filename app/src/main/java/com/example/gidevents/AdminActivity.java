package com.example.gidevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This Class defines the overall function for the Administrator Activity
 */
public class AdminActivity extends AppCompatActivity {

    /**
     * Runs on creation of the activity and holds most functionality
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);


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

        Button browseProfileButton = (Button) findViewById(R.id.browse_profiles_button);
        browseProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminBrowseProfilesActivity.class);
            startActivity(intent);

        });

//        Button browseProfilesBtn = (Button) findViewById(R.id.browse_profiles_button);
//        browseEventsBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(AdminActivity.this, AdminBrowseProfile.class);
//            startActivity(intent);
//        });
    }
}
