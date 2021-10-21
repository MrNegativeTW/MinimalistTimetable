package com.txwstudio.app.timetable.ui.preferences

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.txwstudio.app.timetable.R
import java.util.*

const val PREFERENCE_TABLE_TITLE = "tableTitle_Pref"
const val PREFERENCE_THEME = "pref_theme"
private const val PREFERENCE_MAP_PICKER = "pref_campusMapPicker"
private const val PREFERENCE_CALENDAR_PICKER = "pref_schoolCalendarPicker"
private const val PREFERENCE_MAP_CAL_HELPER = "pref_mapCalHelper"
const val PREFERENCE_WEEKEND_COL = "pref_weekendCol"
const val PREFERENCE_WEEKDAY_LENGTH_LONG = "pref_weekdayLengthLong"
private const val PREFERENCE_BUG_REPORT = "pref_bugReport"

const val PREFERENCE_NAME_MAP_REQUEST = "schoolMapPath"
const val PREFERENCE_NAME_CALENDAR_REQUEST = "schoolCalendarPath"
private const val PREFERENCE_NAME_EMPTY = "ohThisIsAEmptySlot"

private const val REQUEST_CODE_MAP = 0
private const val REQUEST_CODE_CALENDAR = 1

private const val BUG_REPORT_LINK = "http://bit.ly/timetableFeedback"

class PreferenceFragment : PreferenceFragmentCompat(),
        Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var prefManager: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager.getDefaultSharedPreferences(requireContext())
//        PreferenceManager.setDefaultValues(requireContext(), R.xml.preferences, false)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    /**
     * Handle file section, invoked by showPicker().
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            handleSelectedFile(requestCode, data)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(requireActivity(), R.string.all_cancel, Toast.LENGTH_SHORT).show()
        } else if (data == null) {
            Toast.makeText(requireActivity(), R.string.fileReadErrorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        TODO("Not yet implemented, also not called when click, what the hell is this?")
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            PREFERENCE_MAP_PICKER -> showPicker(REQUEST_CODE_MAP)
            PREFERENCE_CALENDAR_PICKER -> showPicker(REQUEST_CODE_CALENDAR)
            PREFERENCE_MAP_CAL_HELPER -> showDialog(PREFERENCE_MAP_CAL_HELPER)
            PREFERENCE_BUG_REPORT -> {
                val customTabsIntent = CustomTabsIntent.Builder().build()
                customTabsIntent.launchUrl(requireContext(), Uri.parse(BUG_REPORT_LINK))
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PREFERENCE_THEME -> {
                val value = sharedPreferences?.getString(PREFERENCE_THEME, "-1")?.toInt()
                AppCompatDelegate.setDefaultNightMode(value!!)
            }
        }
    }

    /**
     * Start an activity for picking file
     * */
    private fun showPicker(requestCode: Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = when (requestCode) {
            REQUEST_CODE_MAP -> "image/*"
            REQUEST_CODE_CALENDAR -> "application/pdf"
            else -> "image/*"
        }
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(intent, requestCode)
    }

    /**
     * Show information dialog.
     * */
    private fun showDialog(which: String) {
        val materialAlertDialog = MaterialAlertDialogBuilder(requireContext())
        val title = when (which) {
            PREFERENCE_MAP_CAL_HELPER -> R.string.settings_mapCalendarHelperTitle
            else -> R.string.all_ohIsError
        }
        val message = when (which) {
            PREFERENCE_MAP_CAL_HELPER -> R.string.settings_mapCalendarHelperMessage
            else -> R.string.all_ohIsError
        }
        materialAlertDialog.apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.all_confirm) { _, _ -> }
            show()
        }
    }

    /**
     * Decide which preference to write, then save uri into preference.
     * Keyword: Storage Access Framework.
     *
     * @param requestCode Receive code and decide which preference to write.
     * @param data File uri
     */
    private fun handleSelectedFile(requestCode: Int, data: Intent) {
        val prefName = when (requestCode) {
            REQUEST_CODE_MAP -> {
                PREFERENCE_NAME_MAP_REQUEST
            }
            REQUEST_CODE_CALENDAR -> {
                PREFERENCE_NAME_CALENDAR_REQUEST
            }
            else -> PREFERENCE_NAME_EMPTY
        }

        if (requestCode == REQUEST_CODE_CALENDAR) createShortcut(data)

        try {
            val fileUri = data.data
            val takeFlags = (data.flags
                    and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
            requireActivity().grantUriPermission(requireActivity().packageName, fileUri, takeFlags)
            requireActivity().contentResolver.takePersistableUriPermission(fileUri!!, takeFlags)
            val filePath = fileUri.toString()

            /**Old method, remove soon. */
//            String filePath = Util.getPath(getContext(), fileUri);
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val editor = prefs.edit()
            editor.putString(prefName, filePath)
            editor.commit()
        } catch (e: Exception) {
            Toast.makeText(activity, R.string.fileReadErrorMsg, Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Create shortcut it user set calendar path.
     * */
    private fun createShortcut(data: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val fileUri = data.data
            val shortcutManager = requireContext().getSystemService(ShortcutManager::class.java)
            val shortcut = ShortcutInfo.Builder(context, "calendarShortcut")
                    .setShortLabel(getString(R.string.menuCalendar))
                    .setLongLabel(getString(R.string.menuCalendar))
                    .setIcon(Icon.createWithResource(context, R.mipmap.ic_event_note))
                    .setIntent(Intent(Intent.ACTION_VIEW)
                            .setDataAndType(fileUri, "application/pdf")
                            .setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))
                    .build()
            shortcutManager.dynamicShortcuts = Arrays.asList(shortcut)
        }
    }
}