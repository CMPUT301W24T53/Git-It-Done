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
import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This activity handles the scanning of QR codes for event check-ins and navigation to event details.
 * After a QR code is scanned, the activity may either check-in the attendee or navigate
 * to the event details activity with the appropriate information based on the content of the QR code.
 * Outstanding Issues:
 * - Consider implementing better error handling for database operations.
 */
public class ScanQRCodeActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);

        if (hasCameraPermission()) {
            ScanQRCode();
        } else {
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
                ScanQRCode();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    showManualPermissionSettingDialog();
                } else {
                    Toast.makeText(this, "Camera permission is required to scan QR" +
                            " codes", Toast.LENGTH_LONG).show();
                    returnToAttendeeActivity();
                }
            }
        }
    }

    private void showManualPermissionSettingDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Denied")
                .setMessage("Camera permission is needed to scan QR codes. " +
                        "Please enable it in app settings.")
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
        setContentView(R.layout.activity_scan_qr_code);

        DecoratedBarcodeView barcodeView = findViewById(R.id.scanner_view);
        barcodeView.setStatusText("");
        barcodeView.decodeContinuous(callback);
    }

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                String scannedData = result.getText();
                checkQRCode(scannedData);
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // 可以在这里处理可能的结果点
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        DecoratedBarcodeView barcodeView = findViewById(R.id.scanner_view);
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DecoratedBarcodeView barcodeView = findViewById(R.id.scanner_view);
        barcodeView.pause();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String scannedData = result.getContents();
            if (scannedData != null) {
                checkQRCode(scannedData);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/


    /**
     * Checks the QR code data for proper format and determines the subsequent action,
     * either check-in handling or navigation to event details.
     */
    void checkQRCode(String scannedData) {
        if (scannedData != null && scannedData.startsWith("CHECKIN-")) {
            String eventId = scannedData.substring("CHECKIN-".length());
            Log.i("ScanQRCodeActivity", "Handling check-in for event ID: " + eventId);
            handleCheckIn(eventId);
        } else if (scannedData != null) {
            Log.w("ScanQRCodeActivity", "Unexpected QR code format: " + scannedData);
            navigateToEventDetails(scannedData);
        }
    }


    /**
     * Handles the check-in process by verifying the event and participant's status in Firestore
     * and updating the check-in status accordingly.
     */
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
                                        handleFailure("Participants lookup failed: " +
                                                participantsTask.getException().getMessage());
                                    }
                                });
                    } else {
                        // Handle event lookup failure or event not found
                        handleFailure(task.isSuccessful() ? "Event not found" : "Event lookup failed: " + task.getException().getMessage());
                    }
                })
                .addOnCompleteListener(task -> returnToAttendeeActivity());
    }

/**
 * Queries the Firestore database for event details associated with the given eventId.
 * If the event is found, it navigates to the EventDetailsPageActivity with the event details.
 */
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
                            returnToAttendeeActivity();
                            showToast("Invalid QR code.");

                        }
                    } else {
                        logError("Error getting documents: ", task.getException());
                    }
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
