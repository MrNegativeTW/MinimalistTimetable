package com.txwstudio.app.timetable;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.documentfile.provider.DocumentFile;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class SettingsFragment extends PreferenceFragment implements
        OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {


    public static boolean restartSchedule = false;
    private EditTextPreference editTextPreference;
    private static final int MAP_REQUEST_CODE = 0;
    private static final int CALENDAR_REQUEST_CODE = 1;


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
        schoolMapPicker.setOnPreferenceClickListener(this);
        schoolCalendarPicker.setOnPreferenceClickListener(this);
        mapCalHelper.setOnPreferenceClickListener(this);
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

    @Override
    public boolean onPreferenceClick(Preference preference) {
        int permission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},0);

        } else if (preference.getKey().equals("schoolMapPicker")) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(intent, MAP_REQUEST_CODE);

        } else if (preference.getKey().equals("schoolCalendarPicker")) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(intent, CALENDAR_REQUEST_CODE);

        } else if (preference.getKey().equals("mapCalHelper")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("PlaceHolder");
            dialog.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getActivity().finish();
                }
            });
            dialog.show();
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null){
            fileRequest(requestCode, data);
        } else if (resultCode == RESULT_CANCELED){
        } else if (data == null) {
            Toast.makeText(getActivity(), "oops", Toast.LENGTH_SHORT).show();
        }
    }


    private void fileRequest(int requestCode, Intent data) {

        String prefName = "";
        switch (requestCode){
            case MAP_REQUEST_CODE:
                prefName = "schoolMapPath";
                break;
            case CALENDAR_REQUEST_CODE:
                prefName = "schoolCalendarPath";
                break;
        }

        try {
            Uri fileUri = data.getData();
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getActivity().grantUriPermission(getActivity().getPackageName(), fileUri, takeFlags);
            getActivity().getContentResolver().takePersistableUriPermission(fileUri, takeFlags);
//            String filePath = Util.getPath(getContext(), fileUri);
            String filePath = fileUri.toString(); //Testing

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(prefName, filePath);
            editor.commit();
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.fileReadErrorMsg, Toast.LENGTH_SHORT).show();
        }
    }

}