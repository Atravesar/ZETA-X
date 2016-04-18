package com.example.npquy.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.npquy.activity.GetAddressActivity;
import com.example.npquy.activity.R;
import com.example.npquy.database.AddressDb;
import com.example.npquy.database.UserDb;
import com.example.npquy.entity.Address;
import com.example.npquy.entity.User;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by ITACHI on 3/30/2016.
 */
public class FrequentAdapter extends BaseAdapter {
    private ArrayList<Object> addressArrayList;
    private LayoutInflater inflater;
    private AddressDb addressDb ;
    private UserDb userDb;
    private User user;
    private static final int TYPE_ADDRESS = 0;
    private static final int TYPE_DIVIDER = 1;
    private static final int TYPE_HOMEADDRESS = 2;
    private Context context;

    public FrequentAdapter(Context context, ArrayList<Object> addressArrayList) {
        this.addressArrayList = addressArrayList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return addressArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return addressArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Object target = getItem(position);
        if (position == 1) {
            return TYPE_HOMEADDRESS;
        }
        if (target instanceof Address) {
            return TYPE_ADDRESS;
        }
        return TYPE_DIVIDER;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) == TYPE_ADDRESS);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_ADDRESS:
                    convertView = inflater.inflate(R.layout.row_item, parent, false);
                    break;
                case TYPE_HOMEADDRESS:
                    convertView = inflater.inflate(R.layout.row_item_home, parent, false);
                    break;
                case TYPE_DIVIDER:
                    convertView = inflater.inflate(R.layout.row_header, parent, false);
                    break;
            }
        }
        switch (type) {
            case TYPE_HOMEADDRESS:
                Address homeAddress = (Address) getItem(position);
                TextView homeRoadName = (TextView)convertView.findViewById(R.id.home_road_name);
                TextView homeAddressName = (TextView)convertView.findViewById(R.id.home_address_name);
               /* userDb = new UserDb(context);
                user = new User();
                user = userDb.getCurrentUser();

                addressDb = new AddressDb(context);
                addressDb.getHomeAddressFromDb(user.getCusID());
                if(userDb.getCurrentUser()!=null) {
                    if (homeAddress != null) {
                        String fullAddress = homeAddress.getFulladdress();
                        if (!fullAddress.isEmpty()) {
                            String[] data = fullAddress.split(",");
                            homeRoadName.setText(data[0]);
                            try {
                                homeAddressName.setText(data[2]);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                if (data.length == 1) {
                                    homeAddressName.setText("");
                                } else {
                                    homeAddressName.setText(data[1]);
                                }
                            } catch (Exception e) {
                                homeAddressName.setText("");
                            }
                        } else {
                            homeRoadName.setText("Unknown");
                        }
                    }
                } else {*/
                   // homeAddressName.setText("Tap to select");
                //    homeRoadName.setText("Home address");
                if (homeAddress != null) {
                    String fullAddress = homeAddress.getFulladdress();
                    if (!fullAddress.isEmpty()) {
                        String[] data = fullAddress.split(",");
                        homeRoadName.setText(data[0]);
                        try {
                            homeAddressName.setText(data[2]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            if (data.length == 1) {
                                homeAddressName.setText("");
                            } else {
                                homeAddressName.setText(data[1]);
                            }
                        } catch (Exception e) {
                            homeAddressName.setText("");
                        }
                    } else {
                        homeRoadName.setText("Unknown");
                    }

                } else {
                    homeAddressName.setText("Tap to select");
                    homeRoadName.setText("Home address");
                }
                break;
            case TYPE_ADDRESS:
                Address address = (Address) getItem(position);
                TextView roadName = (TextView) convertView.findViewById(R.id.road_name);
                TextView addressName = (TextView) convertView.findViewById(R.id.address);
                if (address != null) {
                  //  ImageView icon = (ImageView) convertView.findViewById(R.id.imgitem);
                  //  icon.setImageResource(R.drawable.);
                    String fullAddress = address.getFulladdress();
                    if (!fullAddress.isEmpty()) {
                        String[] data = fullAddress.split(",");
                        roadName.setText(data[0]);
                        try {
                            addressName.setText(data[2]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            if (data.length == 1) {
                                addressName.setText("");
                            } else {
                                addressName.setText(data[1]);
                            }
                        } catch (Exception e) {
                            addressName.setText("");
                        }
                    } else {
                        roadName.setText("Unknown");
                    }
                }
                break;
            case TYPE_DIVIDER:
                TextView title = (TextView) convertView.findViewById(R.id.headerTitle);
                String titleString = (String) getItem(position);
                title.setText(titleString);
                break;
        }
        return convertView;
    }
}
