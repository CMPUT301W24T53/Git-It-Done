package com.example.gidevents;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.location.Location;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
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
import java.util.HashSet;
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
    FusedLocationProviderClient userLoc;
    private boolean isHandlingCheckIn = false;
    private String lastScannedQRCode = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);
        userLoc = LocationServices.getFusedLocationProviderClient(this);

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
                if (!scannedData.equals(lastScannedQRCode)) {
                    lastScannedQRCode = scannedData;
                    checkQRCode(scannedData);
                } else {
                    returnToAttendeeActivity();
                }
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
        lastScannedQRCode = null;

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
                returnToAttendeeActivity();
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


    private void handleCheckIn(String eventIdFromQR) {
        if (isHandlingCheckIn) {
            return;
        }

        isHandlingCheckIn = true;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("Events").whereEqualTo("eventID", eventIdFromQR).limit(1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot eventDocument = task.getResult().getDocuments().get(0);
                        String actualEventId = eventDocument.getId();

                        eventDocument.getReference().collection("participants").document(userId).get()
                                .addOnCompleteListener(participantsTask -> {
                                    if (participantsTask.isSuccessful()) {
                                        DocumentSnapshot participantDocument = participantsTask.getResult();
                                        if (participantDocument.exists()) {
                                            checkInToParticipants(actualEventId, userId, db);
                                        } else {
                                            showToast("You are not signed up for this event. Cannot check-in.");
                                        }
                                    } else {
                                        handleFailure("Failed to verify if user is a participant: " + participantsTask.getException().getMessage());
                                    }
                                    isHandlingCheckIn = false;
                                });
                    } else {
                        handleFailure("Event lookup failed: " + task.getException().getMessage());
                        isHandlingCheckIn = false;
                    }
                });
    }




    private void checkInToParticipants(String eventId, String userId, FirebaseFirestore db) {
        DocumentReference eventDocRef = db.collection("Events").document(eventId);

        CollectionReference participantsCheckInRef = eventDocRef.collection("participantsCheckIn");

        participantsCheckInRef.document(userId).get()
                .addOnCompleteListener(participantsCheckInTask -> {
                    if (participantsCheckInTask.isSuccessful()) {
                        DocumentSnapshot participantCheckInSnapshot = participantsCheckInTask.getResult();
                        if (participantCheckInSnapshot.exists()) {
                            checkInIncrease(participantsCheckInRef, userId);
                        } else {
                            addNewParticipant(participantsCheckInRef, userId, eventDocRef.getId());
                        }
                    } else {
                        handleFailure("Failed to check if user has checked in: " + participantsCheckInTask.getException().getMessage());
                    }
                    isHandlingCheckIn = false;
                });
    }

    private void checkInIncrease(CollectionReference participantsRef, String userId) {
        DocumentReference participantDocRef = participantsRef.document(userId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot participantSnapshot = transaction.get(participantDocRef);
                    transaction.update(participantDocRef, "numOfCheckIns",
                            participantSnapshot.getLong("numOfCheckIns") + 1);
                    return null;
                }).addOnSuccessListener(aVoid -> showToast("number of check-ins increased"))
                .addOnFailureListener(e -> handleFailure("Failed to increase number of check-ins: " + e.getMessage()));
    }

    private void addNewParticipant(CollectionReference participantsRef, String userId, String Event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(userId);

        HashSet<String> inputOptions = new HashSet<String>();
        inputOptions.add("true");
        inputOptions.add("t");
        inputOptions.add("T");
        inputOptions.add("True");
        inputOptions.add("TRUE");

        Map<String, Object> participantData = new HashMap<>();
        participantData.put("userId", userId);
        participantData.put("checkedIn", true);
        participantData.put("timestamp", FieldValue.serverTimestamp());
        participantData.put("numOfCheckIns", 1);
        participantData.put("eventName", Event);
        //To be Tested
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot userInfo = task.getResult();
                if (inputOptions.contains((String)userInfo.get("GeoLocation"))){
                    if (checkLocPermissions()){
                        userLoc.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                participantData.put("geoLocation", location);
                            }
                        });
                    }
                }
            }
        });

        participantsRef.document(userId).set(participantData)
                .addOnSuccessListener(aVoid -> showToast("Check-in successful"))
                .addOnFailureListener(e -> handleFailure("New participant check-in failed: " + e.getMessage()));
    }

    private boolean checkLocPermissions(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    private void navigateToEventDetails(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events")
                .whereEqualTo("eventID", eventId)
                .limit(1)
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
                            String eventLocation= doc.getString("eventLocation");
                            String eventPoster = doc.getString("eventPoster");
                            Events eventDetails = new Events(eventTitle, eventDate, eventLocation, eventOrganizer, eventDescription, eventPoster, eventID);
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