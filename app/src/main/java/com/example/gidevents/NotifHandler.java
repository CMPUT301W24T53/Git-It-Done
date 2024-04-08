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
import com.google.firebase.auth.FirebaseAuth;
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


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("FCM Token", "Refreshed token: " + token);
        HashMap<String, Object> fcmToken = new HashMap<>();
        fcmToken.put("fcmToken", token);

        db.collection("Users").document(userID).update(fcmToken)
                .addOnSuccessListener( v -> {
                    Log.d("FCM Token", "FCM token added successfully");
                }).addOnFailureListener( v-> {
                    Log.d("FCM Token", "FCM token failed to add");
                });
    }

    public void storeFCMToken() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String, Object> fcmToken = new HashMap<>();
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        Log.d("FCM Token", "FCM Token: " + token);
                        // Send this token to your server to subscribe the device
                        // to topics or to send direct messages
                        db.collection("Users").document(userID).update(fcmToken)
                                .addOnSuccessListener( v -> {
                                    Log.d("FCM Token", "FCM token added successfully");
                                }).addOnFailureListener( v-> {
                                    Log.d("FCM Token", "FCM token failed to add");
                                });
                    }
                });
    }

}