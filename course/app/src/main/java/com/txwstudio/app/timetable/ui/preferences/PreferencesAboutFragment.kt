package com.txwstudio.app.timetable.ui.preferences

import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.txwstudio.app.timetable.BuildConfig
import com.txwstudio.app.timetable.R

private const val PREFERENCE_ABOUT_VERSION = "prefAbout_version"
private const val PREFERENCE_ABOUT_CHANGELOG = "prefAbout_changelog"
private const val PREFERENCE_ABOUT_OPEN_SOURCE = "prefAbout_openSource"
private const val PREFERENCE_ABOUT_TERMS_OF_SERVICE = "prefAbout_termsOfService"
private const val PREFERENCE_ABOUT_PRIVACY_POLICY = "prefAbout_privacyPolicy"
private const val PREFERENCE_ABOUT_LICENSE = "prefAbout_license"

private const val CHANGELOG_LINK = "https://github.com/MrNegativeTW/MinimalistTimetable/releases"
private const val TERMS_OF_SERVICE_LINK = "https://example.com"
private const val PRIVACY_POLICY_LINK = "https://example.com"
private const val LICENSE_LINK = "https://example.com"

class PreferencesAboutFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_about, rootKey)

        findPreference<Preference>(PREFERENCE_ABOUT_VERSION)?.summary = BuildConfig.VERSION_NAME
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        TODO("Not yet implemented")
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        when (preference.key) {
            PREFERENCE_ABOUT_CHANGELOG -> customTabsIntent.launchUrl(
                requireContext(),
                Uri.parse(CHANGELOG_LINK)
            )
            PREFERENCE_ABOUT_OPEN_SOURCE -> {
                val fragment = LibsBuilder()
                    .withFields(R.string::class.java.fields) // in some cases it may be needed to provide the R class, if it can not be automatically resolved
                    .withLibraryModification(
                        "aboutlibraries",
                        Libs.LibraryFields.LIBRARY_NAME,
                        "_AboutLibraries"
                    ) // optionally apply modifications for library information
                    .supportFragment()
                requireActivity().supportFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    replace(R.id.fragment_container_view, fragment)
                    setReorderingAllowed(true)
                    addToBackStack("name")
                }
            }
            PREFERENCE_ABOUT_TERMS_OF_SERVICE -> customTabsIntent.launchUrl(
                requireContext(),
                Uri.parse(TERMS_OF_SERVICE_LINK)
            )
            PREFERENCE_ABOUT_PRIVACY_POLICY -> customTabsIntent.launchUrl(
                requireContext(),
                Uri.parse(PRIVACY_POLICY_LINK)
            )
            PREFERENCE_ABOUT_LICENSE -> customTabsIntent.launchUrl(
                requireContext(),
                Uri.parse(LICENSE_LINK)
            )
        }

        return true
    }

}