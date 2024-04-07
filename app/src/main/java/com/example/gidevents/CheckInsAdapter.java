package com.example.gidevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class CheckInsAdapter extends ArrayAdapter<Map<String,Object>> {
    private List<Map<String, Object>> data;
    public CheckInsAdapter(Context context, List<Map<String, Object>> participants) {
        super(context, 0, participants);
        data = participants;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.participant_display, parent, false);
        }
        Map<String,Object> participant = data.get(position);
        TextView usernameTextView = convertView.findViewById(R.id.participant_name);
        TextView timestampTextView = convertView.findViewById(R.id.timestamp_view);
        TextView numCheckinTextView = convertView.findViewById(R.id.num_of_check_ins);
        TextView eventNameTextView = convertView.findViewById(R.id.event_name);

        assert participant != null;
        usernameTextView.setText("User Name: " + participant.get("username").toString());
        timestampTextView.setText("Time Stamp: " + participant.get("timeStamp").toString());
        numCheckinTextView.setText("Number Of Check Ins: " + participant.get("numOfCheckIns").toString());
        eventNameTextView.setText("Event: " + participant.get("eventName").toString());


        return convertView;
    }
}
