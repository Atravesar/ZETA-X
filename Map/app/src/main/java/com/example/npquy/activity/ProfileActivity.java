package com.example.npquy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.npquy.database.AddressDb;
import com.example.npquy.database.UserDb;
import com.example.npquy.entity.Address;
import com.example.npquy.entity.User;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView fullNameTextView, mobileTextView, emailTextView, homeTextView;
    private Switch enableSnoozeSwitch;

    private UserDb userDb;
    private AddressDb addressDb;

    private LinearLayout homeLayOut;

    private String cusId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        config();
        userDb = new UserDb(this);

        User currentUser = userDb.getCurrentUser();
        if (currentUser != null) {
            cusId = currentUser.getCusID();
        }
        addressDb = new AddressDb(this);
        setData();
        setListener();
    }

    /**
     * Config
     */
    private void config() {
        fullNameTextView = (TextView) findViewById(R.id.full_name_text_view);
        mobileTextView = (TextView) findViewById(R.id.mobile_text_view);
        emailTextView = (TextView) findViewById(R.id.email_profile_text_view);
        homeTextView = (TextView) findViewById(R.id.home_profile_text_view);
        homeLayOut = (LinearLayout) findViewById(R.id.home_lay_out);

        enableSnoozeSwitch = (Switch) findViewById(R.id.enable_snooze_switch);
    }

    private void setListener() {
        homeLayOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ProfileActivity.this, GetHomeAddressActivity.class);
                startActivityForResult(myIntent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data.hasExtra("HomeAddress")) {
                Address address = (Address) data.getExtras().get("HomeAddress");
                if (cusId != null) {
                    addressDb.insertHomeAddress(address, cusId);
                    homeTextView.setText(address.getFulladdress());
                }
            }
        }
    }

    /**
     *
     */
    private void setData() {
        User currentUser = userDb.getCurrentUser();
        if (currentUser != null) {
            Log.e("user", currentUser.toString());
//            fullNameTextView.setText(currentUser.getName());
            mobileTextView.setText(currentUser.getMobile());
            emailTextView.setText(currentUser.getEmail());
            List<Address> addressList = addressDb.getHomeAddressFromDb(currentUser.getCusID());
            if (addressList.size() != 0) {
                Address homeAddress = addressList.get(0);
                homeTextView.setText(homeAddress.getFulladdress());
            } else {
                homeTextView.setText("Tap to select home address");

            }
        }
    }
}
