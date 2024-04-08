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

import java.util.ArrayList;
import java.util.List;

public class UserProfilesAdapter extends ArrayAdapter<User> implements Filterable {
    private List<User> users;
    private List<User> filteredUsers;
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
        TextView usernameTextView = convertView.findViewById(R.id.username);
        TextView userIDTextView = convertView.findViewById(R.id.user_id);
        TextView emailTextView = convertView.findViewById(R.id.user_email);


        assert user != null;

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
