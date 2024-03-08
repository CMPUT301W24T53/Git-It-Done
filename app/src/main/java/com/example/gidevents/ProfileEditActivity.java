package com.example.gidevents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfileEditActivity extends AppCompatActivity  implements AttendeeEditOptionFragment.EditOptionDialogListener{
    private AttendeeDB user;
    private ArrayList<AttendeeProfileEditOption> options = new ArrayList<>();
    private ListView optionsList;
    private AttendeeProfileEditAdapter optionAdapter;
    @Override
    public void editOption(AttendeeProfileEditOption option){
        user.setData(option);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_profile);

        optionsList = findViewById(R.id.attendeeOptionsList);
        optionAdapter = new AttendeeProfileEditAdapter(this, options);
        optionsList.setAdapter(optionAdapter);

        user = new AttendeeDB(optionAdapter);

        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new AttendeeEditOptionFragment(options.get(position)).show(getSupportFragmentManager(), "Edit Option");
            }
        });
    }

}
