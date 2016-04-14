package com.example.npquy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.npquy.entity.Address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by npquy on 3/22/2016.
 */
public class AddressDb extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database.db";
    public static final String ADDRESS_TABLE_NAME = "Addresses";
    public static final String ADDRESS_COLUMN_ID = "id";
    public static final String ADDRESS_COLUMN_OUT_CODE = "out_code";
    public static final String ADDRESS_COLUMN_POST_CODE = "post_code";
    public static final String ADDRESS_COLUMN_FULL_ADDRESS = "full_address";
    public static final String ADDRESS_COLUMN_CATEGORY = "category";
    public static final String ADDRESS_COLUMN_ICON_PATH = "icon_path";
    public static final String ADDRESS_COLUMN_LATITUDE = "latitude";
    public static final String ADDRESS_COLUMN_LONGITUDE = "longitude";
    public static final String ADDRESS_COLUMN_USER_ID = "user_id";

    private static final Integer FREQUENTLY_NUMBER = 5;

    public AddressDb(Context context) {
        super(context, AddressDb.DATABASE_NAME, null, 1);
    }

    public AddressDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create TABLE IF NOT EXISTS " + AddressDb.ADDRESS_TABLE_NAME + "("
                        + AddressDb.ADDRESS_COLUMN_ID + " integer primary key autoincrement,"
                        + AddressDb.ADDRESS_COLUMN_OUT_CODE + " VARCHAR(255), "
                        + AddressDb.ADDRESS_COLUMN_POST_CODE + " VARCHAR(255), "
                        + AddressDb.ADDRESS_COLUMN_FULL_ADDRESS + " VARCHAR(255), "
                        + AddressDb.ADDRESS_COLUMN_CATEGORY + " VARCHAR(255), "
                        + AddressDb.ADDRESS_COLUMN_ICON_PATH + " VARCHAR(255), "
                        + AddressDb.ADDRESS_COLUMN_LATITUDE + " DOUBLE, "
                        + AddressDb.ADDRESS_COLUMN_LONGITUDE + " DOUBLE,"
                        + AddressDb.ADDRESS_COLUMN_USER_ID + " VARCHAR(255))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AddressDb.ADDRESS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertAddress(Address address) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = getData(address.getFulladdress());
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            deleteAddress(cursor.getInt(0));
        }
        Cursor fullData = getData();
        if (fullData.getCount() >= FREQUENTLY_NUMBER) {
            fullData.moveToFirst();
            deleteAddress(fullData.getInt(0));
        }
        ContentValues values = new ContentValues();
        values.put(AddressDb.ADDRESS_COLUMN_OUT_CODE, address.getOutcode());
        values.put(AddressDb.ADDRESS_COLUMN_POST_CODE, address.getPostcode());
        values.put(AddressDb.ADDRESS_COLUMN_FULL_ADDRESS, address.getFulladdress());
        values.put(AddressDb.ADDRESS_COLUMN_CATEGORY, address.getCategory());
        values.put(AddressDb.ADDRESS_COLUMN_ICON_PATH, address.getIcon_Path());
        values.put(AddressDb.ADDRESS_COLUMN_LATITUDE, address.getLatitude());
        values.put(AddressDb.ADDRESS_COLUMN_LONGITUDE, address.getLongitude());

        if (db.insert(AddressDb.ADDRESS_TABLE_NAME, null, values) == -1) {
            return false;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (fullData != null && !fullData.isClosed()) {
            fullData.close();
        }
        return true;
    }

    public boolean insertHomeAddress(Address address, String cusId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AddressDb.ADDRESS_COLUMN_OUT_CODE, address.getOutcode());
        values.put(AddressDb.ADDRESS_COLUMN_POST_CODE, address.getPostcode());
        values.put(AddressDb.ADDRESS_COLUMN_FULL_ADDRESS, address.getFulladdress());
        values.put(AddressDb.ADDRESS_COLUMN_CATEGORY, address.getCategory());
        values.put(AddressDb.ADDRESS_COLUMN_ICON_PATH, address.getIcon_Path());
        values.put(AddressDb.ADDRESS_COLUMN_LATITUDE, address.getLatitude());
        values.put(AddressDb.ADDRESS_COLUMN_LONGITUDE, address.getLongitude());
        values.put(AddressDb.ADDRESS_COLUMN_USER_ID, cusId);

        if (db.insert(AddressDb.ADDRESS_TABLE_NAME, null, values) == -1) {
            return false;
        }
        Log.e("Insert data","Succeed");
        return true;
    }

    public Cursor getData(String full_address) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + AddressDb.ADDRESS_TABLE_NAME + " where " + AddressDb.ADDRESS_COLUMN_FULL_ADDRESS + " = '" + full_address + "'", null);
        return res;
    }

    public Cursor queryHomeAddress(String cusId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + AddressDb.ADDRESS_TABLE_NAME + " where " + AddressDb.ADDRESS_COLUMN_USER_ID + " = '" + cusId + "'", null);
        return res;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + AddressDb.ADDRESS_TABLE_NAME  + " where " + AddressDb.ADDRESS_COLUMN_USER_ID + " = null", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, AddressDb.ADDRESS_TABLE_NAME);
        return numRows;
    }

    public Integer deleteAddress(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(AddressDb.ADDRESS_TABLE_NAME,
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public List<Address> getAddressFromDb() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Address> addresses = new ArrayList<>();
        Cursor cursor = null;
        if (db != null) {
            try {
                cursor = db.query(ADDRESS_TABLE_NAME, null, null, null, null, null, null);
                cursor.moveToLast();
                if(cursor != null) {
                    while (cursor.isBeforeFirst() == false) {
                        if(cursor.getString(8) == null) {
                            Address address = new Address();
                            address.setOutcode(cursor.getString(1));
                            address.setPostcode(cursor.getString(2));
                            address.setFulladdress(cursor.getString(3));
                            address.setCategory(cursor.getString(4));
                            address.setIcon_Path(cursor.getString(5));
                            address.setLatitude(cursor.getDouble(6));
                            address.setLongitude(cursor.getDouble(7));
                            addresses.add(address);
                        }
                        cursor.moveToPrevious();
                    }
                }
            } catch (Exception ex) {
                Log.e("Error", ex.getLocalizedMessage(), ex);
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return addresses;
    }

    public List<Address> getHomeAddressFromDb(String cusId) {
        List<Address> addresses = new ArrayList<>();
        Cursor cursor = queryHomeAddress(cusId);
        try {
            cursor.moveToLast();
            if(cursor != null) {
                while (cursor.isBeforeFirst() == false) {
                    if(cursor.getString(8) != null) {
                        Address address = new Address();
                        address.setOutcode(cursor.getString(1));
                        address.setPostcode(cursor.getString(2));
                        address.setFulladdress(cursor.getString(3));
                        address.setCategory(cursor.getString(4));
                        address.setIcon_Path(cursor.getString(5));
                        address.setLatitude(cursor.getDouble(6));
                        address.setLongitude(cursor.getDouble(7));
                        addresses.add(address);
                    }
                    cursor.moveToPrevious();
                }
            }
        } catch (Exception ex) {
            Log.e("Error", ex.getLocalizedMessage(), ex);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return addresses;
    }
}
