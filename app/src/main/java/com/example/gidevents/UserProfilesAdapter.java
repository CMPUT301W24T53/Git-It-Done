package com.example.gidevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class UserProfilesAdapter extends ArrayAdapter<User> {
    public UserProfilesAdapter(Context context, List<User> users) {
        super(context, 0, users);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_profile_display, parent, false);
        }

        User user = getItem(position);
        ImageView userProfilePic = convertView.findViewById(R.id.profilePic);
        TextView usernameTextView = convertView.findViewById(R.id.username);
        TextView userIDTextView = convertView.findViewById(R.id.user_id);
        TextView emailTextView = convertView.findViewById(R.id.user_email);


        assert user != null;

        usernameTextView.setText(user.getUsername());
        userIDTextView.setText("UserID: "+user.getUserID());
        emailTextView.setText("Email: "+user.getEmail());
        return convertView;
    }
}
