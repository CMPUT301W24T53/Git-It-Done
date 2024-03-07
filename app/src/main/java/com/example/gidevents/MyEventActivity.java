package com.example.gidevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MyEventActivity extends AppCompatActivity {
    private ArrayList<String> eventList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);

        // Get a reference to the ListView and set the adapter
        ListView listView = findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventList);
        listView.setAdapter(adapter);

        // Get the data passed by the Intent
        Intent intent = getIntent();
        String qrcodeData = intent.getStringExtra("qrcode_data");
        if (qrcodeData != null) {
            addEventToList(qrcodeData);
        }
    }

    private void addEventToList(String eventData) {
        // get current time
        String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());

        String eventEntry = eventData + " - " + currentDateTime;
        eventList.add(eventEntry);

        adapter.notifyDataSetChanged();
    }
}