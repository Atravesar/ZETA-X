package com.example.xoeracustomer.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.xoeracustomer.database.UserDb;
import com.example.xoeracustomer.entity.Address;
import com.example.xoeracustomer.entity.ElectronicPayment;
import com.example.xoeracustomer.entity.RetrieveQuote;
import com.example.xoeracustomer.entity.RetrieveQuoteResult;
import com.example.xoeracustomer.entity.SaveBooking;
import com.example.xoeracustomer.entity.User;
import com.example.xoeracustomer.service.CustomEditText;
import com.example.xoeracustomer.service.DrawableClickListener;
import com.example.xoeracustomer.service.WebServiceTaskManager;
import com.example.xoeracustomer.service.volley.Request;
import com.example.xoeracustomer.service.volley.RequestQueue;
import com.example.xoeracustomer.service.volley.Response;
import com.example.xoeracustomer.service.volley.VolleyError;
import com.example.xoeracustomer.service.volley.toolbox.JsonObjectRequest;
import com.example.xoeracustomer.service.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Override;
import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class BookingActivity extends AppCompatActivity implements
        View.OnClickListener {
    private EditText dateTime, phoneNumber, mPhoneNumber, note, name, pay_by, billingPostcodeTv, expiryTv, cvvTv;
    private int mYear, mMonth, mDay, mHour, mMinute, hours, minutes;
    private CustomEditText viaAdd, dropOff, pickUp;
    private LinearLayout confirmBooking, totalBooking;
    private Switch waitAndReturn, childSeat, pet, eco;
    private AutoCompleteTextView mEmailView, signUpEmail, cardNumberTv;
    private Menu menu;

    private SaveBooking saveBooking;

    private Address pickUpAddress, dropOffAddress, viaAddress;

    private TextView people, luggage;
    private TextView total, confirmTv1, confirmTv2;

    private int num_people, num_luggage;
    private Date dateBook;

    private Boolean isWaitAndReturn = false;
    private Boolean isChildSeat = false;
    private Boolean isPet = false;
    private Boolean isEco = false;
    private UserDb userDb;

    private String payType, message;
    private RetrieveQuote retrieveQuote;
    private RetrieveQuoteResult retrieveQuoteResult;

    private Double totalFare;

    private User user;
    private LinearLayout carLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        config();

        userDb = new UserDb(this);
        User userCurrent = userDb.getCurrentUser();
        if (userCurrent != null) {
            user = userCurrent;
        } else {
            user = new User();
        }

        Intent callerIntent = getIntent();
        if (callerIntent != null) {
            Bundle packageFromCaller =
                    callerIntent.getBundleExtra("data");
            String pickUpJson = packageFromCaller.getString("pickUpAddress");
            pickUpAddress = new JSONDeserializer<Address>().use(null,
                    Address.class).deserialize(pickUpJson);
            String dropOffJson = packageFromCaller.getString("dropOffAddress");
            dropOffAddress = new JSONDeserializer<Address>().use(null,
                    Address.class).deserialize(dropOffJson);
            String retrieveQuoteJson = packageFromCaller.getString("retrieveQuote");
            retrieveQuote = new JSONDeserializer<RetrieveQuote>().use(null,
                    RetrieveQuote.class).deserialize(retrieveQuoteJson);
            Log.e("retrieveQuote", retrieveQuote.toString());

            totalFare = packageFromCaller.getDouble("totalFare");
            total.setText("£" + totalFare);
            dateTime.setInputType(InputType.TYPE_NULL);
            pickUp.setText(pickUpAddress.getFulladdress());
            dropOff.setText(dropOffAddress.getFulladdress());
        }
        retrieveQuote.setPaq(Integer.parseInt(luggage.getText().toString()));
        retrieveQuote.setBags(Integer.parseInt(people.getText().toString()));
        handleListener();
    }

    private void handleListener() {
        carLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCarBox(BookingActivity.this);
            }
        });
        dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
        dateTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDateTimePicker();
                }
            }
        });
        pickUp.setOnClickListener(this);
        pickUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    pickLocation(1);
                }
            }
        });
        dropOff.setOnClickListener(this);
        dropOff.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    pickLocation(2);
                }
            }
        });
        viaAdd.setOnClickListener(this);
        viaAdd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    pickLocation(3);
                }
            }
        });
        viaAdd.setDrawableClickListener(new DrawableClickListener() {

            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        viaAdd.setVisibility(View.GONE);
                        viaAddress = null;
                        viaAdd.setText("");
                        retrieveQuote.setVia(null);
                        retrieveQuote.setViaLat(null);
                        retrieveQuote.setViaLong(null);
                        retrieveQuote.setViapostcode(null);
                        doChange();
                        break;

                    default:
                        break;
                }
            }
        });
        dropOff.setDrawableClickListener(new DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        viaAdd.setVisibility(View.VISIBLE);
                        break;

                    default:
                        break;
                }
            }

        });

        pickUp.setDrawableClickListener(new DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        if (pickUpAddress != null && dropOffAddress != null) {
                            Address address = pickUpAddress.clone();
                            pickUpAddress = dropOffAddress;
                            pickUp.setText(pickUpAddress.getFulladdress());
                            dropOffAddress = address;
                            dropOff.setText(dropOffAddress.getFulladdress());

                            retrieveQuote.setDoff(dropOffAddress.getFulladdress());
                            retrieveQuote.setDoffLat(dropOffAddress.getLatitude());
                            retrieveQuote.setDoffLong(dropOffAddress.getLongitude());
                            retrieveQuote.setDroppostcode(dropOffAddress.getPostcode());
                            retrieveQuote.setPick(pickUpAddress.getFulladdress());
                            retrieveQuote.setPickLat(pickUpAddress.getLatitude());
                            retrieveQuote.setPickLong(pickUpAddress.getLongitude());
                            retrieveQuote.setPickpostcode(pickUpAddress.getPostcode());
                            doChange();
                        }
                        break;

                    default:
                        break;
                }
            }

        });

        confirmBooking.setOnClickListener(this);
        pay_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogPayment(BookingActivity.this);
            }
        });
        pay_by.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openDialogPayment(BookingActivity.this);
                }
            }
        });

        waitAndReturn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isWaitAndReturn = isChecked;
                if(isChecked) {
                    retrieveQuote.setRtnType("w&r");
                }else {
                    retrieveQuote.setRtnType(null);
                }
            }
        });
        childSeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isChildSeat = isChecked;
                retrieveQuote.setChildseat(isChecked);
            }
        });

    }

    private void config() {
        carLayout = (LinearLayout) findViewById(R.id.car);
        dateTime = (EditText) findViewById(R.id.date_time);
        dateTime.setInputType(InputType.TYPE_NULL);
        people = (TextView) findViewById(R.id.people_booking);
        luggage = (TextView) findViewById(R.id.luggage_booking);
        pickUp = (CustomEditText) findViewById(R.id.pick_up_booking);
        dropOff = (CustomEditText) findViewById(R.id.drop_off_booking);
        viaAdd = (CustomEditText) findViewById(R.id.via_address_booking);
        confirmBooking = (LinearLayout) findViewById(R.id.book_booking);
        totalBooking = (LinearLayout) findViewById(R.id.total_booking);
        total = (TextView) findViewById(R.id.tv_total_booking);
        confirmTv1 = (TextView) findViewById(R.id.tv_book_booking1);
        confirmTv2 = (TextView) findViewById(R.id.tv_book_booking2);
        waitAndReturn = (Switch) findViewById(R.id.w8);
        childSeat = (Switch) findViewById(R.id.child_seat);
        note = (EditText) findViewById(R.id.content_note);

        pay_by = (EditText) findViewById(R.id.pay_by_edit);
        pay_by.setInputType(InputType.TYPE_NULL);

        pickUp.setInputType(InputType.TYPE_NULL);
        dropOff.setInputType(InputType.TYPE_NULL);
        viaAdd.setInputType(InputType.TYPE_NULL);
    }

    private void showDateTimePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(BookingActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateBook = new Date(year, monthOfYear, dayOfMonth, hours, minutes);
                        dateTime.setText(dateBook.toString());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String date = sdf.format(dateBook);
                        retrieveQuote.setBookingdate(date);
                        doChange();
                    }
                }, mYear, mMonth, mDay);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(BookingActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        hours = hourOfDay;
                        minutes = minute;

                        datePickerDialog.show();
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void pickLocation(int type) {
        Intent myIntent = new Intent(BookingActivity.this, GetAddressActivity.class);
        Bundle bundle = new Bundle();
        if (pickUpAddress != null) {
            bundle.putString("postCode", pickUpAddress.getPostcode());
        } else {
            bundle.putString("postCode", "");
        }
        myIntent.putExtra("data", bundle);
        startActivityForResult(myIntent, type);
    }

    /**
     * Show dialog picking car
     *
     * @param context
     */
    private void openCarBox(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.car_selection);

        ImageView dialogButton = (ImageView) dialog.findViewById(R.id.imageView_close);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        LinearLayout salonLayout = (LinearLayout) dialog.findViewById(R.id.salon_layout);
        final ImageView salon_tick = (ImageView) dialog.findViewById(R.id.salon_tick);

        final LinearLayout mvp_layout = (LinearLayout) dialog.findViewById(R.id.mvp_layout);
        final ImageView mvp_tick = (ImageView) dialog.findViewById(R.id.mvp_tick);

        LinearLayout executive_layout = (LinearLayout) dialog.findViewById(R.id.executive_layout);
        final ImageView executive_tick = (ImageView) dialog.findViewById(R.id.executive_tick);

        LinearLayout estate_layout = (LinearLayout) dialog.findViewById(R.id.estate_layout);
        final ImageView estate_tick = (ImageView) dialog.findViewById(R.id.estate_tick);

        salonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideImage(mvp_tick, executive_tick, salon_tick, estate_tick);
                salon_tick.setVisibility(View.VISIBLE);
                num_people = 4;
                num_luggage = 2;
            }
        });

        mvp_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideImage(mvp_tick, executive_tick, salon_tick, estate_tick);
                mvp_tick.setVisibility(View.VISIBLE);
                num_people = 6;
                num_luggage = 5;
            }
        });

        executive_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideImage(mvp_tick, executive_tick, salon_tick, estate_tick);
                executive_tick.setVisibility(View.VISIBLE);
                num_people = 4;
                num_luggage = 2;
            }
        });

        estate_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideImage(mvp_tick, executive_tick, salon_tick, estate_tick);
                estate_tick.setVisibility(View.VISIBLE);
                num_people = 4;
                num_luggage = 3;
            }
        });

        Button chooseCar = (Button) dialog.findViewById(R.id.choose_car);
        chooseCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num_people != 0 && num_luggage != 0) {
                    people.setText(num_people + "");
                    luggage.setText(num_luggage + "");
                    retrieveQuote.setPaq(num_people);
                    retrieveQuote.setBags(num_luggage);
                    doChange();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public void onClick(View v) {
        if (v == pickUp) {
            pickLocation(1);
        } else if (v == dropOff) {
            pickLocation(2);
        } else if (v == viaAdd) {
            pickLocation(3);
        } else if (v == confirmBooking) {
            if (dateBook == null) {
                Toast.makeText(BookingActivity.this, "Please select the time to booking car", Toast.LENGTH_LONG).show();
            } else {
                if (retrieveQuoteResult != null) {
                    saveBookingFunction();
                } else {
                    postQuotationBeforeBooking(retrieveQuote);
                }
            }
        }
    }

    private void hideImage(ImageView imageView1, ImageView imageView2, ImageView imageView3, ImageView imageView4) {
        imageView1.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);
        imageView4.setVisibility(View.INVISIBLE);
    }

    private void beforePostData() {
        confirmTv1.setTextColor(Color.WHITE);
        confirmTv2.setTextColor(Color.WHITE);
        confirmBooking.setClickable(false);
    }

    private void afterPostData() {
        confirmTv1.setTextColor(Color.parseColor("#00CCCC"));
        confirmTv2.setTextColor(Color.parseColor("#00CCCC"));
        confirmBooking.setClickable(true);
    }


    private void saveBookingFunction() {
        User userDbCurrentUser = userDb.getCurrentUser();
        if (userDbCurrentUser == null || userDbCurrentUser.getCusID() == null) {
            openDialogSignIn(this);
        } else {
            int currentUserId = Integer.parseInt(userDbCurrentUser.getCusID());
            ElectronicPayment electronicPayment = new ElectronicPayment();
            electronicPayment.setAmount(totalFare);
            electronicPayment.setCustID(currentUserId);
            postElectronicPayment(electronicPayment);
            Log.e("RetrieveQuoteResult", retrieveQuoteResult.toString());
            saveBooking = new SaveBooking();
            saveBooking.setCustid(currentUserId);
            saveBooking.setRoutedistance(retrieveQuoteResult.getRoutedistance());
            saveBooking.setVehTypeID(retrieveQuoteResult.getVehTypeID());
            saveBooking.setTravelTime(retrieveQuoteResult.getTraveltime());
            saveBooking.setTotalfare(totalFare);
            saveBooking.setFare(Double.parseDouble(retrieveQuoteResult.getFare()));
            saveBooking.setPick(pickUpAddress.getFulladdress());
            saveBooking.setDoff(dropOffAddress.getFulladdress());
            if (viaAddress != null) {
                saveBooking.setVia(viaAddress.getFulladdress());
                saveBooking.setViaLat(viaAddress.getLatitude());
                saveBooking.setViaLong(viaAddress.getLongitude());
            }
            saveBooking.setPkLat(pickUpAddress.getLatitude());
            saveBooking.setPkLong(pickUpAddress.getLongitude());
            saveBooking.setPaq(Integer.parseInt(people.getText().toString()));
            saveBooking.setBags(Integer.parseInt(luggage.getText().toString()));
            saveBooking.setOutcode(pickUpAddress.getOutcode());
            saveBooking.setVehType(retrieveQuoteResult.getVehType());
            saveBooking.setRjType(":");
            saveBooking.setNote(note.getText().toString());
            saveBooking.setDoLat(dropOffAddress.getLatitude());
            saveBooking.setDoLong(dropOffAddress.getLongitude());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String date = sdf.format(dateBook);
            saveBooking.setBookingdate(date);
            saveBooking.setInServiceArea(true);
            getSaveBooking();
        }
    }


    private void openDialogSignIn(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_login);

        ImageView dialogButton = (ImageView) dialog.findViewById(R.id.imageView_close);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);

        TextView createAccount = (TextView) dialog.findViewById(R.id.create_account);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openDialogSignUp(BookingActivity.this);
            }
        });

        Button mEmailSignInButton = (Button) dialog.findViewById(R.id.email_sign_in_button);
        mEmailView = (AutoCompleteTextView) dialog.findViewById(R.id.email_sign_up_string);

        phoneNumber = (EditText) dialog.findViewById(R.id.phone_number);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setEmail(mEmailView.getText().toString());
                user.setMobile(phoneNumber.getText().toString());
                String android_id = Settings.Secure.getString(BookingActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                user.setDeviceID(android_id);
                login();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void login() {

        String url = WebServiceTaskManager.URL + "SignIn";

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "") {

            @Override
            public void handleResponse(String response) {
                Log.e("response_login", response, null);
                try {
                    JSONObject root = new JSONObject(response);
                    int code = root.getInt("code");
                    message = root.getString("message");
                    if (code == 1) {
                        String cusId = root.getString("CustID");
                        if (cusId != null) {
                            Log.e("CusId", cusId, null);
                            user.setCusID(cusId);
                            userDb.login(user);
                        }
                    } else {
                        BookingActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(BookingActivity.this, message, Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("Exception Sign Up", e.getLocalizedMessage());
                }
            }
        };

        String json = new JSONSerializer().exclude("name", "*.class").serialize(
                user);
        Log.e("json", json, null);
        wst.addNameValuePair("", json);

        wst.execute(new String[]{url});

    }

    private void doChange() {
        postQuotation(retrieveQuote);
    }

    private void openDialogSignUp(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_sign_up);

        ImageView dialogButton = (ImageView) dialog.findViewById(R.id.imageView_close);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView signIn = (TextView) dialog.findViewById(R.id.login_from_sign_in);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openDialogSignIn(BookingActivity.this);
            }
        });

        Button signUpButton = (Button) dialog.findViewById(R.id.sign_up_button);
        signUpEmail = (AutoCompleteTextView) dialog.findViewById(R.id.email_sign_up);
        name = (EditText) dialog.findViewById(R.id.name);
        mPhoneNumber = (EditText) dialog.findViewById(R.id.phone_number_sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setEmail(signUpEmail.getText().toString());
                user.setName(name.getText().toString());
                user.setMobile(mPhoneNumber.getText().toString());
                String android_id = Settings.Secure.getString(BookingActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                user.setDeviceID(android_id);
                signUp(user);

                dialog.dismiss();
                openDialogSignIn(BookingActivity.this);
            }
        });

        dialog.show();
    }

    private void hideImage(ImageView imageView1, ImageView imageView2) {
        imageView1.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
    }

    private void openDialogPayment(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_payment);

        dialog.setCanceledOnTouchOutside(true);
        cardNumberTv = (AutoCompleteTextView) dialog.findViewById(R.id.card_number);
        expiryTv = (EditText) dialog.findViewById(R.id.expiry);
        billingPostcodeTv = (EditText) dialog.findViewById(R.id.billing);
        cvvTv = (EditText) dialog.findViewById(R.id.cvv);
        final LinearLayout payCashLayout = (LinearLayout) dialog.findViewById(R.id.pay_cash_layout);
        final ImageView cash_tick = (ImageView) dialog.findViewById(R.id.cash_tick);

        LinearLayout payCardLayout = (LinearLayout) dialog.findViewById(R.id.pay_card_layout);
        final ImageView card_tick = (ImageView) dialog.findViewById(R.id.card_tick);

        payCashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideImage(cash_tick, card_tick);
                cash_tick.setVisibility(View.VISIBLE);
                payType = "Cash";
            }
        });

        payCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideImage(cash_tick, card_tick);
                card_tick.setVisibility(View.VISIBLE);
                payType = "Card";
            }
        });

        Button paymentButton = (Button) dialog.findViewById(R.id.payment_button);
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!payType.isEmpty()) {
                    pay_by.setText(payType);
                }
                String cardNumber = cardNumberTv.getText().toString();
                String billingPostcode = billingPostcodeTv.getText().toString();
                String cvv = cvvTv.getText().toString();
                String expiry = expiryTv.getText().toString();

                dialog.dismiss();
            }
        });

        ImageView dialogButton = (ImageView) dialog.findViewById(R.id.imageView_close);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ElectronicPayment electronicPayment = new ElectronicPayment();
        electronicPayment.setNonce(null);
        electronicPayment.setAmount(0.0);
        electronicPayment.setCustID(0);
        postElectronicPayment(electronicPayment);
        dialog.show();
    }

    private void signUp(User user) {

        String url = WebServiceTaskManager.URL + "SignUp";

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "") {

            @Override
            public void handleResponse(String response) {
                Log.e("response", response, null);
            }
        };

        String json = new JSONSerializer().exclude("cusID", "*.class").serialize(
                user);
        Log.e("json", json, null);
        wst.addNameValuePair("", json);

        wst.execute(new String[]{url});

    }

    private void postElectronicPayment(ElectronicPayment electronicPayment) {
        String url = WebServiceTaskManager.URL + "ElectronicPayment";

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "") {

            @Override
            public void handleResponse(String response) {
                Log.e("response_postElectronic", response, null);
            }
        };

        String json = new JSONSerializer().exclude("*.class").serialize(
                electronicPayment);
        wst.addNameValuePair("", json);

        wst.execute(new String[]{url});
    }


    private void postQuotation(RetrieveQuote retrieveQuote) {
        totalFare = 0.00;
        total.setText("£" + totalFare);
        String url = WebServiceTaskManager.URL + "Quotation";
        beforePostData();
        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "") {

            @Override
            public void handleResponse(String response) {
                try {
                    retrieveQuoteResult = new JSONDeserializer<RetrieveQuoteResult>().use(null,
                            RetrieveQuoteResult.class).deserialize(response);
                    Log.e("RetrieveQuoteResult", retrieveQuoteResult.toString());
                    if (retrieveQuoteResult != null) {
                        totalFare = Double.parseDouble(retrieveQuoteResult.getTotalfare().trim());
                        total.setText("£" + totalFare);
                        afterPostData();
                    }
                } catch (Exception e) {
                    Log.e("post Quotation", e.getLocalizedMessage());
                }
            }
        };

        String json = new JSONSerializer().exclude("*.class").serialize(
                retrieveQuote);
        wst.addNameValuePair("", json);

        wst.execute(new String[]{url});

    }

    private void postQuotationBeforeBooking(RetrieveQuote retrieveQuote) {

        totalFare = 0.00;
        total.setText("£" + totalFare);
        String url = WebServiceTaskManager.URL + "Quotation";

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject retrieveQuoteJson = null;
        try {
            String json = new JSONSerializer().exclude("*.class").serialize(
                    retrieveQuote);
            retrieveQuoteJson = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSONException", e.getLocalizedMessage());
        }
        if (retrieveQuoteJson == null) {
            return;
        }
        JsonObjectRequest requestQuotation = new JsonObjectRequest(Request.Method.POST, url, retrieveQuoteJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response_quotation", response.toString(), null);
                if (response != null) {
                    try {
                        retrieveQuoteResult = new JSONDeserializer<RetrieveQuoteResult>().use(null,
                                RetrieveQuoteResult.class).deserialize(response.toString());
                        Log.e("RetrieveQuoteResult", retrieveQuoteResult.toString());
                        if (retrieveQuoteResult != null) {
                            totalFare = Double.parseDouble(retrieveQuoteResult.getTotalfare().trim());
                            total.setText("£" + totalFare);
                            saveBookingFunction();
                        }
                    } catch (Exception e) {
                        Log.e("post Quotation", e.getLocalizedMessage());
                    }
                }
                afterPostData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Exception", "Post data to /Quotation is failure!");
            }
        });
        queue.add(requestQuotation);

    }

    private void getSaveBooking() {
        String url = WebServiceTaskManager.URL + "SaveBooking";
        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "Saving Booking") {

            @Override
            public void handleResponse(String response) {
                Log.e("response_SaveBooking", response, null);
                try {
                    JSONObject root = new JSONObject(response);
                    if (root == null) {
                        return;
                    }
                    String message = root.getString("message");
                    String code = root.getString("code");
                    if (code.equals("1")) {
                        Intent myIntent = new Intent(BookingActivity.this, BookingSaved.class);

                        Bundle bundle = new Bundle();
                        String savedBooking = new JSONSerializer().exclude("*.class").serialize(
                                saveBooking);
                        bundle.putString("savedBooking", savedBooking);
                        myIntent.putExtra("data", bundle);
                        startActivity(myIntent);
                    } else {
                        if (message.length() < 50) {
                            Toast.makeText(BookingActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BookingActivity.this, "Booking not success", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    Log.e("Error", e.getLocalizedMessage(), e);
                }
            }
        };

        String json = new JSONSerializer().exclude("*.class").serialize(
                saveBooking);
        Log.e("SaveBookingJson", json, null);
        wst.addNameValuePair("", json);

        wst.execute(new String[]{url});
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data.hasExtra("pickUp")) {
                Address address = (Address) data.getExtras().get("pickUp");
                pickUp.setText(address.getFulladdress());
                pickUpAddress = address;
                retrieveQuote.setPick(pickUpAddress.getFulladdress());
                retrieveQuote.setPickLat(pickUpAddress.getLatitude());
                retrieveQuote.setPickLong(pickUpAddress.getLongitude());
                retrieveQuote.setPickpostcode(pickUpAddress.getPostcode());
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data.hasExtra("dropOff")) {
                Address address = (Address) data.getExtras().get("dropOff");
                dropOff.setText(address.getFulladdress());
                dropOffAddress = address;
                retrieveQuote.setDoff(dropOffAddress.getFulladdress());
                retrieveQuote.setDoffLat(dropOffAddress.getLatitude());
                retrieveQuote.setDoffLong(dropOffAddress.getLongitude());
                retrieveQuote.setDroppostcode(dropOffAddress.getPostcode());
            }
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            if (data.hasExtra("viaAdd")) {
                Address address = (Address) data.getExtras().get("viaAdd");
                if (address != null) {
                    Log.e("address", address.toString());
                    viaAdd.setText(address.getFulladdress());
                    viaAddress = address;
                    retrieveQuote.setVia(viaAddress.getFulladdress());
                    retrieveQuote.setViaLat(viaAddress.getLatitude());
                    retrieveQuote.setViaLong(viaAddress.getLongitude());
                    retrieveQuote.setViapostcode(viaAddress.getPostcode());
                }
            }
        }
        doChange();
    }
}
