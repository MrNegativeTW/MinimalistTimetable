package com.example.admin.course;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment{
//  implements TimePickerDialog.OnTimeSetListener
    // https://www.youtube.com/watch?v=QMwaNN_aM3U

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(),
                (TimePickerDialog.OnTimeSetListener) getActivity(),
                hour, min, android.text.format.DateFormat.is24HourFormat(getActivity()));
//        return new TimePickerDialog(getActivity(), this, hour, min,
//                android.text.format.DateFormat.is24HourFormat(getActivity()));
    }


//    @Override
//    public void onTimeSet(TimePicker timePicker, int hourOfDay, int min) {
//        Log.i("Test Message", hourOfDay + "::" + min + "View:" + timePicker);
//    }



}
