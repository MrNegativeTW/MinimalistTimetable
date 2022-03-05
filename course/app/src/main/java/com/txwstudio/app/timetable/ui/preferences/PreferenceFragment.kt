package com.txwstudio.app.timetable.ui.preferences

import android.app.Activity
import android.content.Context
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
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.utilities.DATA_TYPE_IMAGE
import com.txwstudio.app.timetable.utilities.DATA_TYPE_PDF

const val PREFERENCE_TABLE_TITLE = "tableTitle_Pref"
const val PREFERENCE_THEME = "pref_theme"
const val PREFERENCE_FAB_ACTION = "pref_fabAction"
private const val PREFERENCE_MAP_PICKER = "pref_campusMapPicker"
private const val PREFERENCE_CALENDAR_PICKER = "pref_schoolCalendarPicker"
private const val PREFERENCE_MAP_CAL_HELPER = "pref_mapCalHelper"
const val PREFERENCE_WEEKEND_COL = "pref_weekendCol"
const val PREFERENCE_WEEKDAY_LENGTH_LONG = "pref_weekdayLengthLong"
private const val PREFERENCE_BUG_REPORT = "pref_bugReport"

const val PREFERENCE_MAP_DATA_TYPE = "schoolMapDataType"
const val PREFERENCE_MAP_PATH = "schoolMapPath"
const val PREFERENCE_CALENDAR_PATH = "schoolCalendarPath"
private const val PREFERENCE_NAME_EMPTY = "ohThisIsAEmptySlot"
const val PREFERENCE_LAST_TIME_USE = "pref_lastTimeUse"

private const val REQUEST_CODE_MAP_IMAGE = 0
private const val REQUEST_CODE_MAP_PDF = 2
private const val REQUEST_CODE_CALENDAR = 1

private const val BUG_REPORT_LINK = "http://bit.ly/timetableFeedback"

class PreferenceFragment : PreferenceFragmentCompat(),
    Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var prefManager: SharedPreferences

    private val getMapImageContract = registerForActivityResult(MyContract()) { documentUri ->
        if (documentUri == null) {
            Toast.makeText(requireActivity(), R.string.fileReadErrorMsg, Toast.LENGTH_SHORT).show()
        } else {
            //  Persist the permission across restarts.
            requireActivity().contentResolver.takePersistableUriPermission(
                documentUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            // Save the document to [SharedPreferences].
            prefManager.edit().putString(PREFERENCE_MAP_DATA_TYPE, DATA_TYPE_IMAGE).commit()
            prefManager.edit().putString(PREFERENCE_MAP_PATH, documentUri.toString()).commit()
        }
    }

    private val getMapPDFContract = registerForActivityResult(MyContract()) { documentUri ->
        if (documentUri == null) {
            Toast.makeText(requireActivity(), R.string.fileReadErrorMsg, Toast.LENGTH_SHORT).show()
        } else {
            //  Persist the permission across restarts.
            requireActivity().contentResolver.takePersistableUriPermission(
                documentUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            // Save the document to [SharedPreferences].
            prefManager.edit().putString(PREFERENCE_MAP_DATA_TYPE, DATA_TYPE_PDF).commit()
            prefManager.edit().putString(PREFERENCE_MAP_PATH, documentUri.toString()).commit()
        }
    }

    private val getCalendarContract = registerForActivityResult(MyContract()) { documentUri ->
        if (documentUri == null) {
            Toast.makeText(requireActivity(), R.string.fileReadErrorMsg, Toast.LENGTH_SHORT).show()
        } else {
            //  Persist the permission across restarts.
            requireActivity().contentResolver.takePersistableUriPermission(
                documentUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            createShortcut(documentUri)

            // Save the document to [SharedPreferences].
            prefManager.edit().putString(PREFERENCE_CALENDAR_PATH, documentUri.toString()).commit()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        TODO("Not yet implemented, also not called when click, what the hell is this?")
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            PREFERENCE_MAP_PICKER -> {
                val fileCategory = arrayOf("Image", "PDF")
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle("選擇類型")
                    setItems(fileCategory) { _, which ->
                        when (which) {
                            0 -> getMapImageContract.launch(REQUEST_CODE_MAP_IMAGE)
                            1 -> getMapPDFContract.launch(REQUEST_CODE_MAP_PDF)
                        }
                    }
                    show()
                }
            }
            PREFERENCE_CALENDAR_PICKER -> getCalendarContract.launch(REQUEST_CODE_CALENDAR)
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
     *
     * @deprecated Switched to ActivityResultContract. Will be deleted soon.
     */
    private fun handleSelectedFile(requestCode: Int, data: Intent) {
        val prefName = when (requestCode) {
            REQUEST_CODE_MAP_IMAGE -> {
                PREFERENCE_MAP_PATH
            }
            REQUEST_CODE_CALENDAR -> {
                PREFERENCE_CALENDAR_PATH
            }
            else -> PREFERENCE_NAME_EMPTY
        }

//        if (requestCode == REQUEST_CODE_CALENDAR) createShortcut(data)

        try {
            val fileUri = data.data
            val takeFlags = (data.flags
                    and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
            requireActivity().grantUriPermission(requireActivity().packageName, fileUri, takeFlags)
            requireActivity().contentResolver.takePersistableUriPermission(fileUri!!, takeFlags)
            val filePath = fileUri.toString()

            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val editor = prefs.edit()
            editor.putString(prefName, filePath)
            editor.commit()
        } catch (e: Exception) {
            Toast.makeText(activity, R.string.fileReadErrorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Create shortcut if user set calendar path.
     */
    private fun createShortcut(documentUri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = requireContext().getSystemService(ShortcutManager::class.java)
            val shortcut = ShortcutInfo.Builder(context, "calendarShortcut")
                .setShortLabel(getString(R.string.menuCalendar))
                .setLongLabel(getString(R.string.menuCalendar))
                .setIcon(Icon.createWithResource(context, R.mipmap.ic_event_note))
                .setIntent(
                    Intent(Intent.ACTION_VIEW)
                        .setDataAndType(documentUri, DATA_TYPE_PDF)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                )
                .build()
            shortcutManager.dynamicShortcuts = listOf(shortcut)
        }
    }
}

/**
 * For picking up file.
 * */
class MyContract : ActivityResultContract<Int, Uri?>() {

    override fun createIntent(context: Context, input: Int?): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = when (input) {
                REQUEST_CODE_MAP_IMAGE -> DATA_TYPE_IMAGE
                REQUEST_CODE_MAP_PDF, REQUEST_CODE_CALENDAR -> DATA_TYPE_PDF
                else -> DATA_TYPE_IMAGE
            }
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }
        return intent?.data
    }

}