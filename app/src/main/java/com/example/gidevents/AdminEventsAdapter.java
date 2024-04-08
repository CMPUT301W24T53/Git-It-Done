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
import java.util.stream.Collectors;

/** This is the EventsAdapter class
 * Sets up the adapter
 */
public class AdminEventsAdapter extends ArrayAdapter<Events> implements Filterable {

    private List<Events> eventsList;
    private List<Events> filteredList;
    //private EventFilter eventFilter;
    private Context context;


    public AdminEventsAdapter(Context context, List<Events> events) {
        super(context, 0, events);
        this.context = context;
        this.eventsList = new ArrayList<>(events);
        this.filteredList = new ArrayList<>(events);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_display, parent, false);
        }

        Events event = getItem(position);
        ImageView posterImageView = convertView.findViewById(R.id.poster);
        TextView titleTextView = convertView.findViewById(R.id.event_title);
        TextView dateTextView = convertView.findViewById(R.id.event_date);
        TextView timeTextView = convertView.findViewById(R.id.event_time);
        TextView locationTextView = convertView.findViewById(R.id.event_location);
        TextView organizerTextView = convertView.findViewById(R.id.event_organizer);
        TextView descriptionTextView = convertView.findViewById(R.id.event_description);

        assert event != null;

        titleTextView.setText(event.getEventTitle());
        dateTextView.setText(event.getEventDate());
        timeTextView.setText(event.getEventTime());
        locationTextView.setText(event.getEventLocation());
        organizerTextView.setText(event.getEventOrganizer());
        descriptionTextView.setText(event.getEventDescription());

        return convertView;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Events getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
//        }
//
//        ImageView imageView = convertView.findViewById(R.id.imageView);
//        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
//        TextView subtitleTextView = convertView.findViewById(R.id.subtitleTextView);
//
//        Item item = filteredList.get(position);
//        imageView.setImageResource(item.getImageResourceId());
//        titleTextView.setText(item.getTitle());
//        subtitleTextView.setText(item.getSubtitle());
//
//        return convertView;
//    }


//    @Override
//    public Filter getFilter() {
//        if (eventFilter == null) {
//            eventFilter = new EventFilter();
//        }
//        return eventFilter;
//    }
//
//    private class EventFilter extends Filter {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            FilterResults results = new FilterResults();
//            if (constraint == null || constraint.length() == 0) {
//                results.values = eventsList;
//                results.count = eventsList.size();
//            } else {
//                List<Events> filteredList = new ArrayList<>();
//                for (Events event : eventsList) {
//                    if (event.getEventTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
//                        filteredList.add(event);
//                    }
//                }
//                results.values = filteredList;
//                results.count = filteredList.size();
//            }
//            return results;
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            filteredList = (ArrayList<Events>) results.values;
//            notifyDataSetChanged();
//        }
//    }
//}

//
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                List<Events> filteredResults;
//                if (constraint == null || constraint.length() == 0) {
//                    filteredResults = eventsList;
//                } else {
//                    String filterPattern = constraint.toString().toLowerCase().trim();
//                    filteredResults = eventsList.stream()
//                            .filter(item -> item.getEventTitle().toLowerCase().contains(filterPattern) ||
//                                    item.getEventOrganizer().toLowerCase().contains(filterPattern))
//                            .collect(Collectors.toList());
//                }
//
//                FilterResults results = new FilterResults();
//                results.values = filteredResults;
//
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                eventsList = (List<Events>) results.values;
//                notifyDataSetChanged();
//            }
//        };
