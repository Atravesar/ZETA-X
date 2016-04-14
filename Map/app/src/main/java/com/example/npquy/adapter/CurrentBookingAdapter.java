package com.example.npquy.adapter;

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

import com.example.npquy.activity.R;
import com.example.npquy.entity.JourneyHistory;

import java.util.ArrayList;


public class CurrentBookingAdapter extends ArrayAdapter<JourneyHistory> implements View.OnClickListener{
    private Activity context;
    private ArrayList<JourneyHistory> myArray = new ArrayList<>();
    private int layoutId;


    public CurrentBookingAdapter(Activity context, int resource, ArrayList<JourneyHistory> arr) {
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
            LayoutInflater inflater =
                    context.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
            vh = new ViewHolder();
            vh.bookingDate = (TextView)
                    convertView.findViewById(R.id.booking_current_date);
            vh.paymentType = (TextView)convertView.findViewById(R.id.payment_type);
            vh.pickUpED = (TextView) convertView.findViewById(R.id.pick_up_current_booking);
            vh.dropUpED = (TextView) convertView.findViewById(R.id.drop_off_current_booking);
            vh.price = (TextView) convertView.findViewById(R.id.current_booking_price);
            vh.menu = (ImageView) convertView.findViewById(R.id.imgMenu);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        if (myArray.size() > 0 && position >= 0 && (myArray.size() > position)) {
            JourneyHistory journeyHistory = myArray.get(position);
            if (journeyHistory != null) {
                vh.bookingDate.setText(journeyHistory.getPickupDateTime());
                vh.paymentType.setText(journeyHistory.getPaymentMethod());
                vh.pickUpED.setText(journeyHistory.getPickupAddress());
                vh.dropUpED.setText(journeyHistory.getDropoffAddress());
                vh.price.setText("£" + journeyHistory.getTotalFare());
                vh.menu.setOnClickListener(this);
            }
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        Log.d("sfsd","imgmenu"+v);
        if (v.getId()== R.id.imgMenu){
            showPopupMenu(v);
        }
    }
    public void showPopupMenu(View view){
        PopupMenu popup = new PopupMenu(context,view);
        final MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_repeat_journey:
                        Toast.makeText(context,"repeat journey",Toast.LENGTH_SHORT).show();
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
        TextView bookingDate;
        TextView paymentType;
        TextView pickUpED;
        TextView dropUpED;
        TextView price;
        ImageView menu;
    }
}
