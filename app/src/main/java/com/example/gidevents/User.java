package com.example.gidevents;

import android.content.Intent;
import android.icu.text.BidiRun;
import android.location.Address;

import java.io.Serializable;

/**
 * This is the User class used to display user profile
 */
public class User implements Serializable {
    private String name;
    private String username;
    private String address;
    private String birthday;
    private String email;
    private String gender;
    private String userID;
    private String phone;

    /**
     * Constructor for User class
     * @param Name String read from Firestore
     * @param Username String read from Firestore
     * @param Address String read from Firestore
     * @param Birthday String read from Firestore
     * @param Email String read from Firestore
     * @param Gender String read from Firestore
     * @param UserID String read from Firestore
     * @param Phone String read from Firestore
     */
    public User(String Name, String Username, String Address, String Birthday, String Email, String Gender, String UserID, String Phone) {
        this.name = Name;
        this.username = Username;
        this.address = Address;
        this.birthday = Birthday;
        this.email = Email;
        this.gender = Gender;
        this.userID = UserID;
        this.phone = Phone;
    }
    //Some basic getters
    public String getName(){return name == null || name.isEmpty()? "N/A" : name;}
    public String getUsername(){return username == null || username.isEmpty()? "Anonymous User" : username;}
    public String getAddress() {return address == null || address.isEmpty()? "N/A" : address;}
    public String getBirthday() {return birthday == null || birthday.isEmpty()? "N/A" : birthday;}
    public String getEmail() {return email == null || email.isEmpty()? "N/A" : email;}
    public String getGender() {return gender == null || gender.isEmpty()? "N/A" : gender;}
    public String getUserID() {return userID;}
    public String getPhone() {return phone == null || phone.isEmpty()? "N/A" : phone;}

}
