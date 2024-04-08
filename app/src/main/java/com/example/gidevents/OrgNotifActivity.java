package com.example.gidevents;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class OrgNotifActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private CollectionReference notifRef;
    FirebaseFirestore notifDB;
    private NotificationsAdapter notifAdapter;
    private ArrayList<Notification> notifList = new ArrayList<Notification>();
    private ListView listView;
    private String nTitle;
    private String nDetails;

    // For handling sending notifications to users
    private static final String CHANNEL_ID = "Notifications";
    private static final int MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS_SERVICE = 1001;

    /**
     * Method to set up notification channel for push notifications
     * Note: Must be called prior to calling sendPushNotif
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     *  Sends a push notification to all users (will likely allow a toggle for
     *  users to opt out of push notifications as well as select certain
     *  types of push notifications they would like to receive
     *  Note: Must be called after createNotificationChannel
     * @param title Title of the notification
     * @param details The details and content of the notification
     */
    public void sendPushNotif(String title, String details) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_event);
        builder.setContentTitle(title);
        builder.setContentText(details);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS_SERVICE);
            return;
        }
        managerCompat.notify(1, builder.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_notif_page);
        listView = findViewById(R.id.notif_listview);
        mAuth = FirebaseAuth.getInstance();
        notifDB = FirebaseFirestore.getInstance();

        notifRef = notifDB.collection("Notifications");
        FirebaseApp.initializeApp(this);
        createNotificationChannel();

        notifAdapter = new NotificationsAdapter(this, notifList);
        listView.setAdapter(notifAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OrgNotifActivity.this, NotifDetailsActivity.class);
                intent.putExtra("notifDetails", notifList.get(position));
                startActivity(intent);
            }
        });

        notifRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Log.e("NotifDB", error.toString());
                    return;
                }

                if(querySnapshot != null) {
                    notifList.clear();
                    for(QueryDocumentSnapshot doc : querySnapshot) {
                        String eventTitle = doc.getId();
                        String details = doc.getString("details");
                        String notifDate = doc.getString("notifDate");
                        String notifType = doc.getString("notifType");

                        Log.d("NotifDB", String.format("Event: %s fetched", eventTitle));
                        notifList.add(new Notification(eventTitle, details, notifDate, notifType));
                    }
                    notifAdapter.notifyDataSetChanged();
                    Log.d("NotifDB", "NotifAdapter should be updated");
                }
            }
        });

        Button backBtn = (Button) findViewById(R.id.back_button);
        Button newNotif = (Button) findViewById(R.id.notif_button);

        backBtn.setOnClickListener(v -> {
            finish();
        });
        newNotif.setOnClickListener(v -> {
            newNotif(findViewById(R.id.notif_button));
        });
    }

    /**
     * Handles the popup window and allows organizer to create new notifications
     * Automatically send push notification when confirm is pressed, will likely
     * change to ensure all desired content is entered into notification
     * Also will likely change to disable confirm button until both text fields have
     * text content in them
     * @param view
     */
    public void newNotif(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.organizer_notif_create, null);
        EditText notifTitle = popupView.findViewById(R.id.orgEventTitle);
        EditText notifDetails = popupView.findViewById(R.id.orgNotifDetails);

        RelativeLayout.LayoutParams notifTitleParams = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        RelativeLayout.LayoutParams notifDetailParams = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        notifTitleParams.setMargins(50,150,50,0);
        notifDetailParams.setMargins(50,350,50,300);
        notifTitle.setLayoutParams(notifTitleParams);
        notifDetails.setLayoutParams(notifDetailParams);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow notifCreatePopup = new PopupWindow(popupView, width, height, focusable);

        notifCreatePopup.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        Button confirmButton = popupView.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nTitle = notifTitle.getText().toString();
                nDetails = notifDetails.getText().toString();
                // Possibly to be implemented later:
                // String notifType = notifType.getText().toString();

                HashMap<String,String> data = new HashMap<>();
                data.put("details", nDetails);
                // Placeholders
                data.put("notifDate", "Mar 21, 2024"); // Later make it a Date obj and convert to String?
                data.put("notifType", "reminder");     // Later give option

                notifRef
                        .document(nTitle)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                Log.d("NotifDB Update", "Data has been added successfully!");
                                // Uses input information to create and send push notification
                                sendPushNotif(nTitle, nDetails);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("NotifDB Update", "Data could not be added");
                            }
                        });
                notifCreatePopup.dismiss(); // Close popup window
            }
        });
        Button cancelButton = popupView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            notifCreatePopup.dismiss();
        });
    }
}

