package com.example.gidevents;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AttendeeUploadImageActivity extends AppCompatActivity {
    StorageReference storageReference;
    Uri pfpImage;
    FirebaseAuth mAuth;
    DocumentReference userRef;
    Button selectImage, uploadImage, deleteImage;
    ImageView pfpImageView;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK){
                if (result.getData() != null){
                    pfpImage = result.getData().getData();
                    uploadImage.setEnabled(true);
                    Glide.with(getApplicationContext()).load(pfpImage).into(pfpImageView);
                }
            } else{
                Toast.makeText(AttendeeUploadImageActivity.this, "Select an Image", Toast.LENGTH_LONG).show();
            }
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_pfp_upload_page);

        storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures");


        pfpImageView = findViewById(R.id.attendeeUploadImage);
        selectImage = findViewById(R.id.attendeeSelectImagebttn);
        uploadImage = findViewById(R.id.attendeeUploadImagebttn);
        deleteImage = findViewById(R.id.attendeeDeleteImagebttn);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getUid());


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);

            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(pfpImage);
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> updates = new HashMap<>();
                updates.put("pfpImage", FieldValue.delete());
                userRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AttendeeUploadImageActivity.this, "Profile picture removed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AttendeeUploadImageActivity.this, ProfileEditActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void uploadImage(Uri image){
        HashMap<String, Object> data = new HashMap<>();
        StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(pfpImage));
        reference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(taskSnapshot.getMetadata().getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri dowloadUri) {
                        data.put("pfpImage", dowloadUri.toString());
                        userRef.set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AttendeeUploadImageActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AttendeeUploadImageActivity.this, ProfileEditActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AttendeeUploadImageActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}