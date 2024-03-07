package com.example.gidevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileEditActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_profile);

        Button UsernameBtn = findViewById(R.id.upUsernameButton);
        Button NameBtn = findViewById(R.id.upNameButton);
        Button EmailBtn = findViewById(R.id.upEmailButton);
        Button PhoneBtn = findViewById(R.id.upPhoneButton);
        Button GenderBtn = findViewById(R.id.upGenderButton);
        Button BirthdayBtn = findViewById(R.id.upBirthdayButton);
        Button AddressBtn = findViewById(R.id.upAddressButton);
        Button GeoLocBtn = findViewById(R.id.upGeoLocationButton);
    }

}
