package com.akadasoftware.danceworksonline.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;


/**
 * Created by Kyle on 7/7/2014.
 */
public class EditStartTimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public EditStartTimeDialog() {
    }


    public interface EditTimeDialogListener {
        void onFinishEditTimeDialog(TimePicker tpStartTime);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i2) {

        EditTimeDialogListener activity = (EditTimeDialogListener) getActivity();
        activity.onFinishEditTimeDialog(timePicker);
        //this.dismiss();

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        String startHour = getArguments().getString("Start").trim();

        if (startHour.trim().equalsIgnoreCase("")) {
            startHour = "1:00pm";
        }
        int hour;
        if (startHour.charAt(1) == ':') {
            String strHour = startHour.substring(0, 1).trim();
            hour = Integer.parseInt(strHour);
        } else {
            String strHour = startHour.substring(0, 2).trim();
            hour = Integer.parseInt(strHour);
        }

        String strMinute = startHour.substring(startHour.length() - 4, startHour.length() - 2);
        int minute = Integer.parseInt(strMinute);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }


}
