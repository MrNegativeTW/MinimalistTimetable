package com.txwstudio.app.timetable;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.view.View;
import android.widget.ListView;

public class SettingsFragment extends PreferenceFragment implements
        OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    public static boolean restartSchedule = false;
    private EditTextPreference editTextPreference;

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
        schoolMapPicker.setOnPreferenceClickListener(this);
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
        if (preference.getKey().equals("schoolMapPicker")) {

            int permission = ContextCompat.checkSelfPermission(getContext(),
                                                        Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);
            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, 0);
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (data != null) {
                Uri imageUri = data.getData();
                String imageRealPath = Util.getPath(getContext(), imageUri);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("schoolMapPath", imageRealPath);
                editor.commit();
            }
        } catch (Exception e) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.imageReadErrorTitle);
            dialog.setMessage(R.string.imageReadErrorMsg);
            dialog.show();
        }

    }
}