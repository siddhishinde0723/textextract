package com.example.textread;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public String getName() {
        name=prefs.getString("username","");
        return name;
    }

    public void setName(String name) {
        this.name = name;
        prefs.edit().putString("username",name).commit();
    }

    private String name;

   public Session(Context ctx){

        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("myapp", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedin(boolean logggedin){
        editor.putBoolean("loggedInmode",logggedin);
        editor.commit();
    }

    public boolean loggedin(){
        return prefs.getBoolean("loggedInmode", false);
    }
}