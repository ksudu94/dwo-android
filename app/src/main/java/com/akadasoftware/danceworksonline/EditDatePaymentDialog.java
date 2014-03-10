package com.akadasoftware.danceworksonline;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;


/**
 * Created by Kyle on 3/10/14.
 */
public class EditDatePaymentDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    DatePicker datepicker;

    public EditDatePaymentDialog() {
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        EditDatePaymentDialogListener activity = (EditDatePaymentDialogListener) getActivity();

        datePicker.setCalendarViewShown(false);
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.YEAR, year);
        selectedDate.set(Calendar.MONTH, month);
        selectedDate.set(Calendar.DATE, day);

        activity.onFinishEditDatePaymentDialog(selectedDate);
        this.dismiss();
    }


    public interface EditDatePaymentDialogListener {
        void onFinishEditDatePaymentDialog(Calendar cal);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        Calendar cal = (Calendar) getActivity().getIntent().getSerializableExtra("Calendar");

        /*int month = getArguments().getInt("Month");
        int day = getArguments().getInt("Day");
        int year = getArguments().getInt("Year");
        */
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        int year = cal.get(Calendar.YEAR);


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

}
