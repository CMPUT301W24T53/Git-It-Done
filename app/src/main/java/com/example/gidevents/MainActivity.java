package com.example.gidevents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button attendeeBtn = (Button) findViewById(R.id.attendeeButton);
        Button organizerBtn = (Button) findViewById(R.id.organizerButton);
        Button administratorBtn = (Button) findViewById(R.id.administratorButton);

        attendeeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AttendeeActivity.class);
            startActivity(intent);

        });

        organizerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrganizerActivity.class);
            startActivity(intent);

        });

        administratorBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdministratorActivity.class);
            startActivity(intent);
        });

    }

}