package com.example.gidevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OrganizerActivity extends AppCompatActivity {


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

