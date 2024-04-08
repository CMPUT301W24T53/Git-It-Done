package com.example.gidevents;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipantListActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    String eventID;
    CollectionReference participantRef;
    private ArrayList<String> participantList = new ArrayList<>();
    private ListView listView;
    private String nTitle;
    private String nDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_list_page);

        db = FirebaseFirestore.getInstance();

        Events eventDetails = (Events) getIntent().getSerializableExtra("eventDetails");
        eventID = eventDetails.getEventID();

        ParticipantsAdapter adapter = new ParticipantsAdapter(this, participantList);
        listView = findViewById(R.id.organizer_participants_listview);
        listView.setAdapter(adapter);

        participantRef = db.collection("Events").document(eventID).collection("participants");

        participantRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    participantList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String participantID = doc.getId();
                        db.collection("Events").document(eventID).collection("participants").document(participantID).get()
                                .addOnSuccessListener(participant -> {
                                    if (participant.exists()) {
                                        String name = participant.getString("name");
                                        participantList.add(name);
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Unable to fetch participant"));
                    }
                }

            }

        });

        Button back_button = findViewById(R.id.back_button);
        Button newNotifBtn = findViewById(R.id.notif_button);

        back_button.setOnClickListener(v -> {
            finish();
        });
        newNotifBtn.setOnClickListener(v -> {
            newNotif(findViewById(R.id.notif_button));
        });
    }

    /**
     * Handles the popup window and allows organizer to create new notifications
     * Automatically send push notification when confirm is pressed, will likely
     * change to ensure all desired content is entered into notification
     * Also will likely change to disable confirm button until both text fields have
     * text content in them
     *
     * @param view
     */
    public void newNotif(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.organizer_notif_create, null);
        EditText eventTitle = popupView.findViewById(R.id.orgEventTitle);
        EditText notifDetails = popupView.findViewById(R.id.orgNotifDetails);

        RelativeLayout.LayoutParams eventTitleParams = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        RelativeLayout.LayoutParams notifDetailParams = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        eventTitleParams.setMargins(50, 150, 50, 0);
        notifDetailParams.setMargins(50, 350, 50, 300);
        eventTitle.setLayoutParams(eventTitleParams);
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
                nTitle = eventTitle.getText().toString();
                nDetails = notifDetails.getText().toString();

                // Uses input information to create and send push notification


                Log.d("FCM Notifications", "Notif should send");
                notifSender(GlobalContext.context, eventID, nTitle, nDetails);
                NotifHandler notifHandler = new NotifHandler();
                notifHandler.storeNotifDetails(nTitle,nDetails,eventID);
                Log.d("NotifDB", "Notif should be stored");

                notifCreatePopup.dismiss(); // Close popup window
            }
        });
        Button cancelButton = popupView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            notifCreatePopup.dismiss();
        });
    }

    public void notifSender(Context context, String eventID, String eventName, String body) {
        final int MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS_SERVICE  = 1001;
        List<String> tokenList = new ArrayList<String>();
        participantRef = db.collection("Events").document(eventID).collection("participants");


        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS_SERVICE );
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, eventID);
        builder.setSmallIcon(R.drawable.ic_event);
        builder.setContentTitle(eventName);
        builder.setContentText(body);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);


        managerCompat.notify(1, builder.build());
    }
}
