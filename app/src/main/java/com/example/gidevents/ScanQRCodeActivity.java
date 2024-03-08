package com.example.gidevents;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScanQRCodeActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);

        if (hasCameraPermission()) {
            // If permission is already granted, start the scan
            ScanQRCode();
        } else {
            // Else request permission
            requestCameraPermission();
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, continue with camera-related tasks
                ScanQRCode();
            } else {
                // Permission was denied
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    // User selected 'Never Ask Again', guide the user to settings
                    showManualPermissionSettingDialog();
                } else {
                    // User denied permission without selecting 'Never Ask Again'
                    Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
                    returnToAttendeeActivity();
                }
            }
        }
    }

    private void showManualPermissionSettingDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Denied")
                .setMessage("Camera permission is needed to scan QR codes. Please enable it in app settings.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    // Take the user to the app settings page
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    returnToAttendeeActivity();
                })
                .create()
                .show();
    }

    private void ScanQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CustomScannerActivity.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String scannedData = result.getContents();
            if (scannedData != null) {
                // Check if the QRCode matches any QR code's data present in Firestore
                checkQRCode(scannedData);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkQRCode(String scannedData) {
        if (scannedData != null && scannedData.startsWith("CHECKIN-")) {
            String eventId = scannedData.substring("CHECKIN-".length());
            // Log only if the QR code format is correct and we are about to handle a check-in
            Log.i("ScanQRCodeActivity", "Handling check-in for event ID: " + eventId);
            handleCheckIn(eventId);
        } else if (scannedData != null) {
            // Log only if the QR code does not start with the expected prefix
            Log.w("ScanQRCodeActivity", "Unexpected QR code format: " + scannedData);
            navigateToEventDetails(scannedData);
        }
    }

    private void handleCheckIn(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String deviceId = getUniqueDeviceId();
        CollectionReference qrcodesRef = db.collection("qrcodes");

        qrcodesRef.whereEqualTo("eventID", eventId).limit(1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot eventDocument = task.getResult().getDocuments().get(0);
                        CollectionReference participantsRef = eventDocument.getReference().collection("Participants");

                        participantsRef.whereEqualTo("deviceId", deviceId).limit(1).get()
                                .addOnCompleteListener(participantsTask -> {
                                    if (participantsTask.isSuccessful() && participantsTask.getResult().isEmpty()) {
                                        // No participant found, create a new participant record
                                        addNewParticipant(participantsRef, deviceId);
                                    } else if (participantsTask.isSuccessful()) {
                                        // Participant found, update check-in status
                                        updateParticipantCheckIn(participantsTask.getResult().getDocuments().get(0));
                                    } else {
                                        // Handle participant lookup failure
                                        handleFailure("Participants lookup failed: " + participantsTask.getException().getMessage());
                                    }
                                });
                    } else {
                        // Handle event lookup failure or event not found
                        handleFailure(task.isSuccessful() ? "Event not found" : "Event lookup failed: " + task.getException().getMessage());
                    }
                })
                .addOnCompleteListener(task -> returnToAttendeeActivity());
    }

    private void navigateToEventDetails(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("qrcodes")
                .whereEqualTo("eventID", eventId)
                .limit(1) // Limit the documents to return only one
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            Events eventDetails = querySnapshot.getDocuments().get(0).toObject(Events.class);
                            Intent intent = new Intent(ScanQRCodeActivity.this, EventDetailsPageActivity.class);
                            intent.putExtra("eventDetails", eventDetails);
                            startActivity(intent);
                        } else {
                            showToast("Invalid QR code.");
                        }
                    } else {
                        logError("Error getting documents: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    logError("Error accessing the database: ", e);
                    showToast("Database access error.");
                });
    }

    private void addNewParticipant(CollectionReference participantsRef, String deviceId) {
        Map<String, Object> participantData = new HashMap<>();
        participantData.put("Device ID:", deviceId);
        participantData.put("CheckIn Time:", FieldValue.serverTimestamp());
        participantData.put("CheckedIn Status:", true);

        participantsRef.add(participantData)
                .addOnSuccessListener(documentReference -> showToast("Check-in successful"))
                .addOnFailureListener(e -> handleFailure("New participant check-in failed: " + e.getMessage()));
    }

    private void updateParticipantCheckIn(DocumentSnapshot participantDocument) {
        participantDocument.getReference()
                .update("checkedIn", true, "timestamp", FieldValue.serverTimestamp())
                .addOnSuccessListener(aVoid -> showToast("Check-in updated"))
                .addOnFailureListener(e -> handleFailure("Participant check-in update failed: " + e.getMessage()));
    }

    private void handleFailure(String message) {
        showToast(message);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void returnToAttendeeActivity() {
        Intent intent = new Intent(this, AttendeeActivity.class);
        startActivity(intent);
        finish();
    }
    private String getUniqueDeviceId() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String deviceId = sharedPreferences.getString("device_id", null);
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            sharedPreferences.edit().putString("device_id", deviceId).apply();
        }
        return deviceId;
    }

    private void logError(String message, Exception e) {
        Log.e("ScanQRCodeActivity", message, e);
    }
}
