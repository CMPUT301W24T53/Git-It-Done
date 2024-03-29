package com.example.gidevents;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This activity handles the scanning of QR codes for event check-ins and navigation to event details.
 * After a QR code is scanned, the activity may either check-in the attendee or navigate
 * to the event details activity with the appropriate information based on the content of the QR code.
 * Outstanding Issues:
 * - Consider implementing better error handling for database operations.
 */
public class ScanQRCodeActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int SELECT_IMAGE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);

        if (hasCameraPermission()) {
            ScanQRCode();
        } else {
            requestCameraPermission();
        }

        Button backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> onBackPressed());

        Button selectImageButton = findViewById(R.id.btnSelectImage);
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                Button selectImageButton = findViewById(R.id.btnSelectImage);
                selectImageButton.setVisibility(View.VISIBLE);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Result result = scanQRCodeFromBitmap(bitmap);
                if (result != null) {
                    String scannedData = result.getText();
                    checkQRCode(scannedData);
                } else {
                    showToast("No valid QR code found in the image");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Fail to read image");
            }
        }
    }

    private Result scanQRCodeFromBitmap(Bitmap bitmap) {
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(binaryBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        CollectionReference eventsRef = db.collection("Events");

        eventsRef.whereEqualTo("eventID", eventId).limit(1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot eventDocument = task.getResult().getDocuments().get(0);
                        eventDocument.getId();
                        CollectionReference participantsRef = eventDocument.getReference().collection("participants");

                        participantsRef.document(userId).get()
                                .addOnCompleteListener(participantsTask -> {
                                    if (participantsTask.isSuccessful()) {
                                        DocumentSnapshot participantDocument = participantsTask.getResult();
                                        if (participantDocument.exists()) {
                                            updateParticipantCheckIn(participantDocument);
                                        } else {
                                            addNewParticipant(participantsRef, userId);
                                        }
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
        db.collection("Events")
                .whereEqualTo("eventID", eventId)
                .limit(1) // Limit the documents to return only one
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                            String eventTitle = doc.getString("eventTitle");
                            String eventDate = doc.getString("eventDate");
                            String eventOrganizer = doc.getString("eventOrganizer");
                            String eventDescription = doc.getString("eventDescription");
                            String eventID = doc.getId();
                            String time= doc.getString("eventTime");
                            String eventLocation= doc.getString("eventLocation");
                            String eventPoster = doc.getString("eventPoster");
                            Events eventDetails = new Events(eventTitle, eventDate, time, eventLocation, eventOrganizer, eventDescription, eventPoster, eventID);
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

    private void addNewParticipant(CollectionReference participantsRef, String userId) {
        Map<String, Object> participantData = new HashMap<>();
        participantData.put("userId", userId);
        participantData.put("checkedIn", true);
        participantData.put("timestamp", FieldValue.serverTimestamp());

        participantsRef.document(userId).set(participantData)
                .addOnSuccessListener(aVoid -> showToast("Check-in successful"))
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

    private void logError(String message, Exception e) {
        Log.e("ScanQRCodeActivity", message, e);
    }
}
