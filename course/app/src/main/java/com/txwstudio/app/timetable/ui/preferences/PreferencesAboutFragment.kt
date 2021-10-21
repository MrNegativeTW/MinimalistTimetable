package com.txwstudio.app.timetable.ui.preferences

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.mikepenz.aboutlibraries.LibsBuilder
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mikepenz.aboutlibraries.Libs
import com.txwstudio.app.timetable.R

private const val PREFERENCE_ABOUT_VERSION = "prefAbout_version"
private const val PREFERENCE_ABOUT_CHANGELOG = "prefAbout_changelog"
private const val PREFERENCE_ABOUT_OPEN_SOURCE = "prefAbout_openSource"
private const val PREFERENCE_ABOUT_OFFICIAL_SITE = "prefAbout_officialSite"
private const val PREFERENCE_ABOUT_GITHUB = "prefAbout_github"

private const val CHANGELOG_LINK = "https://github.com/MrNegativeTW/MinimalistTimetable/blob/master/CHANGELOG.md"
private const val OPEN_SOURCE_LINK = "prefAbout_openSource"
private const val OFFICIAL_SITE_LINK = "prefAbout_officialSite"
private const val GITHUB_LINK = "https://github.com/MrNegativeTW/MinimalistTimetable"

class PreferencesAboutFragment : PreferenceFragmentCompat(),
        Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_about, rootKey)
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        when (preference?.key) {
            PREFERENCE_ABOUT_CHANGELOG -> customTabsIntent.launchUrl(requireContext(), Uri.parse(CHANGELOG_LINK))
            PREFERENCE_ABOUT_OPEN_SOURCE -> {
                val fragment = LibsBuilder()
                    .withFields(R.string::class.java.fields) // in some cases it may be needed to provide the R class, if it can not be automatically resolved
                    .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "_AboutLibraries") // optionally apply modifications for library information
                    .supportFragment()
                requireActivity().supportFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    replace(R.id.fragment_container_view, fragment)
                    setReorderingAllowed(true)
                    addToBackStack("name")
                }

            }
            PREFERENCE_ABOUT_OFFICIAL_SITE -> customTabsIntent.launchUrl(requireContext(), Uri.parse(OFFICIAL_SITE_LINK))
            PREFERENCE_ABOUT_GITHUB -> customTabsIntent.launchUrl(requireContext(), Uri.parse(GITHUB_LINK))
        }

        return true
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        TODO("Not yet implemented")
    }


}