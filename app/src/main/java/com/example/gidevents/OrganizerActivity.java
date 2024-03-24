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
        setContentView(R.layout.activity_organizer);

    /**
     * add all these into your organizerActivity when you try to merge.
     */
        Button btnNewEvent;


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

