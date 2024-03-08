package com.example.gidevents;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AttendeeDB{
    private AttendeeDBConnector dbConnector;
    private AttendeeProfileEditAdapter optionAdapter;
    public AttendeeDB(AttendeeProfileEditAdapter optionAdapter){
        this.optionAdapter = optionAdapter;
        dbConnector = new AttendeeDBConnector(optionAdapter);
    }
    public void setData(AttendeeProfileEditOption option){
        dbConnector.setData(option);
    }
    public static void populateDB(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String Uid = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        CollectionReference collUsers = db.collection("Users");
        Map<String, String> data = Map.of
                ("Email","",
                "Address","",
                "Username","",
                "Phone","",
                "Birthday","",
                "GeoLocation","",
                "Gender","",
                "Name","");
        collUsers.document(Uid).set(data);
    }

}
