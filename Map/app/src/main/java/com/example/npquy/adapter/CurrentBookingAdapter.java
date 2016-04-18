package com.example.npquy.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.npquy.activity.BookingActivity;
import com.example.npquy.activity.R;
import com.example.npquy.entity.Address;
import com.example.npquy.entity.JourneyHistory;
import com.example.npquy.entity.SaveBooking;
import com.example.npquy.service.WebServiceTaskManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import flexjson.JSONSerializer;


public class CurrentBookingAdapter extends ArrayAdapter<JourneyHistory> implements View.OnClickListener{
    private Activity context;
    private ArrayList<JourneyHistory> myArray = new ArrayList<>();
    private int layoutId;

    private int mYear, mMonth, mDay, mHour, mMinute, hours, minutes;
    private Date dateBook;

    public CurrentBookingAdapter(Activity context, int resource, ArrayList<JourneyHistory> arr) {
        super(context, resource);
        this.context = context;
        this.layoutId = resource;
        this.myArray = arr;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater =
                    context.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
            viewHolder = new ViewHolder();
            viewHolder.bookingDate = (TextView)
                    convertView.findViewById(R.id.booking_current_date);
            viewHolder.paymentType = (TextView)convertView.findViewById(R.id.payment_type);
            viewHolder.pickUpED = (TextView) convertView.findViewById(R.id.pick_up_current_booking);
            viewHolder.dropUpED = (TextView) convertView.findViewById(R.id.drop_off_current_booking);
            viewHolder.price = (TextView) convertView.findViewById(R.id.current_booking_price);
            viewHolder.menu = (ImageView) convertView.findViewById(R.id.imgMenu);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (myArray.size() > 0 && position >= 0 && (myArray.size() > position)) {
            JourneyHistory journeyHistory = myArray.get(position);
            if (journeyHistory != null) {
                viewHolder.bookingDate.setText(journeyHistory.getPickupDateTime());
                viewHolder.paymentType.setText(journeyHistory.getPaymentMethod());
                viewHolder.pickUpED.setText(journeyHistory.getPickupAddress());
                viewHolder.dropUpED.setText(journeyHistory.getDropoffAddress());
                viewHolder.price.setText("£" + journeyHistory.getTotalFare());
                viewHolder.menu.setOnClickListener(this);
            }
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        Log.d("sfsd", "imgmenu" + v);
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
                        repeatJourney();
                        Toast.makeText(context, "Can't repeat journey",Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.menu_return_journey:
                        //returnJourney(saveBooking);
                        Toast.makeText(context, "Can't return journey",Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.menu_cancel_booking:
                       // cancelBooking();
                        Toast.makeText(context, "Cancel booking is fail",Toast.LENGTH_LONG).show();
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

    private void cancelBooking() {
        String url = WebServiceTaskManager.URL + "CancelBooking";

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, context, "") {

            @Override
            public void handleResponse(String response) {
                Log.e("response", response, null);
            }
        };

        wst.addNameValuePair("BookingRef", "");
        Log.d("send data", url);
        wst.execute(new String[]{url});
    }

    private void showDateTime() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateBook = new Date(year, monthOfYear + 1, dayOfMonth, hours, minutes);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        hours = hourOfDay;
                        minutes = minute;
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void repeatJourney() {
        Intent myIntent = new Intent(context, BookingActivity.class);
        Bundle bundle = new Bundle();
/*        String pickUpJson = new JSONSerializer().exclude("*.class").serialize(
                pickUpAddress);
        String dropOffJson = new JSONSerializer().exclude("*.class").serialize(
                dropOffAddress);
        String retrieveQuoteJson = new JSONSerializer().exclude("*.class").serialize(retrieveQuote);

        Log.e("retrieveQuoteJson", retrieveQuoteJson);
        bundle.putString("pickUpAddress", pickUpJson);
        bundle.putString("dropOffAddress", dropOffJson);
        bundle.putString("retrieveQuote", retrieveQuoteJson);
        bundle.putDouble("totalFare", totalFare);*/
        myIntent.putExtra("data", bundle);
        context.startActivity(myIntent);
    }

    private void returnJourney(SaveBooking saveBooking) {
        String url = WebServiceTaskManager.URL + "SaveBooking";

        Address pickUpAddress = new Address();
        pickUpAddress.setOutcode(saveBooking.getOutcode());
        pickUpAddress.setLongitude(saveBooking.getDoLong());
        pickUpAddress.setLatitude(saveBooking.getDoLat());
        pickUpAddress.setFulladdress(saveBooking.getDoff());

        Address dropOffAddress = new Address();
        dropOffAddress.setLongitude(saveBooking.getPkLong());
        dropOffAddress.setLatitude(saveBooking.getPkLat());
        dropOffAddress.setFulladdress(saveBooking.getPick());


        saveBooking.setPkLat(pickUpAddress.getLatitude());
        saveBooking.setPkLong(pickUpAddress.getLongitude());
        saveBooking.setPick(pickUpAddress.getFulladdress());
        saveBooking.setDoff(dropOffAddress.getFulladdress());
        saveBooking.setOutcode(pickUpAddress.getOutcode());
        saveBooking.setDoLat(dropOffAddress.getLatitude());
        saveBooking.setDoLong(dropOffAddress.getLongitude());

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, context, "") {

            @Override
            public void handleResponse(String response) {
                Log.e("response_SaveBooking", response, null);
            }
        };

        String json = new JSONSerializer().exclude("*.class").serialize(
                saveBooking);
        Log.e("SaveBookingJson", json, null);
        wst.addNameValuePair("", json);

        wst.execute(new String[]{url});
    }
}
