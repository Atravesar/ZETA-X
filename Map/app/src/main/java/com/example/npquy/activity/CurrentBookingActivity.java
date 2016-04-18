package com.example.npquy.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import com.example.npquy.adapter.CurrentBookingAdapter;
import com.example.npquy.database.UserDb;
import com.example.npquy.entity.Address;
import com.example.npquy.entity.JourneyHistory;
import com.example.npquy.entity.SaveBooking;
import com.example.npquy.entity.User;
import com.example.npquy.service.WebServiceTaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class CurrentBookingActivity extends AppCompatActivity {

    private EditText pickUp;
    private EditText dropOff;
    private SaveBooking saveBooking;
    private UserDb userDb;


    private Date dateBook;
    private int mYear, mMonth, mDay, mHour, mMinute, hours, minutes;

    private CurrentBookingAdapter currentBookingAdapter;

    private ArrayList<JourneyHistory> journeyHistories = new ArrayList<>();

    private ListView currentBookingListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_booking);
      /*  pickUp = (EditText) findViewById(R.id.pick_up_detail);
        dropOff = (EditText) findViewById(R.id.drop_off_detail);*/

        userDb = new UserDb(this);
        currentBookingListView = (ListView) findViewById(R.id.current_booking);

        User user = userDb.getCurrentUser();
        getCurrentBooking(user.getCusID(), user.getDeviceID());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.repeat_journey: {
                repeatJourney(saveBooking);
                return true;
            }
            case R.id.return_journey: {
                returnJourney(saveBooking);
                return true;
            }
            case R.id.cancel_booking: {
                cancelBooking();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getCurrentBooking(String cusId, String deviceId) {
        String url = WebServiceTaskManager.URL + "CurrentBookings";

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.GET_TASK, this, "") {

            @Override
            public void handleResponse(String response) {
                Log.e("response", response, null);
      /*         */
                try {
                    JSONObject root = new JSONObject(response);
                    JSONArray journeyHistoryJsonArray = root.getJSONArray("jlist");
                    String message = root.getString("message");
                    String code = root.getString("code");
                    ArrayList jouArrayList = new ArrayList();
                    for (int i = 0; i < journeyHistoryJsonArray.length(); i++) {
                        JourneyHistory journeyHistory = new JSONDeserializer<JourneyHistory>().use(null,
                                JourneyHistory.class).deserialize(journeyHistoryJsonArray.get(i).toString());
                        jouArrayList.add(journeyHistory);
                    }
                    journeyHistories.addAll(jouArrayList);
                    currentBookingAdapter = new CurrentBookingAdapter(CurrentBookingActivity.this,
                            R.layout.current_booking_item, journeyHistories);
                    currentBookingAdapter.addAll(jouArrayList);
                    currentBookingListView.setAdapter(currentBookingAdapter);

                } catch (JSONException e) {
                    Log.e("Error", e.getLocalizedMessage(), e);
                }

            }
        };

        wst.addNameValuePair("CustID", cusId);
        wst.addNameValuePair("DeviceID", deviceId);
        wst.execute(new String[]{url});
    }

    private void cancelBooking() {
        String url = WebServiceTaskManager.URL + "CancelBooking";

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "") {

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
        DatePickerDialog datePickerDialog = new DatePickerDialog(CurrentBookingActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateBook = new Date(year, monthOfYear + 1, dayOfMonth, hours, minutes);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        TimePickerDialog timePickerDialog = new TimePickerDialog(CurrentBookingActivity.this,
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

    private void repeatJourney(SaveBooking saveBooking) {
        String url = WebServiceTaskManager.URL + "SaveBooking";
        showDateTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sdf.format(dateBook);
        saveBooking.setBookingdate(date);

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "") {

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

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "") {

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
