package com.example.gidevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class NotificationsAdapter extends ArrayAdapter<Notification> {
    public NotificationsAdapter(Context context, List<Notification> notificationsList) {
        super(context, 0, notificationsList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notif_display, parent, false);
        }

        Notification notif = getItem(position);
        TextView notifEvent = convertView.findViewById(R.id.event_title);
        TextView notifDate = convertView.findViewById(R.id.notif_date);
        TextView notifDetails = convertView.findViewById(R.id.notif_description);
        assert notif != null;

        notifEvent.setText(notif.getEventTitle());
        notifDate.setText(notif.getDate());
        notifDetails.setText(notif.getNotifInfo());

        return convertView;
    }
}
