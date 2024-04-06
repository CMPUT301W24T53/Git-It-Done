package com.example.gidevents;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;


public class CreateEventActivity extends AppCompatActivity {

    private EditText etEventOrganizer, etEventTitle, etEventDescription, etAttendeeLimit,etEventLocation;
    private TextView tvSelectedDate;
    private ImageView ivEventPoster;
    private Button btnSelectDate, btnUploadPoster, btnGenerateQRCodes, btnReuseQR;


    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference posterStorageRef;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri posterImageUri;
    private StorageReference qrCodeStorageRef;
    private SwitchMaterial toggleAttendeeLimit;

    private String reuseQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Initialize Firebase Firestore and Storage references
        db = FirebaseFirestore.getInstance();
        posterStorageRef = FirebaseStorage.getInstance().getReference("eventPosters");
        qrCodeStorageRef = FirebaseStorage.getInstance().getReference("QRCodeBitmap");

        etEventOrganizer = findViewById(R.id.etEventOrganizer);
        etEventTitle = findViewById(R.id.etEventTitle);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventDescription = findViewById(R.id.etEventDescription);
        ivEventPoster = findViewById(R.id.ivEventPoster);
        btnUploadPoster = findViewById(R.id.btnUploadPoster);
        btnReuseQR = findViewById(R.id.btnReuseQR);

        btnGenerateQRCodes = findViewById(R.id.btnGenerateQRCodes);
        toggleAttendeeLimit = findViewById(R.id.toggleAttendeeLimit);
        etAttendeeLimit = findViewById(R.id.etAttendeeLimit);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnSelectDate = findViewById(R.id.btnSelectDate);


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
                showTimePickerDialog();
            }
        });

        btnUploadPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnReuseQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQRPickerDialog();
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
     * Show a time picker dialog to select the event time.
     */
    private void showTimePickerDialog() {
        final Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentCalendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                Calendar selectedCalendar = Calendar.getInstance();
                                selectedCalendar.set(year, month, dayOfMonth, hourOfDay, minute);

                                if (selectedCalendar.before(currentCalendar)) {
                                    Toast.makeText(CreateEventActivity.this, "The start time must be after the current time", Toast.LENGTH_SHORT).show();
                                } else {
                                    String selectedDateTime = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.SHORT).format(selectedCalendar.getTime());
                                    tvSelectedDate.setText("Event start time: " + selectedDateTime);
                                }
                            }, currentHour, currentMinute, false);

                    timePickerDialog.show();
                }, currentYear, currentMonth, currentDay);

        datePickerDialog.getDatePicker().setMinDate(currentCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showQRPickerDialog(){
        ListView listView = new ListView(this);

        ArrayList<String[]> data = new ArrayList<>();
        OrganizerReuseQRAdapter adapter= new OrganizerReuseQRAdapter(this, data);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        db.collection("Events").whereEqualTo("creatorID", auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {

                Log.d("FireStore", "Queried QR codes Successfully!");
                querySnapshot.forEach(new Consumer<QueryDocumentSnapshot>() {
                                          @Override
                                          public void accept(QueryDocumentSnapshot queryDocumentSnapshot) {
                                              String[] d = {queryDocumentSnapshot.get("checkInEventID").toString(),queryDocumentSnapshot.get("eventTitle").toString() };
                                              data.add(d);
                                              adapter.notifyDataSetChanged();
                                          }
                                      }
                );
            }
        });





        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setSelection(position);
                Log.d("test", "this item has been clicked" );
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setTitle("Pick a QR Code")
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        reuseQR = data.get(listView.getCheckedItemPosition())[0];
                        Log.d("test", "this is the selected item" + (String)listView.getSelectedItem());

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reuseQR = null;
                    }
                })
                .setView(listView);

        final AlertDialog dialog = builder.create();

        dialog.show();


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
        mAuth = FirebaseAuth.getInstance();
        String creatorID = mAuth.getCurrentUser().getUid();

        String eventOrganizer = etEventOrganizer.getText().toString().trim();
        String eventTitle = etEventTitle.getText().toString().trim();
        String eventLocation = etEventLocation.getText().toString().trim();
        String eventDescription = etEventDescription.getText().toString().trim();
        String eventDate = tvSelectedDate.getText().toString().trim();
        String strAttendeeLimit = etAttendeeLimit.getText().toString().trim();
        int attendeeLimit;
        if (!strAttendeeLimit.isEmpty()) {
            attendeeLimit = Integer.parseInt(strAttendeeLimit);
        } else {
            attendeeLimit = 0;
        }


        if (TextUtils.isEmpty(eventOrganizer) || TextUtils.isEmpty(eventTitle) || TextUtils.isEmpty(eventLocation)
                || TextUtils.isEmpty(eventDescription) || TextUtils.isEmpty(eventDate)
                || posterImageUri == null) {
            Toast.makeText(this, "Please fill in all fields and upload an event poster", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventId = generateRandomEventId();
        String checkInEventId;
        if (reuseQR != null) {
            checkInEventId = reuseQR;

        }else {
            checkInEventId ="CHECKIN-" + eventId;

        }
        Log.d("test", "created event qr with this checkin ID: " + checkInEventId);
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventOrganizer", eventOrganizer);
        eventData.put("eventTitle", eventTitle);
        eventData.put("eventLocation", eventLocation);
        eventData.put("eventDescription", eventDescription);
        eventData.put("eventDate", eventDate);
        eventData.put("checkInEventID", checkInEventId);
        eventData.put("eventID", eventId);
        eventData.put("attendeeLimit", attendeeLimit);
        eventData.put("creatorID", creatorID);



        uploadPosterImage(posterImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        posterStorageRef.child(taskSnapshot.getMetadata().getName()).getDownloadUrl()
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
        StorageReference fileReference = posterStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
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
                // QR Code uploaded successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to upload QR Code
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
     * This method now add the eventID of the current event to the User's MyEvents collection
     * @param eventID the ID of the current event
     */
    private void addCreatedEvent(String eventID) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!= null) {
            String creatorID = currentUser.getUid();
            Map <String, Object> event = new HashMap<>();
            event.put("eventID", eventID);
            db.collection("Users").document(creatorID).collection("CreatedEvents").document(eventID)
                    .set(event)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("EventSignUp", "EventID added to CreatedEvents");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("EventSignUp", "Failed to add EventID to CreatedEvents" + e);
                    });
        } else {
            Log.d("EventSignUp", "No User found");
        }

    }

    /**
     * Save the event data to Firestore.
     * @param eventData The event data to save.
     */
    private void saveEventDataToFirestore(Map<String, Object> eventData) {
        db.collection("Events")
                .add(eventData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CreateEventActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                        String eventID = documentReference.getId();
                        String checkInQRCodeContent = eventData.get("checkInEventID").toString();
                        String eventQRCodeContent = eventData.get("eventID").toString();
                        addCreatedEvent(eventID);

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
