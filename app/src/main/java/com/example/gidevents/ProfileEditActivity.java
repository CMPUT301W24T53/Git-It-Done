package com.example.gidevents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileEditActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_profile);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users").document(mAuth.getCurrentUser().getUid());

        Button UsernameBtn = findViewById(R.id.upUsernameButton);
        TextView UsernameTxt = findViewById(R.id.upUsernameText);
        Button NameBtn = findViewById(R.id.upNameButton);
        TextView NameTxt = findViewById(R.id.upNameText);
        Button EmailBtn = findViewById(R.id.upEmailButton);
        TextView EmailTxt = findViewById(R.id.upEmailText);
        Button PhoneBtn = findViewById(R.id.upPhoneButton);
        TextView PhoneTxt = findViewById(R.id.upPhoneText);
        Button GenderBtn = findViewById(R.id.upGenderButton);
        TextView GenderTxt = findViewById(R.id.upGenderText);
        Button BirthdayBtn = findViewById(R.id.upBirthdayButton);
        TextView BirthdayTxt = findViewById(R.id.upBirthdayText);
        Button AddressBtn = findViewById(R.id.upAddressButton);
        TextView AddressTxt = findViewById(R.id.upAddressText);
        Button GeoLocBtn = findViewById(R.id.upGeoLocationButton);
        TextView GeoLocTxt = findViewById(R.id.upGeoLocationText);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot user = task.getResult();
                if (user.exists()) {
                    UsernameTxt.setText(user.getString("Username"));
                } else {
                    UsernameTxt.setText("");
                }
            }
        });

    }

}
