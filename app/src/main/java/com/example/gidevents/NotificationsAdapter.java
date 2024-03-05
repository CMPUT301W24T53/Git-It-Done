package com.example.gidevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class NotificationsAdapter extends ArrayAdapter<Notifications> {
    public NotificationsAdapter(Context context, List<Notifications> notificationsList) {
        super(context, 0, notificationsList);
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_display, parent, false);
        }



        return convertView;
    }
}
