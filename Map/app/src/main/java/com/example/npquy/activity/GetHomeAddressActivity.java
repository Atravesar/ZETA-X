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

public class GetHomeAddressActivity extends AppCompatActivity {

    private ListView lvGetAddress;
    private SelectHomeAddressAdapter selectHomeAddressAdapter;
    private ArrayList<Object> addressesData = new ArrayList<>();

    private AddressDb addressDb;
    private UserDb userDb;
    private String cusId;

    private EditText inputSearch;


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
        addressDb = new AddressDb(this);
        userDb = new UserDb(this);
        User currentUser = userDb.getCurrentUser();
        if (currentUser != null) {
            cusId = currentUser.getCusID();
        }
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        addDataForAddressListView();

        // lv.setAdapter(addressArrayAdapter);
        selectHomeAddressAdapter = new SelectHomeAddressAdapter(this, addressesData);
        lvGetAddress.setAdapter(selectHomeAddressAdapter);
        handleListener();
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
                    address = (Address) selectHomeAddressAdapter.getItem(position);
                } catch (ClassCastException ex) {
                    Log.e("Cast Exception", ex.toString());
                }
                // doInsertRecord(address);
                if (address != null) {
                    if(cusId != null) {
                        addressDb.insertHomeAddress(address, cusId);
                    }
                    Intent homeAddressData = new Intent();
                    homeAddressData.putExtra("HomeAddress", address);
                    setResult(RESULT_OK, homeAddressData);
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
//        Intent callerIntent = getIntent();
//
//        if (callerIntent != null) {
//            Bundle packageFromCaller =
//                    callerIntent.getBundleExtra("data");
//            String pickUpJson = packageFromCaller.getString("pickUpAddress");
//            String homeAddressJson = packageFromCaller.getString("homeAddress");
//            pickUpAddress = new JSONDeserializer<Address>().use(null,
//                    Address.class).deserialize(pickUpJson);
//            homeAddress = new JSONDeserializer<Address>().use(null,
//                    Address.class).deserialize(homeAddressJson);
//        }
        addressesData.add("SELECT HOME ADDRESS");
        if(cusId != null&&addressDb.getHomeAddressFromDb(cusId)!=null) {
            addressesData.addAll(addressDb.getHomeAddressFromDb(cusId));
        }


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


}
