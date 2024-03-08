package com.example.gidevents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
    private ArrayList<Notification> notifList = new ArrayList<Notification>();
    private ListView listView;
    ApplicationInfo appInfo;
    NotificationsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif_list_display);
        listView = findViewById(R.id.notif_listview);
        appInfo = getApplicationInfo();

        Button backBtn = (Button) findViewById(R.id.back_button);

        // Placeholder notifications
        notifList.add(new Notification("Title 1", "Notif info 1", "2024-1-1"));
        notifList.add(new Notification("Title 2", "Notif info 2", "2024-2-3"));
        notifList.add(new Notification("Title 3", "Notif info 3", "2024-2-3"));

        adapter = new NotificationsAdapter(this, notifList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NotificationActivity.this, NotifDetailsActivity.class);
                intent.putExtra("notifDetails", notifList.get(position));
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(v -> {
            finish();
        });

    }
}
