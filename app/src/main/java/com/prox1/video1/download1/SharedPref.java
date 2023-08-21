package com.prox1.video1.download1;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    public static Context context;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences sharedPreferences;

    public SharedPref(Context context2) {
        this.context = context2;
        SharedPreferences sharedPreferences2 = context2.getSharedPreferences("setting", 0);
        sharedPreferences = sharedPreferences2;
        editor = sharedPreferences2.edit();
    }

    public static void SET_PURCHASED(Boolean bool) {
        editor.putBoolean("purchased", bool.booleanValue());
        editor.apply();
    }

    public static Boolean GET_PURCHASED() {
        return Boolean.valueOf(sharedPreferences.getBoolean("purchased", false));
    }
}