package com.example.gidevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ParticipantsAdapter extends ArrayAdapter<String> {
    public ParticipantsAdapter(Context context, List<String> participants) {
        super(context, 0, participants);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.participant_display, parent, false);
        }
        String participant = getItem(position);
        TextView participantTextView = convertView.findViewById(R.id.participant_name);
        assert participant != null;
        participantTextView.setText(participant);
        return convertView;
    }
}
