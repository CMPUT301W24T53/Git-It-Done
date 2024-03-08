package com.example.gidevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AttendeeProfileEditAdapter extends ArrayAdapter<AttendeeProfileEditOption> {
    public AttendeeProfileEditAdapter(Context context, ArrayList<AttendeeProfileEditOption> options) {
        super(context, 0, options);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.attendee_options_element, parent, false);
        } else {
            view = convertView;
        }

        AttendeeProfileEditOption option = getItem(position);

        TextView optionType = view.findViewById(R.id.attendee_option_type);
        TextView currentValue = view.findViewById(R.id.attendee_option_current);

        optionType.setText(option.getOptionType());
        currentValue.setText(option.getCurrentvalue());

        return view;
    }
}
