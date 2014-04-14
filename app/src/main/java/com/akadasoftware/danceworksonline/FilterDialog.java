package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.Globals;


/**
 * Created by Kyle on 4/7/2014.
 */
public class FilterDialog extends DialogFragment {

    Spinner spinnerSort, spinnerSelect;
    AppPreferences _appPrefs;
    int sort, select;
    String strQuery;

    public FilterDialog() {
    }


    public interface FilterDialogListener {
        public void onFilterDialogPositiveClick(String mTitle);

    }

    FilterDialogListener Listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            Listener = (FilterDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement FilterDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.filterdialog, null);

        spinnerSort = (Spinner) view.findViewById(R.id.spinnerSort);
        spinnerSelect = (Spinner) view.findViewById(R.id.spinnerSelect);
        _appPrefs = new AppPreferences(getActivity());

        final String mTitle = getArguments().getString("mTitle");
        ArrayAdapter<CharSequence> sortAdapter = null;
        ArrayAdapter<CharSequence> selectAdapter = null;

        if (mTitle.equals("Accounts") || mTitle.equals("Home")) {
            sortAdapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.SortBy, android.R.layout.simple_spinner_item);

            selectAdapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.SelectBy, android.R.layout.simple_spinner_item);

            spinnerSelect.setSelection(_appPrefs.getAccountSelectBy());
            spinnerSort.setSelection(_appPrefs.getAccountSortBy());
        } else if (mTitle.equals("Students")) {
            sortAdapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.StudentSortBy, android.R.layout.simple_spinner_item);

            selectAdapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.StudentSelectBy, android.R.layout.simple_spinner_item);

            spinnerSelect.setSelection(_appPrefs.getStudentSelectBy());
            spinnerSort.setSelection(_appPrefs.getStudentSortBy());

        } else if (mTitle.equals("Classes")) {

        }

        spinnerSort.setAdapter(sortAdapter);
        spinnerSelect.setAdapter(selectAdapter);

        builder.setView(view)
                // Title of dialog
                .setTitle("Change Filter Settings")

                        // Add action buttons
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        sort = spinnerSort.getSelectedItemPosition();
                        select = spinnerSelect.getSelectedItemPosition();

                        Globals global = new Globals();
                        strQuery = global.BuildQuery(select, sort, mTitle);

                        if (mTitle.equals("Accounts") || mTitle.equals("Home")) {
                            _appPrefs.saveAccountSortBy(sort);
                            _appPrefs.saveAccountSelectBy(select);
                            _appPrefs.saveAccountQuery(strQuery);
                        } else if (mTitle.equals("Students")) {
                            _appPrefs.saveStudentSortBy(sort);
                            _appPrefs.saveStudentSelectBy(select);
                            _appPrefs.saveStudentQuery(strQuery);
                        }

                        Listener.onFilterDialogPositiveClick(mTitle);


                    }


                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FilterDialog.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }

}
