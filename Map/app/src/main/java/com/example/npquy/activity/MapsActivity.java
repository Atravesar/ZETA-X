package com.example.npquy.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.npquy.database.UserDb;
import com.example.npquy.entity.Address;
import com.example.npquy.entity.Location;
import com.example.npquy.entity.NearestDriver;
import com.example.npquy.entity.RetrieveQuote;
import com.example.npquy.entity.RetrieveQuoteResult;
import com.example.npquy.entity.User;
import com.example.npquy.service.Const;
import com.example.npquy.service.GPSTracker;
import com.example.npquy.service.SingleServiceTaskManager;
import com.example.npquy.service.StopHandler;
import com.example.npquy.service.WebServiceMethod;
import com.example.npquy.service.WebServiceTaskManager;
import com.example.npquy.service.WebServiceThread;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @author npquy
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener,LocationListener, GpsStatus.Listener {

    private GoogleMap mMap;
    private LocationManager mService;
    private GpsStatus mStatus;

    private EditText pickUp, dropOff, phoneNumber, mPhoneNumber, name;

    private LinearLayout book, total;

    private Boolean isCheck = false;

    private Address pickUpAddress, dropOffAddress;

    private TextView people, luggage, numMinuteDisplayOnMarker, userEmail,tv_total_1,tv_total_2,tv_booking_1,tv_booking_2;

    private LatLng yourLocation, lastLocation, currentLocation, pickUpLocation, defaultLocation;

    private LinearLayout carLayout;

    private NearestDriver yourNearestDriver;
    private NavigationView navigationView = null;
    private Toolbar toolbar = null;

    private ImageView swap, locationImage;
    private Double totalFare;
    private int num_people, num_luggage;
    private RetrieveQuote retrieveQuote;
    private View marker, taxiMarker, homeMarker;
    private AutoCompleteTextView mEmailView, signUpEmail;

    private UserDb userDb;
    private User user;
    private String custId;

    private ProgressBar progressBar;

    private ThreadPoolExecutor threadPoolExecutor;
    private CompletionService<String> pool;

    private int version = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Config before running
        configActivity();
        configListener();


        retrieveQuote = new RetrieveQuote();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Config some listeners
     */
    private void configListener() {
        navigationView.setNavigationItemSelectedListener(MapsActivity.this);
        pickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickLocation(1);
            }
        });
        dropOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickLocation(2);
            }
        });
        dropOff.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    pickLocation(2);
                }
            }
        });
        pickUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    pickLocation(1);
                }
            }
        });

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickUpAddress != null && dropOffAddress != null) {
                    Address address = pickUpAddress.clone();
                    pickUpAddress = dropOffAddress;
                    pickUp.setText(pickUpAddress.getFulladdress());
                    dropOffAddress = address;
                    dropOff.setText(dropOffAddress.getFulladdress());
                    postQuotation();
                }
            }
        });

        carLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCarBox(MapsActivity.this);
            }
        });
    }

    /**
     * Config all field before we use
     */
    private void configActivity() {
        StopHandler.createInstance().setVersion(0);
        String languageToLoad = "en"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mService = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mService.addGpsStatusListener(this);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_maps);
        pickUp = (EditText) findViewById(R.id.pick_up);
        people = (TextView) findViewById(R.id.people);
        luggage = (TextView) findViewById(R.id.luggage);
        dropOff = (EditText) findViewById(R.id.drop_off);
        pickUp.setInputType(InputType.TYPE_NULL);
        dropOff.setInputType(InputType.TYPE_NULL);
        locationImage = (ImageView) findViewById(R.id.pick_up_img);
        progressBar = (ProgressBar) findViewById(R.id.search_location_progress);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setClickable(false);
        userEmail = (TextView) findViewById(R.id.email_user);
        book = (LinearLayout) findViewById(R.id.book);
        tv_booking_1 = (TextView) findViewById(R.id.tv_book_1);
        tv_booking_2 = (TextView) findViewById(R.id.tv_book_2);
        tv_total_1 = (TextView) findViewById(R.id.tv_total_1);
        tv_total_2 = (TextView) findViewById(R.id.tv_total_2);
        total = (LinearLayout) findViewById(R.id.total);
        swap = (ImageView) findViewById(R.id.swap_location);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        carLayout = (LinearLayout) findViewById(R.id.car);
        userDb = new UserDb(this);
        if(userDb.getCurrentUser() != null) {
            user = userDb.getCurrentUser();
        }else {
            user =  new User();
            user.setCusID("0");
        }
      //  marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.taxi_marker_layout, null);
        taxiMarker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.taxi_marker_layout, null);
        numMinuteDisplayOnMarker = (TextView) findViewById(R.id.num_txt);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setVisibleItem();

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                setVisibleItem();
            }
        };
        drawer.setDrawerListener(toggle);
        toolbar.setTitle("Zeta-X");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.logo);
        toggle.syncState();

        threadPoolExecutor = new ThreadPoolExecutor(
                Const.CORE_POOL_SIZE, Const.CORE_POOL_SIZE,
                Const.KeepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        pool = new ExecutorCompletionService<String>(
                threadPoolExecutor);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setVisibleItem();
    }

    /**
     * POST /Quotation -> When you have both the pickup and dropoff addresses you will need to call another web service method at the following endpoint and display the returned quote
     */
    private void postQuotation() {
        beforePostData();
        if (pickUpAddress != null && dropOffAddress != null) {
            String url = WebServiceTaskManager.URL + "Quotation";

            WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "") {

                @Override
                public void handleResponse(String response) {
                    Log.e("response_quotation", response, null);
                    try {
                        RetrieveQuoteResult bookingSaved = new JSONDeserializer<RetrieveQuoteResult>().use(null,
                                RetrieveQuoteResult.class).deserialize(response);
                        Log.e("Booking save", bookingSaved.toString());
                        if (bookingSaved != null && bookingSaved.getTotalfare() != null) {
                            totalFare = Double.parseDouble(bookingSaved.getTotalfare());
                            tv_total_1.setText("£" + totalFare);
                            afterPostData();
                            book.setClickable(bookingSaved.getInServiceArea());
                        }
                    } catch (Exception jsonEx) {
                        Log.e("Json Exception", jsonEx.getLocalizedMessage());
                    }
                }
            };
            retrieveQuote.setCustid(Integer.parseInt(user.getCusID()));
            retrieveQuote.setPick(pickUpAddress.getFulladdress());
            retrieveQuote.setPickLat(pickUpAddress.getLatitude());
            retrieveQuote.setPickLong(pickUpAddress.getLongitude());
            retrieveQuote.setDoffLat(dropOffAddress.getLatitude());
            retrieveQuote.setDoffLong(dropOffAddress.getLongitude());
            retrieveQuote.setDoff(dropOffAddress.getFulladdress());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date today = new Date();
            String date = sdf.format(today);
            retrieveQuote.setBookingdate(date);
            retrieveQuote.setPaq(Integer.parseInt(people.getText().toString()));
            retrieveQuote.setBags(Integer.parseInt(luggage.getText().toString()));
            retrieveQuote.setPickpostcode(pickUpAddress.getPostcode());
            retrieveQuote.setDroppostcode(dropOffAddress.getPostcode());
            String json = new JSONSerializer().exclude("*.class").serialize(
                    retrieveQuote);
            Log.e("Quotation", json, null);
            wst.addNameValuePair("", json);

            wst.execute(new String[]{url});
        }
    }

    private void postQuotationNewWay() {
        beforePostData();
        if (pickUpAddress != null && dropOffAddress != null) {
            String url = WebServiceTaskManager.URL + "Quotation";

            retrieveQuote.setCustid(Integer.parseInt(user.getCusID()));
            retrieveQuote.setPick(pickUpAddress.getFulladdress());
            retrieveQuote.setPickLat(pickUpAddress.getLatitude());
            retrieveQuote.setPickLong(pickUpAddress.getLongitude());
            retrieveQuote.setDoffLat(dropOffAddress.getLatitude());
            retrieveQuote.setDoffLong(dropOffAddress.getLongitude());
            retrieveQuote.setDoff(dropOffAddress.getFulladdress());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date today = new Date();
            String date = sdf.format(today);
            retrieveQuote.setBookingdate(date);
            retrieveQuote.setPaq(Integer.parseInt(people.getText().toString()));
            retrieveQuote.setBags(Integer.parseInt(luggage.getText().toString()));
            retrieveQuote.setPickpostcode(pickUpAddress.getPostcode());
            retrieveQuote.setDroppostcode(dropOffAddress.getPostcode());
            String json = new JSONSerializer().exclude("*.class").serialize(
                    retrieveQuote);
            Log.e("Quotation", json, null);


            WebServiceThread webServiceThread = new WebServiceThread();
            webServiceThread.setTaskType(WebServiceMethod.POST);
            ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("", json));
            webServiceThread.setParams(postParams);
            webServiceThread.setUrl(url);
            pool.submit(webServiceThread);
            threadPoolExecutor.shutdown();
            try {
                String result = pool.take().get();
                if(result != null) {
                    Log.e("response_quotation", result, null);
                    try {
                        RetrieveQuoteResult bookingSaved = new JSONDeserializer<RetrieveQuoteResult>().use(null,
                                RetrieveQuoteResult.class).deserialize(result);
                        Log.e("Booking save", bookingSaved.toString());
                        if (bookingSaved != null && bookingSaved.getTotalfare() != null) {
                            totalFare = Double.parseDouble(bookingSaved.getTotalfare());
                            tv_total_1.setText("£" + totalFare);
                            afterPostData();
                            book.setClickable(bookingSaved.getInServiceArea());
                        }
                    } catch (Exception jsonEx) {
                        Log.e("Json Exception", jsonEx.getLocalizedMessage());
                    }
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private void beforePostData() {
        tv_booking_1.setTextColor(Color.WHITE);
        tv_booking_2.setTextColor(Color.WHITE);
        book.setClickable(false);
    }

    private void afterPostData() {
        tv_booking_1.setTextColor(Color.parseColor("#00CCCC"));
        tv_booking_2.setTextColor(Color.parseColor("#00CCCC"));
        book.setClickable(true);
    }

    /**
     * Call GetAddressActivity to get address
     *
     * @param type
     */
    public void pickLocation(int type) {
        Intent myIntent = new Intent(MapsActivity.this, GetAddressActivity.class);
        Bundle bundle = new Bundle();
        if (pickUpAddress != null) {
            String pickUpJson = new JSONSerializer().exclude("*.class").serialize(
                    pickUpAddress);
            bundle.putString("pickUpAddress", pickUpJson);
        } else {
            bundle.putString("pickUpAddress", null);
        }
//        String homeAddressJson = new JSONSerializer().exclude("*.class").serialize(
//                getLocationByGeoCode(yourLocation));
        //bundle.putString("homeAddress", homeAddressJson);
        myIntent.putExtra("data", bundle);
        startActivityForResult(myIntent, type);
    }

    /**
     * Handle data return from GetAddressActivity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data.hasExtra("pickUp")) {
                Address address = (Address) data.getExtras().get("pickUp");
                pickUpAddress = address;
                pickUp.setText(address.getFulladdress());
                try {
                    pickUpLocation = new LatLng(address.getLatitude(), address.getLongitude());
                    findNearestDriver(pickUpLocation);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLocation, 12.0f));

                }catch (Exception e) {
                    Log.e("Error", "No gps connection");
                }
                //    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLocation, 12.0f));
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data.hasExtra("dropOff")) {
                Address address = (Address) data.getExtras().get("dropOff");
                dropOffAddress = address;
                dropOff.setText(address.getFulladdress());
            }
        }
        if (!pickUp.getText().toString().isEmpty() && !dropOff.getText().toString().isEmpty()) {
            //postQuotation();
            postQuotation();
        }
    }

    /**
     * Call BookingActivity to make more detail for booking function
     *
     * @param v
     */
    public void booking(View v) {
        if (pickUpAddress != null && dropOffAddress != null) {
            if (!book.isClickable()) {
                Toast.makeText(MapsActivity.this, "This app does not operate in that area", Toast.LENGTH_LONG).show();
            } else {
                Intent myIntent = new Intent(MapsActivity.this, BookingActivity.class);
                Bundle bundle = new Bundle();
                String pickUpJson = new JSONSerializer().exclude("*.class").serialize(
                        pickUpAddress);
                String dropOffJson = new JSONSerializer().exclude("*.class").serialize(
                        dropOffAddress);
                String retrieveQuoteJson = new JSONSerializer().exclude("*.class").serialize(retrieveQuote);

                Log.e("retrieveQuoteJson", retrieveQuoteJson);
                bundle.putString("pickUpAddress", pickUpJson);
                bundle.putString("dropOffAddress", dropOffJson);
                bundle.putInt("people", Integer.parseInt(people.getText().toString()));
                bundle.putInt("luggage", Integer.parseInt(luggage.getText().toString()));
                bundle.putString("retrieveQuote", retrieveQuoteJson);
                bundle.putDouble("totalFare", totalFare);
                myIntent.putExtra("data", bundle);
                startActivity(myIntent);
            }
        }else {
            Toast.makeText(MapsActivity.this, "Please fill the information",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Hide all ImageView from  carlayout
     *
     * @param imageView1
     * @param imageView2
     * @param imageView3
     * @param imageView4
     */
    private void hideImage(ImageView imageView1, ImageView imageView2, ImageView imageView3, ImageView imageView4) {
        imageView1.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);
        imageView4.setVisibility(View.INVISIBLE);
    }

    /**
     * show dialog for picking car
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
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Handle all functions involve to map
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        GPSTracker mGPS = new GPSTracker(this);
        try {
            if (mGPS.isCanGetLocation()) {
                mGPS.getLocation();
            }
        } catch (Exception e) {
            Log.e("Exception", e.getLocalizedMessage(), e);
        }
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        double latitude, longitude;


        latitude = mGPS.getLatitude();
        longitude = mGPS.getLongitude();

        if (latitude != 0 && longitude != 0) {
            // Add a marker in Sydney and move the camera
            yourLocation = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());
            //  mMap.add
            //  mMap.addMarker(new MarkerOptions().position(yourLocation).title("You're here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLocation, 12.0f));
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                public void onCameraChange(CameraPosition arg0) {

                    currentLocation = arg0.target;
                    beforePostData();
                    if (lastLocation != null && calculationByDistance(currentLocation, lastLocation) > 20) {
                        version++;
                        StopHandler.createInstance().setVersion(version);
                        findNearestDriver(currentLocation);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    lastLocation = arg0.target;
                    pickUpAddress = getLocationByGeoCode(currentLocation);
                    pickUp.setText(pickUpAddress.getFulladdress());


                }
            });

            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLocation, 12.0f));
                    return true;
                }
            });
            findNearestDriver(yourLocation);
        } else {
            Toast.makeText(this, "Can't find your location! Please check your GPS!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * get real Address from google map by using geocode
     *
     * @param location
     * @return
     */
    private Address getLocationByGeoCode(LatLng location) {
        Geocoder geocoder;
        List<android.location.Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        Address yourAddressPick = new Address();
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (!addresses.isEmpty()) {
                android.location.Address firstAddress = addresses.get(0);
                yourAddressPick.setPostcode(firstAddress.getPostalCode());
                if(firstAddress.getPostalCode() != null) {
                    String[] outCodes = firstAddress.getPostalCode().split(" ");
                    if (outCodes.length > 2) {
                        if (outCodes[1] != null || outCodes[1].isEmpty()) {
                            yourAddressPick.setOutcode(outCodes[1]);
                        }
                    }
                }
                String fullAddress = "";
                for (int i = 0; i < firstAddress.getMaxAddressLineIndex(); i++) {
                    fullAddress += firstAddress.getAddressLine(i) + ",";
                }

                yourAddressPick.setFulladdress(fullAddress);
                if (firstAddress.hasLongitude()) {
                    yourAddressPick.setLongitude(firstAddress.getLongitude());
                }
                if (firstAddress.hasLatitude()) {
                    yourAddressPick.setLatitude(firstAddress.getLatitude());
                }
            }
        } catch (IOException e) {
            Log.e("Error", e.getLocalizedMessage());
        }

        return yourAddressPick;
    }

    /**
     * Calculation distance between two locations
     *
     * @param StartP
     * @param EndP
     * @return
     */
    public double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        double meter = valueResult % 1000;
        Log.i("Meter", km * 1000 + meter + "");

        return km * 1000 + meter;
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    /**
     * POST /NearestDriver ->Find nearest driver
     *
     * @param location
     */
    private void findNearestDriver(LatLng location) {
        String url = WebServiceTaskManager.URL + "NearestDriver";

        SingleServiceTaskManager wst = new SingleServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "") {

            @Override
            public void handleResponse(String response) {
                if(response != null) {
                    Log.e("NearestDriver_response", response, null);
                    try {
                        yourNearestDriver = new JSONDeserializer<NearestDriver>().use(null,
                                NearestDriver.class).deserialize(response);
                        //postQuotation();
                        postQuotationNewWay();
                        numMinuteDisplayOnMarker.setText((int) (yourNearestDriver.getTravelTime() / 1) + " mins");
                        LatLng taxiNearest = new LatLng(yourNearestDriver.getCurrentPosition().getLat(), yourNearestDriver.getCurrentPosition().getLgn());
                        MarkerOptions dragMark = new MarkerOptions().position(taxiNearest)
                                .title("")
                                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, taxiMarker)));
                        mMap.addMarker(dragMark).showInfoWindow();
                        progressBar.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        Log.e("Error Parse Json", e.getLocalizedMessage());
                    }
                }
            }
        };

        Location locate = new Location(location.latitude, location.longitude);
        String json = new JSONSerializer().exclude("*.class").serialize(
                locate);
        Log.e("NearestDriver", json, null);
        wst.addNameValuePair("", json);

        wst.execute(new String[]{url});
    }

    private void findNearestDriverNew(LatLng location) {
        String url = WebServiceTaskManager.URL + "NearestDriver";

        WebServiceThread webServiceThread = new WebServiceThread();
        webServiceThread.setTaskType(WebServiceMethod.POST);
        ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("prefix", "se137jj"));
        webServiceThread.setParams(postParams);
        webServiceThread.setUrl(url);
        pool.submit(webServiceThread);
        threadPoolExecutor.shutdown();
        try {
            String result = pool.take().get();
            if(result != null) {
                Log.e("NearestDriver_response", result, null);
                try {
                    yourNearestDriver = new JSONDeserializer<NearestDriver>().use(null,
                            NearestDriver.class).deserialize(result);
                    //postQuotation();
                    postQuotationNewWay();
                    numMinuteDisplayOnMarker.setText((int) (yourNearestDriver.getTravelTime() / 1) + " mins");
                    LatLng taxiNearest = new LatLng(yourNearestDriver.getCurrentPosition().getLat(), yourNearestDriver.getCurrentPosition().getLgn());
                    MarkerOptions dragMark = new MarkerOptions().position(taxiNearest)
                            .title("")
                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsActivity.this, taxiMarker)));
                    mMap.addMarker(dragMark).showInfoWindow();
                    progressBar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    Log.e("Error Parse Json", e.getLocalizedMessage());
                }
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * POST /NearestDriver ->Find nearest driver
     *
     * @param location
     */
    private void findNearestDriverWithoutPostQuotation(LatLng location) {
        String url = WebServiceTaskManager.URL + "NearestDriver";

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "") {

            @Override
            public void handleResponse(String response) {
                Log.e("NearestDriver_response", response, null);
                try {
                    yourNearestDriver = new JSONDeserializer<NearestDriver>().use(null,
                            NearestDriver.class).deserialize(response);
                    //  postQuotation();
                    numMinuteDisplayOnMarker.setText((int) (yourNearestDriver.getTravelTime() / 1) + " mins");
                } catch (Exception e) {
                    Log.e("Error Parse Json", e.getLocalizedMessage());
                }
            }
        };

        Location locate = new Location(location.latitude, location.longitude);
        String json = new JSONSerializer().exclude("*.class").serialize(
                locate);
        Log.e("NearestDriver", json, null);
        wst.addNameValuePair("", json);

        wst.execute(new String[]{url});
    }

    /**
     * set visible/invisible some items when login/logout was called
     */
    public void setVisibleItem() {

        if (userDb.getCurrentUser() == null) {
            navigationView.getMenu().findItem(R.id.nav_booking_history).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_currentbooking).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
        } else {
           // userEmail.setText(user.getEmail());
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_booking_history).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_currentbooking).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        setTitle("ZETA-X");

        int id = item.getItemId();


        if (id == R.id.nav_newbooking) {
            Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_booking_history) {
            Intent intent = new Intent(MapsActivity.this, BookingHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_currentbooking) {
            Intent intent = new Intent(MapsActivity.this, CurrentBookingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_login) {
            openDialogSignIn(this);

        } else if (id == R.id.nav_logout) {
            openDialogLogoutConfirm(this);

        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Show dialog for login function
     *
     * @param context
     */
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
                openDialogSignUp(MapsActivity.this);
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
                String android_id = Settings.Secure.getString(MapsActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                user.setDeviceID(android_id);
                login();

                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void openDialogLogoutConfirm(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.logout_confirm);
        ImageView closeButton = (ImageView) dialog.findViewById(R.id.imageView_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        Button yesBtn = (Button) dialog.findViewById(R.id.logout_button);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDb.clearDataUserDb();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    /**
     * POST /SignIn -> login
     */
    private void login() {

        String url = WebServiceTaskManager.URL + "SignIn";

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "") {

            @Override
            public void handleResponse(String response) {
                Log.e("response_login", response, null);
                try {
                    JSONObject root = new JSONObject(response);
                    int code = root.getInt("code");
                    String message = root.getString("message");
                    if(code == 1) {
                        String cusId = root.getString("CustID");
                        if (cusId != null) {
                            Log.e("CusId", cusId, null);
                            user.setCusID(cusId);
                            userDb.login(user);
                        }
                    }else {
                        Toast.makeText(MapsActivity.this, message, Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    Log.e("Exception Sign Up", e.getLocalizedMessage());
                }
                navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            }
        };

        String json = new JSONSerializer().exclude("name", "*.class").serialize(
                user);
        Log.e("json", json, null);
        wst.addNameValuePair("", json);

        wst.execute(new String[]{url});
    }

    /**
     * Show dialog for sign up
     *
     * @param context
     */
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
                openDialogSignIn(MapsActivity.this);
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
                String android_id = Settings.Secure.getString(MapsActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                user.setDeviceID(android_id);
                signUp(user);

                dialog.dismiss();
                openDialogSignIn(MapsActivity.this);
            }
        });

        dialog.show();
    }

    /**
     * POST /SignUp -> sign up funtion
     *
     * @param user
     */
    private void signUp(User user) {

        String url = WebServiceTaskManager.URL + "SignUp";

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.POST_TASK, this, "") {

            @Override
            public void handleResponse(String response) {
                Log.e("response_signup", response, null);
            }
        };

        String json = new JSONSerializer().exclude("cusID", "*.class").serialize(
                user);
        Log.e("json_sign_up", json, null);
        wst.addNameValuePair("", json);

        wst.execute(new String[]{url});

    }
    @Override
    public void onLocationChanged(android.location.Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onGpsStatusChanged(int event) {
        mStatus = mService.getGpsStatus(mStatus);
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLocation, 12.0f));
                break;

            case GpsStatus.GPS_EVENT_STOPPED:

                defaultLocation = new LatLng(51.5034070,-0.1275920);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12.0f));
                break;


        }

    }
}
