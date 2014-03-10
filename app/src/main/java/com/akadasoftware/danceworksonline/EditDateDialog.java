package com.akadasoftware.danceworksonline;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;


/**
 * Created by Kyle on 3/10/14.
 */
public class EditDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    public EditDateDialog() {
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        EditDateDialogListener activity = (EditDateDialogListener) getActivity();

        datePicker.setCalendarViewShown(false);
        String date = String.valueOf(month) + "-" + String.valueOf(day) + "-" + String.valueOf(year);
        activity.onFinishEditDateDialog(date);
        this.dismiss();
    }


    public interface EditDateDialogListener {
        void onFinishEditDateDialog(String date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        String date = getArguments().getString("Date");


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, 2014, 03, 01);
    }

}
