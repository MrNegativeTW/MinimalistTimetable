package com.txwstudio.app.timetable.Ui.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.txwstudio.app.timetable.R;

import java.util.Arrays;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class SettingsFragment extends PreferenceFragment implements
        OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {


    public static boolean restartSchedule = false;
    private EditTextPreference editTextPreference;
    private static final int MAP_REQUEST_CODE = 0;
    private static final int CALENDAR_REQUEST_CODE = 1;
    public static final String MAP_REQUEST_PREF_NAME = "schoolMapPath";
    private static final String CALENDAR_REQUEST_PREF_NAME = "schoolCalendarPath";


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        ListView list = (ListView) rootView.findViewById(android.R.id.list);
        list.setDivider(null);

        editTextPreference = (EditTextPreference) findPreference("tableTitle_Pref");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference schoolMapPicker = findPreference("schoolMapPicker");
        Preference schoolCalendarPicker = findPreference("schoolCalendarPicker");
        Preference mapCalHelper = findPreference("mapCalHelper");
        Preference versionChangelog = findPreference("versionChangelog");
        schoolMapPicker.setOnPreferenceClickListener(this);
        schoolCalendarPicker.setOnPreferenceClickListener(this);
        mapCalHelper.setOnPreferenceClickListener(this);
        versionChangelog.setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("tableTitle_Pref")) {
            editTextPreference.setSummary(sharedPreferences
                    .getString("tableTitle_Pref", String.valueOf(R.string.tableTitleMessage)));
        }
        if (key.equals("lightMode_Pref")) {
            restartSchedule = true;
        }
    }

    /**
     * onPreferenceClick()
     *
     * schoolMapPicker: Send request code and start an intent for result.
     * schoolCalendarPicker: Send request code and start an intent for result.
     * mapCalHelper: Show what to do with above function in preference screen.
     * */
    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        switch (key){
            case "mapCalHelper":
                dialog.setMessage(R.string.mapCalHelperTitleDescription);
                dialog.setPositiveButton(R.string.dialogNormalButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
                dialog.show();
                break;

            case "versionChangelog":
                dialog.setTitle(R.string.versionChangelogTitle)
                      .setMessage(R.string.versionChangelogMessage);
                dialog.setPositiveButton(R.string.dialogNormalButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
                dialog.show();
                break;
        }

        // Remove soon.
//        if (preference.getKey().equals("mapCalHelper")) {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//            dialog.setMessage(R.string.mapCalHelperTitleDescription);
//            dialog.setPositiveButton(R.string.dialogNormalButton, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {}
//            });
//            dialog.show();
//        }
//
//        if (preference.getKey().equals("versionChangelog")) {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//            dialog.setMessage(R.string.mapCalHelperTitleDescription);
//            dialog.setPositiveButton(R.string.dialogNormalButton, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {}
//            });
//            dialog.show();
//        }

        //No needed when using storage access framework, remove soon.
//        int permission = ContextCompat.checkSelfPermission(getContext(),
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission == PackageManager.PERMISSION_DENIED) {
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
//
//        } else 
        if (preference.getKey().equals("schoolMapPicker")) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(intent, MAP_REQUEST_CODE);

        } else if (preference.getKey().equals("schoolCalendarPicker")) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(intent, CALENDAR_REQUEST_CODE);
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            handleSelectedFile(requestCode, data);
        } else if (resultCode == RESULT_CANCELED) {
        } else if (data == null) {
            Toast.makeText(getActivity(), R.string.fileReadErrorMsg, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Decide which preference to write, then save uri into preference.
     * Keyword: Storage Access Framework.
     *
     * @param requestCode: Receive code and decide which preference to write.
     * @param data: File uri
     * */
    private void handleSelectedFile(int requestCode, Intent data) {
        String prefName = "";
        switch (requestCode){
            case MAP_REQUEST_CODE:
                prefName = MAP_REQUEST_PREF_NAME;
                break;
            case CALENDAR_REQUEST_CODE:
                prefName = CALENDAR_REQUEST_PREF_NAME;
                createShortcut(data);
                break;
        }

        try {
            Uri fileUri = data.getData();
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getActivity().grantUriPermission(getActivity().getPackageName(), fileUri, takeFlags);
            getActivity().getContentResolver().takePersistableUriPermission(fileUri, takeFlags);
            String filePath = fileUri.toString();

            /**Old method, remove soon.*/
//            String filePath = Util.getPath(getContext(), fileUri);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(prefName, filePath);
            editor.commit();
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.fileReadErrorMsg, Toast.LENGTH_SHORT).show();
        }
    }


    private void createShortcut(Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Uri fileUri = data.getData();
            ShortcutManager shortcutManager = getContext().getSystemService(ShortcutManager.class);

            ShortcutInfo shortcut = new ShortcutInfo.Builder(getContext(), "calendarShortcut")
                    .setShortLabel(getString(R.string.menuCalendar))
                    .setLongLabel(getString(R.string.menuCalendar))
                    .setIcon(Icon.createWithResource(getContext(), R.mipmap.ic_event_note))
                    .setIntent(new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(fileUri, "application/pdf")
                            .setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))
                    .build();

            shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
        }
    }

}