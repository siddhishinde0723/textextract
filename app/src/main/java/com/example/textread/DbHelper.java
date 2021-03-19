package com.example.textread;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "myapp.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "Registration";
   public static final String Name = "name";
    public static final String Email = "email";
    public static final String Password = "password";

    public static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_NAME + "("
           // + Name + " TEXT PRIMARY KEY AUTOINCREMENT,"
            + Email + " TEXT,"
            + Password + " TEXT);";
    private static final String TAG = "login";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }


 public void addUser(String name, String email, String password) {
 SQLiteDatabase db = this.getWritableDatabase();

 ContentValues values = new ContentValues();
 values.put(Name,name);
 values.put(Email, email);
 values.put(Password, password);

 long id = db.insert(TABLE_NAME, null, values);
 db.close();

 Log.d(TAG, "user inserted" + id);
 }

    public boolean getUser(String email, String pass){
 //HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "select * from  " + TABLE_NAME+ " where " +
            Email + " = " + "'"+email+"'" + " and " + Password + " = " + "'"+pass+"'";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
 // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {

        return true;
            }
 cursor.close();
 db.close();

 return false;
 }
 }