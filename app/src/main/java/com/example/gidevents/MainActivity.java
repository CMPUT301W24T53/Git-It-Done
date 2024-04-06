package com.example.gidevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;
import com.example.gidevents.FirebaseTokenHelper;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();
        notifPerms(GlobalContext.context);
        FirebaseTokenHelper tokenHelper = new FirebaseTokenHelper();
        tokenHelper.retrieveTokenAndUpdateFirestore();
        Button attendeeBtn = (Button) findViewById(R.id.attendeeButton);
        Button organizerBtn = (Button) findViewById(R.id.organizerButton);
        Button administratorBtn = (Button) findViewById(R.id.administratorButton);

        attendeeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AttendeeActivity.class);
            startActivity(intent);

        });

        organizerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrganizerActivity.class);
            startActivity(intent);

        });

        administratorBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
        });

    }



    /**
     * Signs in user passively and allows for retention of information through app relaunches
     */
    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("UserAuth", "signInAnonymously:success");
                            AttendeeDBConnector.populateDB();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("UserAuth", "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void notifPerms (Context context) {
        final int MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS_SERVICE  = 1001;

        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS_SERVICE );
            return;
        }
    }


}