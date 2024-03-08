package com.example.gidevents;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
    private ArrayList<Notifications> notifList = new ArrayList<>();
    private ListView listView;
    ApplicationInfo appInfo;
    NotificationsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif_list_display);
        listView = findViewById(R.id.notif_listview);
        appInfo = getApplicationInfo();


        Notifications notif1 = new Notifications("Title 1", "Notif info 1", "2024-1-1");
        Notifications notif2 = new Notifications("Title 2", "Notif info 2", "2024-2-3");
        Notifications notif3 = new Notifications("Title 3", "Notif info 3", "2024-2-3");


        notifList.add(notif1);
        notifList.add(notif2);
        notifList.add(notif3);

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

    }
}
