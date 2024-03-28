package com.example.gidevents;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class CreateEventActivity extends AppCompatActivity {

    private EditText etOrganizerName, etEventTitle, etEventDescription, etAttendeeLimit;
    private TextView tvSelectedDate;
    private ImageView ivEventPoster;
    private Button btnSelectDate, btnUploadPoster, btnGenerateQRCodes;

    private FirebaseFirestore db;
    private StorageReference storageRef;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri posterImageUri;
    private StorageReference qrCodeStorageRef;
    private SwitchMaterial toggleAttendeeLimit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize Firebase Firestore and Storage references
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("eventPosters");
        qrCodeStorageRef = FirebaseStorage.getInstance().getReference("QRCodeBitmap");

        etOrganizerName = findViewById(R.id.etOrganizerName);
        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDescription = findViewById(R.id.etEventDescription);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        ivEventPoster = findViewById(R.id.ivEventPoster);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnUploadPoster = findViewById(R.id.btnUploadPoster);
        btnGenerateQRCodes = findViewById(R.id.btnGenerateQRCodes);
        toggleAttendeeLimit = findViewById(R.id.toggleAttendeeLimit);
        etAttendeeLimit = findViewById(R.id.etAttendeeLimit);

        etAttendeeLimit.setEnabled(false);
        etAttendeeLimit.setFilters(new InputFilter[] {
                (source, start, end, dest, dstart, dend) -> {
                    for(int i = start; i< end; i++) {
                        if (!Character.isDigit((source.charAt(i)))) {
                            return "";
                        }
                    }
                    return null;
                }
        });

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnUploadPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnGenerateQRCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCodes();
            }
        });

        toggleAttendeeLimit.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            etAttendeeLimit.setEnabled(isChecked);
        }));
    }

    /**
     * Show a date picker dialog to select the event date.
     */
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CreateEventActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Directly set the selected date to the TextView
                        tvSelectedDate.setText(String.format(Locale.getDefault(), "%04d/%02d/%02d", year, month + 1, dayOfMonth));
                    }
                },
                year, month, day);

        // Set the minimum date to the current date to prevent selection of past dates
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    /**
     * Open a file chooser to select an image for the event poster.
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            posterImageUri = data.getData();
            Glide.with(this).load(posterImageUri).into(ivEventPoster);
        }
    }

    /**
     * Generate QR codes for the event and upload them to Firebase Storage.
     */
    private void generateQRCodes() {
        String organizerName = etOrganizerName.getText().toString().trim();
        String eventTitle = etEventTitle.getText().toString().trim();
        String eventDescription = etEventDescription.getText().toString().trim();
        String eventDate = tvSelectedDate.getText().toString().trim();
        String strAttendeeLimit = etAttendeeLimit.getText().toString().trim();
        int attendeeLimit;
        if (!strAttendeeLimit.isEmpty()) {
            attendeeLimit = Integer.parseInt(strAttendeeLimit);
        } else {
            attendeeLimit = 0;
        }


        if (TextUtils.isEmpty(organizerName) || TextUtils.isEmpty(eventTitle)
                || TextUtils.isEmpty(eventDescription) || TextUtils.isEmpty(eventDate)
                || posterImageUri == null) {
            Toast.makeText(this, "Please fill in all fields and upload an event poster", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventId = generateRandomEventId();
        String checkInEventId = "CHECKIN-" + eventId;

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventOrganizer", organizerName);
        eventData.put("eventTitle", eventTitle);
        eventData.put("eventDescription", eventDescription);
        eventData.put("eventDate", eventDate);
        eventData.put("checkInEventID", checkInEventId);
        eventData.put("eventID", eventId);
        eventData.put("attendeeLimit", attendeeLimit);

        uploadPosterImage(posterImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.child(taskSnapshot.getMetadata().getName()).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUri) {
                                        eventData.put("eventPoster", downloadUri.toString());
                                        saveEventDataToFirestore(eventData);

                                        // Generate QR code image and upload to Firebase Storage
                                        Bitmap checkInQRCodeBitmap = QRCodeBitmap.generateQRCodeBitmap(checkInEventId);
                                        uploadQRCodeToFirebase(checkInQRCodeBitmap, checkInEventId + ".png");

                                        Bitmap eventQRCodeBitmap = QRCodeBitmap.generateQRCodeBitmap(eventId);
                                        uploadQRCodeToFirebase(eventQRCodeBitmap, eventId + ".png");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateEventActivity.this, "Failed to upload event poster", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Upload the event poster image to Firebase Storage.
     *
     * @param imageUri The URI of the image to upload.
     * @return The UploadTask for the image upload.
     */
    private UploadTask uploadPosterImage(Uri imageUri) {
        StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        return fileReference.putFile(imageUri);
    }

    /**
     * Upload a QR code bitmap to Firebase Storage.
     *
     * @param qrCodeBitmap The QR code bitmap to upload.
     * @param qrCodeName   The name of the QR code file.
     */
    private void uploadQRCodeToFirebase(Bitmap qrCodeBitmap, String qrCodeName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference qrCodeRef = qrCodeStorageRef.child(qrCodeName);
        UploadTask uploadTask = qrCodeRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CreateEventActivity.this, "QR Code uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateEventActivity.this, "Failed to upload QR Code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Get the file extension from a URI.
     * @param uri The URI of the file.
     * @return The file extension.
     */
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /**
     * Save the event data to Firestore.
     * @param eventData The event data to save.
     */
    private void saveEventDataToFirestore(Map<String, Object> eventData) {
        db.collection("qrcodes")
                .add(eventData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CreateEventActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                        String eventId = documentReference.getId();
                        String checkInQRCodeContent = eventData.get("checkInEventID").toString();
                        String eventQRCodeContent = eventData.get("eventID").toString();
                        Intent intent = new Intent(CreateEventActivity.this, QRCodeActivity.class);
                        intent.putExtra("checkInQRCodeContent", checkInQRCodeContent);
                        intent.putExtra("eventQRCodeContent", eventQRCodeContent);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateEventActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Generate a random event ID.
     * @return The generated event ID.
     */
    private String generateRandomEventId() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
    }

}
