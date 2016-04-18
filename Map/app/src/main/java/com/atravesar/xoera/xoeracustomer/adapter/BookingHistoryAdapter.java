package com.atravesar.xoera.xoeracustomer.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.atravesar.xoera.xoeracustomer.activity.R;
import com.atravesar.xoera.xoeracustomer.entity.JourneyHistory;

import java.util.ArrayList;

/**
 * Created by ITACHI on 3/27/2016.
 */
public class BookingHistoryAdapter extends ArrayAdapter<JourneyHistory> implements View.OnClickListener{
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
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            LayoutInflater inflater =
                    context.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
            vh.status = (TextView) convertView.findViewById(R.id.status);
            vh.statusValue = (TextView) convertView.findViewById(R.id.booking_id);
            vh.when = (TextView) convertView.findViewById(R.id.date_time_history);
            vh.pickUpHistory = (TextView) convertView.findViewById(R.id.pick_up_history_value);
            vh.dropOffHistory = (TextView) convertView.findViewById(R.id.drop_off_history_value);
            vh.imgMenu = (ImageView) convertView.findViewById(R.id.imgMenu);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (myArray.size() > 0 && position >= 0 && (myArray.size() > position)) {
            try {
                JourneyHistory journeyHistory = myArray.get(position);
                Log.e("journeyHistory", journeyHistory.toString());
                if (journeyHistory != null) {
                    vh.status.setText(journeyHistory.getStatus());
                    vh.pickUpHistory.setText(journeyHistory.getPickupAddress());
                    vh.dropOffHistory.setText(journeyHistory.getDropoffAddress());
                    vh.when.setText(journeyHistory.getPickupDateTime());
                    vh.statusValue.setText("#" + journeyHistory.getJobPartID());
                    vh.imgMenu.setOnClickListener(this);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("journeyHistoryException", e.getLocalizedMessage());
            }
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imgMenu){
            showPopupMenu(v);
        }
    }
    public void showPopupMenu(View view){
        PopupMenu popup = new PopupMenu(context,view);
        final MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu_history_booking, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_repeat_journey:
                        Toast.makeText(context, "repeat journey", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_return_journey:
                        Toast.makeText(context,"return journey",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_cancel_booking:
                        Toast.makeText(context,"cancel booking",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }
    static class ViewHolder {
        TextView status;
        TextView statusValue;
        TextView when;
        TextView pickUpHistory;
        TextView dropOffHistory;
        ImageView imgMenu;
    }
}
