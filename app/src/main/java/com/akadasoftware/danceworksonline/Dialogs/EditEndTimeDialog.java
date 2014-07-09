package com.akadasoftware.danceworksonline.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

/**
 * Created by Kyle on 7/8/2014.
 */
public class EditEndTimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public EditEndTimeDialog() {
    }


    public interface EditStopTimeDialogListener {
        void onFinishEditStopTimeDialog(TimePicker tpStartTime);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i2) {

        EditStopTimeDialogListener activity = (EditStopTimeDialogListener) getActivity();
        activity.onFinishEditStopTimeDialog(timePicker);
        //this.dismiss();

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        String stopHour = getArguments().getString("Stop").trim();
        if (stopHour.trim().equalsIgnoreCase("")) {
            stopHour = "1:00pm";
        }
        int hour;
        if (stopHour.charAt(1) == ':') {
            String strHour = stopHour.substring(0, 1).trim();
            hour = Integer.parseInt(strHour);
        } else {
            String strHour = stopHour.substring(0, 2).trim();
            hour = Integer.parseInt(strHour);
        }


        String strMinute = stopHour.substring(stopHour.length() - 4, stopHour.length() - 2);
        int minute = Integer.parseInt(strMinute);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

}
