package com.example.gidevents;

import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Class holds higher level database interaction for Attendee's
 */
public class AttendeeDB{
    private AttendeeDBConnector dbConnector;
    private AttendeeProfileEditAdapter optionAdapter;

    /**
     * Constructs the class with an adapter to hold data, and creates its associated DBconnector for lower level operations
     * @param optionAdapter adapter to hold attendee data
     */
    public AttendeeDB(AttendeeProfileEditAdapter optionAdapter){
        this.optionAdapter = optionAdapter;
        dbConnector = new AttendeeDBConnector(optionAdapter);
    }

    /**
     * sets data in database from input option
     * @param option attendee option to add to database
     */
    public void setData(AttendeeProfileEditOption option){
        dbConnector.setData(option);
    }
    public String getPfp(){
        return dbConnector.getPfp();
    }
    public String getUsrName(){
        return dbConnector.getUsrName();
    }

}
