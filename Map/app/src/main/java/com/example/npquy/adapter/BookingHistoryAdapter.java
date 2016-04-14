package com.example.npquy.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.npquy.activity.R;

import com.example.npquy.entity.Address;
import com.example.npquy.entity.JourneyHistory;

import java.util.ArrayList;

/**
 * Created by ITACHI on 3/27/2016.
 */
public class BookingHistoryAdapter extends ArrayAdapter<JourneyHistory> {
    Activity context = null;
    ArrayList<JourneyHistory> myArray;
    int layoutId;

    public BookingHistoryAdapter(Activity context, int resource, ArrayList<JourneyHistory> arr) {
        super(context, resource);
        this.context = context;
        this.layoutId = resource;
        this.myArray = arr;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        LayoutInflater inflater =
                context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, null);

        /*if (myArray.size() > 0 && position >= 0 && (myArray.size() > position)) {

        }*/
        TextView status = (TextView)
                convertView.findViewById(R.id.status);
        TextView status_value = (TextView)
                convertView.findViewById(R.id.booking_id);

        TextView when = (TextView)
                convertView.findViewById(R.id.date_time_history);
        TextView pickUpHistory = (TextView)
                convertView.findViewById(R.id.pick_up_history_value);
        TextView dropOffHistory = (TextView)
                convertView.findViewById(R.id.drop_off_history_value);
        if (myArray.size() > 0 && position >= 0 && (myArray.size() > position)) {
            try {
                JourneyHistory journeyHistory = myArray.get(position);
                Log.e("journeyHistory", journeyHistory.toString());
                if (journeyHistory != null) {
                    status.setText(journeyHistory.getStatus());
                    pickUpHistory.setText(journeyHistory.getPickupAddress());
                    dropOffHistory.setText(journeyHistory.getDropoffAddress());
                    when.setText(journeyHistory.getPickupDateTime());
                    status_value.setText("#" + journeyHistory.getJobPartID());
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("journeyHistoryException", e.getLocalizedMessage());
            }
        }
        return convertView;
    }
}
