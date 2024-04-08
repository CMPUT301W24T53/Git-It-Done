package com.example.gidevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserProfilesAdapter extends ArrayAdapter<User> implements Filterable {
    private List<User> users;
    private List<User> filteredUsers;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    public UserProfilesAdapter(Context context, List<User> users) {
        super(context, 0, users);
        this.users =users;
        this.filteredUsers = new ArrayList<>(users);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_profile_display, parent, false);
        }

        User user = getItem(position);
        ImageView userProfilePic = convertView.findViewById(R.id.profilePic);
        TextView userProfileText = convertView.findViewById(R.id.profilePicText);
        TextView usernameTextView = convertView.findViewById(R.id.username);
        TextView userIDTextView = convertView.findViewById(R.id.user_id);
        TextView emailTextView = convertView.findViewById(R.id.user_email);


        assert user != null;

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users").document(user.getUserID());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot userDoc = task.getResult();
                String pfpImage = userDoc.getString("pfpImage");
                if (pfpImage == null || pfpImage.isEmpty()){
                    String Usrname = user.getName();
                    if (Objects.equals(Usrname, "N/A")){
                        Usrname = "None";
                    }
                    userProfilePic.setVisibility(View.INVISIBLE);
                    if (Usrname.length()<2){
                        userProfileText.setText(Usrname);
                    }
                    else{
                        userProfileText.setText(Usrname.substring(0,2));
                    }
                    userProfileText.setVisibility(View.VISIBLE);
                }
                else{
                    userProfileText.setVisibility(View.INVISIBLE);
                    Glide.with(userProfilePic).load(pfpImage).into(userProfilePic);
                    userProfilePic.setVisibility(View.VISIBLE);
                }
            }
        });
        usernameTextView.setText(user.getUsername());
        userIDTextView.setText("UserID: "+user.getUserID());
        emailTextView.setText("Email: "+user.getEmail());
        return convertView;
    }
    @Override
    public int getCount() {
        return filteredUsers.size();
    }

    @Override
    public User getItem(int position) {
        return filteredUsers.get(position);
    }

    @Override
    public Filter getFilter () {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length()==0) {
                    results.values = users;
                } else {
                    List<User> filteredList = new ArrayList<>();
                    String filteredText = constraint.toString().toLowerCase().trim();
                    for (User user : users) {
                        if (user.getUsername().toLowerCase().trim().contains(filteredText)) {
                            filteredList.add(user);
                        }
                    }
                    results.values = filteredList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredUsers= (List<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
