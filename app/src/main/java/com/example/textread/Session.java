package com.example.textread;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class Session {
    public static void save(Context context,String value, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("myapp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String get(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("myapp", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, String.valueOf(false));

    }


}
