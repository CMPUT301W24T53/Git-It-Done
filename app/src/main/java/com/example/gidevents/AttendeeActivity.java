package com.example.gidevents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * This Class defines the overall function for the Attendee Activity
 */
public class AttendeeActivity extends AppCompatActivity {
    /**
     * Runs on creation of the activity and holds most functionality
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_main);
        Button checkInBtn = (Button) findViewById(R.id.checkInButton);
        Button enrolledEventsBtn = (Button) findViewById(R.id.enrolledEventsButton);
        Button notifBtn = (Button) findViewById(R.id.notificationButton);
        Button profileEditBtn = (Button) findViewById(R.id.profileEditButton);
        Button browseEventsBtn = (Button) findViewById(R.id.browseEventsButton);
        Button backBtn = (Button) findViewById(R.id.back_button);
        ImageView pfpImage = findViewById(R.id.AttendeeImageView);
        TextView pfpText = findViewById(R.id.AttendeeImageText);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users").document(mAuth.getCurrentUser().getUid());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String pfp = (String)task.getResult().get("pfpImage");
                if (pfp == null || pfp.isEmpty()){
                    String Usrname = (String)task.getResult().get("Name");
                    pfpImage.setVisibility(View.INVISIBLE);
                    if (Usrname.length()<2){
                        pfpText.setText(Usrname);
                    }
                    else{
                        pfpText.setText(Usrname.substring(0,2));
                    }
                    pfpText.setVisibility(View.VISIBLE);
                }
                else{
                    pfpText.setVisibility(View.INVISIBLE);
                    Glide.with(pfpImage).load(pfp).into(pfpImage);
                    pfpImage.setVisibility(View.VISIBLE);
                }
            }
        });

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AttendeeActivity.this, MainActivity.class);
            startActivity(intent);
        });
        checkInBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AttendeeActivity.this, ScanQRCodeActivity.class);
            startActivity(intent);
        });
        enrolledEventsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AttendeeActivity.this, MyEventsPageActivity.class);
            startActivity(intent);
        });
        notifBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AttendeeActivity.this, NotifActivity.class);
            startActivity(intent);
        });
        profileEditBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AttendeeActivity.this, ProfileEditActivity.class);
            startActivity(intent);
        });
        browseEventsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AttendeeActivity.this, BrowseEventActivity.class);
            startActivity(intent);
        });
    }
}