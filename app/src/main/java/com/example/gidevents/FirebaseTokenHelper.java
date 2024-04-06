package com.example.gidevents;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseTokenHelper extends FirebaseMessagingService {
    private static final String TAG = "FirebaseTokenHelper";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;
    private DocumentReference userRef;

    public FirebaseTokenHelper() {
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onNewToken(String token) {
        // Handle token generation, if needed.
        super.onNewToken(token);
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "Refreshed token: " + token);
        // Update the FCM token for the current user in Firestore
        DocumentReference userRef = db.collection("Users").document(userID);
        userRef.update("fcmToken", token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "FCM token updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error updating FCM token", e);
                    }
                });
    }

    public void retrieveTokenAndUpdateFirestore() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        Log.d(TAG, "Token: " + token);
                        updateTokenInFirestore(token);
                    } else {
                        Log.e(TAG, "Failed to retrieve token", task.getException());
                    }
                });
    }

    private void updateTokenInFirestore(String token) {
        userRef = db.collection("Users").document(userID);
        userRef.update("fcmToken", token)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Token updated successfully in Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating token in Firestore", e));
    }
}
