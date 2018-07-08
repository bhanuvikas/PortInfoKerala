package com.example.bhanu.portinfokerala;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setPhoneNo(String phoneNo) {
        prefs.edit().putString("phone_no", phoneNo).apply();
    }

    public String getPhoneNo() {
        return  prefs.getString("phone_no", "");
    }


}
