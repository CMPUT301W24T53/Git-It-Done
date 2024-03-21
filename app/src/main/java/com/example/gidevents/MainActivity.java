package com.example.gidevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();
        Button attendeeBtn = (Button) findViewById(R.id.attendeeButton);
        Button organizerBtn = (Button) findViewById(R.id.organizerButton);
        Button administratorBtn = (Button) findViewById(R.id.administratorButton);

        attendeeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AttendeeActivity.class);
            startActivity(intent);

        });

        organizerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
            startActivity(intent);

        });

        administratorBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdministratorActivity.class);
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

}