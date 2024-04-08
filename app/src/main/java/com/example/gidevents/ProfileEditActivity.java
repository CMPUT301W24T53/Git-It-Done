package com.example.gidevents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

/**
 * Controls activity for editing a profile
 * Yet to add: Input filtering
 */
public class ProfileEditActivity extends AppCompatActivity  implements AttendeeEditOptionFragment.EditOptionDialogListener{
    private AttendeeDB user;
    private ArrayList<AttendeeProfileEditOption> options = new ArrayList<>();
    private ListView optionsList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private AttendeeProfileEditAdapter optionAdapter;
    private Button changepfpbttn, backbttn;
    private TextView pfpText;
    private ImageView pfpImage;

    /**
     * Implements edit option from AttendeeEditOptionFragment to pass value to database
     * @param option option to pass
     */
    @Override
    public void editOption(AttendeeProfileEditOption option){
        Toast errorToast = Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT);
        HashSet<String> inputOptions = new HashSet<String>();
        inputOptions.add("true");
        inputOptions.add("t");
        inputOptions.add("T");
        inputOptions.add("True");
        inputOptions.add("TRUE");
        inputOptions.add("false");
        inputOptions.add("f");
        inputOptions.add("F");
        inputOptions.add("False");
        inputOptions.add("FALSE");

        if(Objects.equals(option.getOptionType(), "Homepage") && !Patterns.WEB_URL.matcher(option.getCurrentvalue()).matches()){
            errorToast.show();
        }
        if(Objects.equals(option.getOptionType(), "Email") && !Patterns.EMAIL_ADDRESS.matcher(option.getCurrentvalue()).matches()){
            errorToast.show();
        }
        if(Objects.equals(option.getOptionType(), "Phone") && !PhoneNumberUtils.isGlobalPhoneNumber(option.getCurrentvalue())){
            errorToast.show();
        }
        if(Objects.equals(option.getOptionType(), "GeoLocation") && !inputOptions.contains(option.getCurrentvalue())){
            errorToast.show();
        }

        user.setData(option);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users").document(mAuth.getCurrentUser().getUid());


        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_profile);

        optionsList = findViewById(R.id.attendeeOptionsList);
        changepfpbttn = findViewById(R.id.upChangeProfilePicture);
        backbttn = findViewById(R.id.attendee_profile_back_button);
        pfpImage = findViewById(R.id.upProfilePictureImage);
        pfpText = findViewById(R.id.upProfilePictureText);
        optionAdapter = new AttendeeProfileEditAdapter(this, options);
        optionsList.setAdapter(optionAdapter);

        user = new AttendeeDB(optionAdapter);

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("Firestore", error.toString());
                }
                else{
                    if (user.getPfp().isEmpty()){
                        String Usrname = user.getUsrName();
                        pfpImage.setVisibility(View.INVISIBLE);
                        if (Usrname.length()<2){
                            pfpText.setText(Usrname);
                        }
                        else{
                            pfpText.setText(Usrname.substring(0,2));
                        }
                        pfpText.setVisibility(View.VISIBLE);
                    }
                    else{
                        pfpText.setVisibility(View.INVISIBLE);
                        Glide.with(pfpImage).load(user.getPfp()).into(pfpImage);
                        pfpImage.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new AttendeeEditOptionFragment(options.get(position)).show(getSupportFragmentManager(), "Edit Option");
            }
        });
        changepfpbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileEditActivity.this, AttendeeUploadImageActivity.class);
                startActivity(intent);
            }
        });
        backbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}