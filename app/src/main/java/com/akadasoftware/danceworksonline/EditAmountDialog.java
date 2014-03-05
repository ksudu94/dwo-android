package com.akadasoftware.danceworksonline;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
// ...

public class EditAmountDialog extends DialogFragment implements TextView.OnEditorActionListener {

    TextView tvAmount;
    EditText etAmount;

    public EditAmountDialog() {
        // Empty constructor required for DialogFragment
    }

    //Uses the interface so that we can communicate from fragment to parent activity
    public interface EditAmountDialogListener {
        void onFinishEditAmountDialog(String input);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.editamountdialog, container);
        tvAmount = (TextView) view.findViewById(R.id.tvAmount);
        etAmount = (EditText) view.findViewById(R.id.etAmount);
        String input = getArguments().getString("Input");
        etAmount.setText(input);
        getDialog().setTitle("Edit the amount");
        etAmount.setOnEditorActionListener(this);
        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            EditAmountDialogListener activity = (EditAmountDialogListener) getActivity();
            if (etAmount.getText().toString().trim().length() > 0) {
                activity.onFinishEditAmountDialog(etAmount.getText().toString());
                this.dismiss();
            } else {
                activity.onFinishEditAmountDialog("0");
                this.dismiss();
            }
            return true;
        }

        return false;
    }
}