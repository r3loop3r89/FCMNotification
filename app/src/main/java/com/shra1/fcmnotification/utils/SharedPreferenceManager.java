package com.shra1.fcmnotification.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    public static SharedPreferenceManager instance = null;
    Context mCtx;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharedPreferenceManager(Context mCtx) {
        this.mCtx = mCtx;
        sharedPreferences = mCtx.getSharedPreferences(mCtx.getPackageName() +
                        "." +
                        getClass().getName(),
                Context.MODE_PRIVATE);
    }

    public static SharedPreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceManager(context);
        }
        return instance;
    }

    public void clearAll() {
        editor = sharedPreferences.edit();
        editor.clear().commit();
    }


    public String getLAST_SENT_TO() {
        return sharedPreferences.getString("LAST_SENT_TO", "LAST_SENT_TO");
    }

    public void setLAST_SENT_TO(String LAST_SENT_TO) {
        editor = sharedPreferences.edit();
        editor.putString("LAST_SENT_TO", LAST_SENT_TO);
        editor.commit();
    }
}
