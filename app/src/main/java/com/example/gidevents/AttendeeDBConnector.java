package com.example.gidevents;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AttendeeDBConnector {
    private String TAG = "AttendeeDBConnector";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private AttendeeProfileEditAdapter optionAdapter;

    public AttendeeDBConnector(AttendeeProfileEditAdapter optionAdapter){
        this.optionAdapter = optionAdapter;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users").document(mAuth.getCurrentUser().getUid());
        Log.v(TAG, "constructor invoked");
        this.initData();
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("Firestore", error.toString());
                }
                else{
                    updateData(value);
                }
            }
        });
    }
    private void updateData(DocumentSnapshot data){
        optionAdapter.clear();
        for (Map.Entry<String, Object> option: data.getData().entrySet()){
            AttendeeProfileEditOption optionEntry = new AttendeeProfileEditOption(option.getKey(),(String)option.getValue());
            optionAdapter.add(optionEntry);
        }
        optionAdapter.notifyDataSetChanged();
    }
    private void initData(){
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.v("Firebase", "Document Retrieved");
                    updateData(task.getResult());
                }
                if (!task.isSuccessful()){
                    Log.v("Firebase", "Document Not Retrieved");
                }
                if (!task.getResult().exists()){
                    Log.v("Firebase", "Document is Null");
                }
            }
        });
        }
    public void setData(AttendeeProfileEditOption option){
        HashMap<String, String> data = new HashMap<>();
        data.put(option.getOptionType(), option.getCurrentvalue());
        userRef.set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "UserSnapshot successfully written");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "UserSnapshot not successfully written");
                    }
                });
    }
}
