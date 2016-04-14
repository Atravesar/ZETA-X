package com.example.npquy.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.npquy.activity.R;

/**
 * Created by ITACHI on 3/19/2016.
 */
public class ExpandAdapter extends BaseExpandableListAdapter {
    private Activity context;
    private int groupCount;
    private int childCount;
    private static final String[] vehicles =
            {"Bicycle", "Motor", "Car", "Plane", "Ship"};
    private static final String[] devices =
            {"Phone", "Tablet", "Notebook", "Laptop", "PC"};

    public ExpandAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return this.groupCount;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.childCount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView=inflater.inflate(R.layout.previous_bookings, null);
        TextView textView = (TextView) convertView.findViewById(R.id.text_group);
        if(groupPosition == 0){
            textView.setText("HOME");
        }else {
            textView.setText("FREQUENT");
        }
        int bgColor = isExpanded ? Color.GRAY : Color.WHITE;
        convertView.setBackgroundColor(bgColor);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView=inflater.inflate(R.layout.list_item, null);
        final TextView roadName=(TextView)
                convertView.findViewById(R.id.road_name);
        if(groupPosition == 0){
            roadName.setText(vehicles[childPosition]);
        }else{
            roadName.setText(devices[childPosition]);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
