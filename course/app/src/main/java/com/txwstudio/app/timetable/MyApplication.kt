package com.txwstudio.app.timetable

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.txwstudio.app.timetable.ui.settings.PREFERENCE_THEME


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeValue = sharedPreferences?.getString(PREFERENCE_THEME, "-1")?.toInt()
        AppCompatDelegate.setDefaultNightMode(nightModeValue!!)
    }

}