package com.example.npquy.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.npquy.adapter.AddressAdapter;
import com.example.npquy.adapter.FrequentAdapter;
import com.example.npquy.adapter.SelectHomeAddressAdapter;
import com.example.npquy.database.AddressDb;
import com.example.npquy.database.UserDb;
import com.example.npquy.entity.Address;
import com.example.npquy.entity.User;
import com.example.npquy.service.WebServiceTaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import flexjson.JSONDeserializer;

public class GetAddressActivity extends AppCompatActivity {

    private ListView lvGetAddress;
    private FrequentAdapter frequentAdapter;
    private SelectHomeAddressAdapter selectHomeAddressAdapter;
    private ArrayList<Object> addressesData = new ArrayList<>();

    private AddressDb addressDb;
    private UserDb userDb;
    private TextView homeRoadName, homeAddressName;
    private EditText inputSearch;

    private List<Address> nearlyAddress = new ArrayList<>();
    private Address pickUpAddress, homeAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_address);
        //getSupportActionBar().setHomeButtonEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);


        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.search_layout, null);

        actionBar.setCustomView(v);
        View view = actionBar.getCustomView();
        ViewGroup.LayoutParams  lp = view.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(lp);

        lvGetAddress = (ListView) findViewById(R.id.frequent_view);


        // getDatabase();
        addressDb = new AddressDb(GetAddressActivity.this);
        userDb = new UserDb(GetAddressActivity.this);

        inputSearch = (EditText) findViewById(R.id.inputSearch);
        addDataForAddressListView();

        // lv.setAdapter(addressArrayAdapter);

        selectHomeAddressAdapter = new SelectHomeAddressAdapter(this, addressesData);


        frequentAdapter = new FrequentAdapter(this, addressesData);
        lvGetAddress.setAdapter(frequentAdapter);
        handleListener();
    }

    public void selectHome(View v) {

        //Toast.makeText(this,"Select Home Address",Toast.LENGTH_LONG).show();
        if (homeAddress == null) {
            Intent myIntent = new Intent(GetAddressActivity.this, GetHomeAddressActivity.class);

            startActivityForResult(myIntent, 1);

        }
        else {

            Intent data = new Intent();
            data.putExtra("pickUp", homeAddress);
            data.putExtra("dropOff", homeAddress);
            data.putExtra("viaAdd", homeAddress);
            setResult(RESULT_OK, data);
            addressDb.close();
            finish();
        }
    }

    public void editHomeAddress(View v) {
        //Toast.makeText(this,"Edit Home Address",Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(GetAddressActivity.this, GetHomeAddressActivity.class);


        startActivityForResult(myIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data.hasExtra("HomeAddress")) {
                Address address = (Address) data.getExtras().get("HomeAddress");
                homeAddress = address;

                Log.e("homeAddree", homeAddress.toString());

                /*if (homeAddress != null) {
                    String fullAddress = homeAddress.getFulladdress();
                    if (!fullAddress.isEmpty()) {
                        String[] fullAddressSplit = fullAddress.split(",");
                        homeRoadName.setText(fullAddressSplit[0]);
                        try {
                            homeAddressName.setText(fullAddressSplit[2]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            if (fullAddressSplit.length == 1) {
                                homeAddressName.setText("");
                            } else {
                                homeAddressName.setText(fullAddressSplit[1]);
                            }
                        } catch (Exception e) {
                            homeAddressName.setText("");
                        }
                    } else {
                        homeRoadName.setText("Unknown");
                    }
                }else {
                    homeAddressName.setText("Tap to select");
                    homeRoadName.setText("Home address");
                }*/

                addressesData.remove(homeAddress);
                addressesData.set(1,address);

            }
            frequentAdapter.notifyDataSetChanged();
            lvGetAddress.setAdapter(frequentAdapter);
        }

    }

    /**
     * Handle change listener for some items
     */
    private void handleListener() {
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                findSearchAddress(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        lvGetAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position,
                                    long id) {
                Address address = null;

                try {
                    address = (Address) frequentAdapter.getItem(position);
                    address = (Address) selectHomeAddressAdapter.getItem(position);
                } catch (ClassCastException ex) {
                    Log.e("Cast Exception", ex.toString());
                }

                // doInsertRecord(address);
                if (address != null) {
                    addressDb.insertAddress(address);
                    Intent data = new Intent();
                    data.putExtra("pickUp", address);
                    data.putExtra("dropOff", address);
                    data.putExtra("viaAdd", address);
                    setResult(RESULT_OK, data);
                    addressDb.close();
                    finish();
                }

            }
        });


    }



    /**
     * add data for address adapter
     */

    private void addDataForAddressListView() {
        Intent callerIntent = getIntent();

        if (callerIntent != null) {
            Bundle packageFromCaller =
                    callerIntent.getBundleExtra("data");
            String pickUpJson = packageFromCaller.getString("pickUpAddress");
            try {
                pickUpAddress = new JSONDeserializer<Address>().use(null,
                        Address.class).deserialize(pickUpJson);
            } catch (Exception e) {
                Log.e("Error", "No pick up address!");
            }
           /* homeAddress = new JSONDeserializer<Address>().use(null,
                    Address.class).deserialize(homeAddressJson);*/
        }
        User currentUser = userDb.getCurrentUser();

        if (currentUser != null) {

            List<Address> addresses = addressDb.getHomeAddressFromDb(currentUser.getCusID());
            if (!addresses.isEmpty()) {
                homeAddress = addresses.get(0);
            }
        }
        addressesData.add("HOME");
//        Log.e("Home address", homeAddress.toString());
        if (homeAddress != null) {
            addressesData.add(homeAddress);
        } else {
            addressesData.add(null);
        }
        addressesData.add("FREQUENT");
        addressesData.addAll(addressDb.getAddressFromDb());

        if (pickUpAddress != null) {
            String postCode = pickUpAddress.getPostcode();
            getNearlyAddress(postCode);
        }
        addressesData.add("NEAREST");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * GET /SearchAddress -> find Address by using key word
     *
     * @param text
     */
    private void findSearchAddress(String text) {
        String url = WebServiceTaskManager.URL + "SearchAddress";

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.GET_TASK, this, "") {

            @Override
            public void handleResponse(String response) {
                Log.e("response", response, null);
      /*         */
                try {
                    JSONObject root = new JSONObject(response);
                    JSONArray addressArray = root.getJSONArray("addresses");
                    String message = root.getString("message");
                    String code = root.getString("code");
                    List<Address> addresses = new JSONDeserializer<List<Address>>()
                            .use(null, ArrayList.class).use("values", Address.class).deserialize(addressArray.toString());
                    Log.e("message", message.toString(), null);
                    Log.e("code", code.toString(), null);
                    addressesData.clear();
                    addressesData.addAll(addresses);
                    selectHomeAddressAdapter.notifyDataSetChanged();
                    lvGetAddress.setAdapter(selectHomeAddressAdapter);
                } catch (JSONException e) {
                    Log.e("Error", e.getLocalizedMessage(), e);
                }

            }
        };

        wst.addNameValuePair("prefix", text);

        wst.execute(new String[]{url});
    }

    /**
     * GET /NearByPlaces -> get Address around pickup address from mapActivity
     *
     * @param postCode
     */
    private void getNearlyAddress(String postCode) {
        String url = WebServiceTaskManager.URL + "NearbyPlaces";

        WebServiceTaskManager wst = new WebServiceTaskManager(WebServiceTaskManager.GET_TASK, this, "") {

            @Override
            public void handleResponse(String response) {
                Log.e("response_nearly", response, null);
      /*         */
                try {
                    JSONObject root = new JSONObject(response);
                    JSONArray addressArray = root.getJSONArray("addresses");
                    String message = root.getString("message");
                    String code = root.getString("code");
                    List<Address> addresses = new JSONDeserializer<List<Address>>()
                            .use(null, ArrayList.class).use("values", Address.class).deserialize(addressArray.toString());
                    Log.e("message", message.toString(), null);
                    Log.e("code", code.toString(), null);
                    if (addresses.size() <= 5) {
                        nearlyAddress.addAll(addresses);
                    } else {
                        for (int i = 0; i < 5; i++) {
                            nearlyAddress.add(addresses.get(i));
                        }
                    }
                    addressesData.addAll(nearlyAddress);
                    frequentAdapter.notifyDataSetChanged();
                    lvGetAddress.setAdapter(frequentAdapter);
                } catch (JSONException e) {
                    Log.e("Error", e.getLocalizedMessage(), e);
                }

            }
        };

        wst.addNameValuePair("prefix", postCode);
        wst.execute(new String[]{url});
    }




}

