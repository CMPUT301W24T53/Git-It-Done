package com.example.gidevents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileDetailsPage extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    Button deleteButton;
    private User userDetails;
    private int eventCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_details_page);
        db = FirebaseFirestore.getInstance();





        try {
            userDetails = (User) getIntent().getSerializableExtra("userDetails");

            if (userDetails != null) {
                TextView usernameTextView = findViewById(R.id.username);
                ImageView profilePic = findViewById(R.id.profile_pic);
                TextView userIdTextView = findViewById(R.id.user_id);
                TextView nameTextView = findViewById(R.id.name);
                TextView emailTextView = findViewById(R.id.email);
                TextView phoneTextView = findViewById(R.id.phone);
                TextView addressTextView = findViewById(R.id.address);
                TextView genderTextView = findViewById(R.id.gender);
                TextView birthdayTextView = findViewById(R.id.birthday);
                TextView eventCountTextView = findViewById(R.id.event_count);

                //Add user profile pic here......
                String userID = userDetails.getUserID();
                db.collection("Users").document(userID).collection("MyEvents").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventCount = task.getResult().size();
                        Log.d("Firestore", "Event count: " + eventCount);
                    } else {
                        Log.d("Firestore", "Error getting MyEvents: ", task.getException());

                    }
                    eventCountTextView.setText("# of Events Signed Up: "+ eventCount);


                });

                usernameTextView.setText(userDetails.getUsername());
                userIdTextView.setText("UserID: "+ userID);
                nameTextView.setText("Name: "+ userDetails.getName());
                emailTextView.setText("Email: "+ userDetails.getEmail());
                phoneTextView.setText("Phone: "+userDetails.getPhone());
                addressTextView.setText("Address: "+ userDetails.getAddress());
                genderTextView.setText("Gender: "+ userDetails.getGender());
                birthdayTextView.setText("Birthday: "+ userDetails.getBirthday());





                deleteButton = findViewById(R.id.delete_button);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteUser(userID);
                    }
                });
            } else {
                Log.e("UserProfileDetailsPage", "No user details were provided.");
                finish(); // End the activity since there's no data to display
            }
        } catch (Exception e) {
            Log.e("UserProfileDetailsPage", "Error setting user details", e);
            Toast.makeText(this, "Error displaying user details.", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteUser(String userID){
        db.collection("Users").document(userID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User deleted: " + userID);
                    Toast.makeText(getApplicationContext(), "User deleted!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UserProfileDetailsPage.this, AdminBrowseProfilesActivity.class);
                    startActivity(intent);
                    //Maybe delete the user from the events he participated?
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error deleting user: " + userID, e);
                });

        }
}


