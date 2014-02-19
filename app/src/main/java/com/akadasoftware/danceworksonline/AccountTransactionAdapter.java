package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.akadasoftware.danceworksonline.classes.AccountTransactions;
import com.akadasoftware.danceworksonline.classes.AppPreferences;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Kyle on 1/15/14.
 * In Android development, any time you want to show a list of items vertically you will want to use
 * a ListView and an Adapter. The simplest adapter is called an ArrayAdapter because the adapter
 * takes an Array and converts the items into View objects to be loaded into the ListView container.
 * The ArrayAdapter fits in the middle between the Array (data source) and the List View
 * (representation) and describes two things:
 * Which array to use as the data source
 * How to convert any item in the array to a View object
 */
public class AccountTransactionAdapter extends ArrayAdapter<AccountTransactions> {

    private AppPreferences _appPrefs;
    Activity activity;
    int resource;
    private int selectedPos;
    String response, kind, type;
    Context context;
    List<AccountTransactions> transactions;


    public AccountTransactionAdapter(Context context, int resource, List<AccountTransactions> items) {
        super(context, resource, items);
        this.resource = resource;
        transactions = items;
    }

    public class ViewHolder {
        TextView tvDate;
        TextView tvType;
        TextView tvDescription;
        TextView tvAmount;
        TextView tvBalance;
    }

    /*The holder is the container for each list item defined in the ViewHolder class. Below we
      define them and find out what the equivalent is in our xml file
    */
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("ConertView", String.valueOf(position));
        ViewHolder holder = null;


        // Inflate the view
        if (convertView == null) {

            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            convertView = vi.inflate(R.layout.item_transactionlist, null);

            holder = new ViewHolder();
            holder.tvDate = (TextView) convertView
                    .findViewById(R.id.tvDate);

            holder.tvType = (TextView) convertView
                    .findViewById(R.id.tvType);

            holder.tvDescription = (TextView) convertView
                    .findViewById(R.id.tvDescription);

            holder.tvAmount = (TextView) convertView
                    .findViewById(R.id.tvAmount);

            holder.tvBalance = (TextView) convertView
                    .findViewById(R.id.tvBalance);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        AccountTransactions trans = transactions.get(position);
        NumberFormat format = NumberFormat.getCurrencyInstance();
        DateFormat dateformat = DateFormat.getDateInstance();

        if (trans.Kind.equals("$")) {
            kind = "CASH";

        } else if (trans.Kind.equals("C")) {
            kind = "CHECK";

        } else if (trans.Kind.equals("V")) {
            kind = "VISA";

        } else if (trans.Kind.equals("M")) {
            kind = "M/C";

        } else if (trans.Kind.equals("D")) {
            kind = "DISC";

        } else if (trans.Kind.equals("A")) {
            kind = "AMEX";

        } else if (trans.Kind.equals("O")) {
            kind = "OTHER";

        } else {
            kind = "Refund";

        }

        if (trans.Type.equals("C")) {
            type = "Charge";

        } else if (trans.Type.equals("P")) {
            type = "Payment";

        } else if (trans.Type.equals("R")) {
            type = "Refund";

        } else if (trans.Type.equals("M")) {
            type = "Credit";

        } else {
            type = "I have no freaking clue";

        }

        //String date with time portion chopped off
        String date = trans.TDate.substring(0, 10);
        //Switching order around so that mm/dd/yyyy
        String testdate = date.substring(5, date.length()) + "/" + date.substring(0, 4);
        String finaldate = testdate.replace("-", "/");

        holder.tvDate.setText(finaldate + " ");
        holder.tvDate.setTag(position);
        holder.tvDate.setWidth(175);

        if (trans.Type.contains("P") || trans.Type.contains("R"))
            holder.tvType.setText(type + "\n" + kind);
        else
            holder.tvType.setText(type + " ");
        holder.tvType.setWidth(175);

        holder.tvDescription.setText(trans.TDesc + " ");
        holder.tvDescription.setWidth(175);
        holder.tvAmount.setText(" " + String.valueOf(format.format(trans.Amount)));
        holder.tvAmount.setWidth(160);


        return convertView;

    }

}
