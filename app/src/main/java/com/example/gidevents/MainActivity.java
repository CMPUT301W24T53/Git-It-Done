package com.example.gidevents;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;

import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private Activity activity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        signInAnonymously();
        geolocationPerms(GlobalContext.context);
        notifPerms(GlobalContext.context);
        NotifHandler notifHandler = new NotifHandler();
        notifHandler.storeFCMToken();
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


//        administratorBtn.setOnClickListener(v -> {
//            //Admin User Verification based on if users name is Admin
//            if (!(mAuth.getUid() == null)) {
//                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        DocumentSnapshot user = task.getResult();
//                        if (Objects.equals(user.get("Name"), "Admin")){
//                            Toast.makeText(activity,"Welcome Administrator", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
//                            startActivity(intent);
//                        }
//                        else{
//                            Toast.makeText(activity,"ERROR: Not An Admin", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });

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
                            userRef = db.collection("Users").document(mAuth.getCurrentUser().getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("UserAuth", "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void geolocationPerms (Context context) {
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void notifPerms (Context context) {
        final int MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS_SERVICE  = 1001;

        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS_SERVICE );
        }
    }


}