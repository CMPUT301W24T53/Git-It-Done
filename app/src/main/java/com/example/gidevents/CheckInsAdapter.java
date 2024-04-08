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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.check_ins_list_display, parent, false);
        }
        Map<String,Object> participant = data.get(position);
        TextView usernameTextView = convertView.findViewById(R.id.participant_name);
        TextView timestampTextView = convertView.findViewById(R.id.timestamp_view);
        TextView numCheckinTextView = convertView.findViewById(R.id.num_of_check_ins);
        String username;
        String timeStamp;
        Long checkIns;

        if ((String)participant.get("username") == null || (String)participant.get("username")== "")
        {
            username = "None";
        }
        else {
            username = (String)participant.get("username");
        }

        if ((String)participant.get("timeStamp") == null ||(String)participant.get("timeStamp") == "" ){
            timeStamp = "None";
        }
        else {
            timeStamp = (String)participant.get("timeStamp");
        }

        if (participant.get("numOfCheckIns") == null){
            checkIns = 0L;
        }
        else {
            checkIns = (Long) participant.get("numOfCheckIns");
        }

        assert participant != null;
        usernameTextView.setText("User Name: " + username);
        timestampTextView.setText("Time Stamp: " + timeStamp);
        numCheckinTextView.setText("Number Of Check Ins: " + checkIns);



        return convertView;
    }
}
