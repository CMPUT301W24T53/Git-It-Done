package com.example.gidevents;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NotifHandler extends FirebaseMessagingService {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notifRef = db.collection("Notifications");


    public void storeNotifDetails(String eventName, String body, String eventID) {
        HashMap<String, String> data = new HashMap<>();
        data.put("details", body);
        data.put("eventID", eventID);

        notifRef
                .document(eventName)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d("NotifDB Update", "Data has been added successfully!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("NotifDB Update", "Data could not be added");
                    }
                });
    }

}