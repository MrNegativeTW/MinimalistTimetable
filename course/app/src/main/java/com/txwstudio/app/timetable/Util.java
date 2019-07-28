package com.txwstudio.app.timetable;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

public class Util extends Activity {

    public Util() {}

    public static void setupTheme(Activity activity) {
//        PreferenceManager.setDefaultValues(this,  R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        Boolean lightMode = sharedPref.getBoolean("lightMode_Pref", false);
        if (lightMode) {
            activity.setTheme(R.style.LightTheme);
        }
    }
}
