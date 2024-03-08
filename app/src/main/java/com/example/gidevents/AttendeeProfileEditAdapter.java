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

/**
 * Custom array adapter for AttendeeProfileEditOption objects
 */
public class AttendeeProfileEditAdapter extends ArrayAdapter<AttendeeProfileEditOption> {

    public AttendeeProfileEditAdapter(Context context, ArrayList<AttendeeProfileEditOption> options) {
        super(context, 0, options);
    }

    /**
     * assigns data from arraylist to different views to be displayed
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return view to be displayed
     */
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
