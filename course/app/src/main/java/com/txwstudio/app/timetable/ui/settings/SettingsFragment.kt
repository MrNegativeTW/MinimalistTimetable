package com.txwstudio.app.timetable.ui.settings

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.txwstudio.app.timetable.R

private const val PREFERENCE_MAP_CAL_HELPER = "pref_mapCalHelper"
private const val PREFERENCE_CHANGELOG = "pref_changelog"

private const val BUG_REPORT_LINK = "http://bit.ly/timetableFeedback"

class SettingsFragment : PreferenceFragmentCompat(),
        Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        TODO("Not yet implemented, also not called when click, what the hell is this?")
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "pref_mapCalHelper" -> {
                showDialog(PREFERENCE_MAP_CAL_HELPER)
            }
            "pref_bugReport" -> {
                val customTabsIntent = CustomTabsIntent.Builder().build()
                customTabsIntent.launchUrl(requireContext(), Uri.parse(BUG_REPORT_LINK))
            }
            "pref_changelog" -> {
                showDialog(PREFERENCE_CHANGELOG)
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "tableTitle_Pref" -> {

            }
            "lightMode_Pref" -> {

            }
        }
    }

    /**
     * Show information dialog.
     * */
    private fun showDialog(which: String) {
        val materialAlertDialog = MaterialAlertDialogBuilder(requireContext())
        val title = when (which) {
            PREFERENCE_MAP_CAL_HELPER -> R.string.settings_mapCalendarHelperTitle
            PREFERENCE_CHANGELOG -> R.string.settings_changelogTitle
            else -> R.string.all_ohIsError
        }
        val message = when (which) {
            PREFERENCE_MAP_CAL_HELPER -> R.string.settings_mapCalendarHelperMessage
            PREFERENCE_CHANGELOG -> R.string.settings_changelogMessage
            else -> R.string.all_ohIsError
        }
        materialAlertDialog.apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.all_confirm) { _, _ -> }
            show()
        }
    }
}