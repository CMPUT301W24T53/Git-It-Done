package com.example.gidevents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ApplicationInfo appInfo;
    private ArrayList<Events> eventsList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browze_events_page);
        listView = findViewById(R.id.browze_events_listview);
        appInfo = getApplicationInfo();

        Events event1 = new Events("Title 1", "2024-5-3", "Organizer 1", "Event Description goes here......", R.drawable.poster1);
        Events event2 = new Events("Title 2", "2024-4-3", "Organizer 2", "Event Description goes here......",R.drawable.ic_launcher_foreground);
        Events event3 = new Events("Title 3", "2024-6-3", "Organizer 3", "Event Description goes here......",R.drawable.ic_launcher_background);
        eventsList.add(event1);
        eventsList.add(event2);
        eventsList.add(event3);

        EventsAdapter adapter = new EventsAdapter(this, eventsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EventDetailsPageActivity.class);
                intent.putExtra("eventDetails", eventsList.get(position));
                startActivity(intent);
            }
        });

    }

}