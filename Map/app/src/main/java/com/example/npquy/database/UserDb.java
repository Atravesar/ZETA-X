package com.example.npquy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.npquy.entity.User;

/**
 * Created by npquy on 3/22/2016.
 */
public class UserDb extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database_user.db";
    public static final String USER_TABLE_NAME = "Users";
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_CUS_ID = "cus_id";
    public static final String USER_COLUMN_NAME = "name";
    public static final String USER_COLUMN_MOBILE = "mobile";
    public static final String USER_COLUMN_EMAIL = "email";
    public static final String USER_COLUMN_DEVICE_ID = "device_id";

    public UserDb(Context context) {
        super(context, UserDb.DATABASE_NAME, null, 1);
    }

 /*   public UserDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create TABLE IF NOT EXISTS " + UserDb.USER_TABLE_NAME + "("
                        + UserDb.USER_COLUMN_ID + " integer primary key autoincrement,"
                        + UserDb.USER_CUS_ID + " VARCHAR(255),"
                        + UserDb.USER_COLUMN_NAME + " VARCHAR(255), "
                        + UserDb.USER_COLUMN_MOBILE + " VARCHAR(255), "
                        + UserDb.USER_COLUMN_EMAIL + " VARCHAR(255), "
                        + UserDb.USER_COLUMN_DEVICE_ID + " VARCHAR(255) "
                        + ")"
        );

        Log.e("Database", "Create database " + USER_TABLE_NAME + " succeed", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserDb.USER_TABLE_NAME);
        onCreate(db);
    }

    public boolean saveUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
/*        Cursor cursor = getData(user.getEmail());
        if(cursor.getColumnCount() != 0 ){
            cursor.moveToFirst();
            deleteUser(cursor.getInt(0));
        }*/
        ContentValues values = new ContentValues();
        values.put(UserDb.USER_CUS_ID, user.getCusID());
        values.put(UserDb.USER_COLUMN_NAME, user.getName());
        values.put(UserDb.USER_COLUMN_MOBILE, user.getMobile());
        values.put(UserDb.USER_COLUMN_EMAIL, user.getEmail());
        values.put(UserDb.USER_COLUMN_DEVICE_ID, user.getDeviceID());

        if (db.insert(UserDb.USER_TABLE_NAME, null, values) == -1) {
            return false;
        }
        db.notify();
        return true;
    }

    public Cursor getData(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + UserDb.USER_TABLE_NAME + " where " + UserDb.USER_COLUMN_EMAIL + " = '" + email + "'", null);
        return res;
    }

    public Integer deleteUser(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result =  db.delete(UserDb.USER_TABLE_NAME,
                "id = ? ",
                new String[]{Integer.toString(id)});
        return result;
    }

    public void clearDataUserDb() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ UserDb.USER_TABLE_NAME);
    }

    public User getCurrentUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = null;
        if (db != null) {
            try {
                cursor = db.query(UserDb.USER_TABLE_NAME, null, null, null, null, null, null, null);
                if(cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    user = new User();
                    user.setCusID(cursor.getString(1));
                    user.setName(cursor.getString(2));
                    user.setMobile(cursor.getString(3));
                    user.setEmail(cursor.getString(4));
                    user.setDeviceID(cursor.getString(5));
                }
            } catch (Exception ex) {
                Log.e("Error", ex.getLocalizedMessage(), ex);
            }finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return user;
    }

    public User login(User user) {
     //   clearDataUserDb();
        saveUser(user);
        return user;
    }
}
