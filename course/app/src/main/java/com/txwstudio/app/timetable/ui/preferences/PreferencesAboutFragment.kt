package com.txwstudio.app.timetable.ui.preferences

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.txwstudio.app.timetable.R

private const val PREFERENCE_ABOUT_VERSION = "prefAbout_version"
private const val PREFERENCE_ABOUT_CHANGELOG = "prefAbout_changelog"
private const val PREFERENCE_ABOUT_OPEN_SOURCE = "prefAbout_openSource"
private const val PREFERENCE_ABOUT_OFFICIAL_SITE = "prefAbout_officialSite"
private const val PREFERENCE_ABOUT_GITHUB = "prefAbout_github"

private const val CHANGELOG_LINK = "prefAbout_changelog"
private const val OPEN_SOURCE_LINK = "prefAbout_openSource"
private const val OFFICIAL_SITE_LINK = "prefAbout_officialSite"
private const val GITHUB_LINK = "prefAbout_officialSite"

class PreferencesAboutFragment : PreferenceFragmentCompat(),
        Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_about)
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        when(preference?.key) {
            PREFERENCE_ABOUT_CHANGELOG -> customTabsIntent.launchUrl(requireContext(), Uri.parse(CHANGELOG_LINK))
            PREFERENCE_ABOUT_OPEN_SOURCE -> customTabsIntent.launchUrl(requireContext(), Uri.parse(OPEN_SOURCE_LINK))
            PREFERENCE_ABOUT_OFFICIAL_SITE -> customTabsIntent.launchUrl(requireContext(), Uri.parse(OFFICIAL_SITE_LINK))
            PREFERENCE_ABOUT_GITHUB -> customTabsIntent.launchUrl(requireContext(), Uri.parse(GITHUB_LINK))
        }

        return true
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        TODO("Not yet implemented")
    }




}