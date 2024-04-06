package com.example.gidevents;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotifDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif_details_page);

        Button backBtn = (Button) findViewById(R.id.back_button);

        Notification notifDetails = (Notification) getIntent().getSerializableExtra("notifDetails");

        TextView title = findViewById(R.id.event_title);
        TextView description = findViewById(R.id.notif_details);


        title.setText(notifDetails.getEventTitle());
        description.setText(notifDetails.getNotifInfo());

        backBtn.setOnClickListener(v -> {
            finish();
        });
    }
}
