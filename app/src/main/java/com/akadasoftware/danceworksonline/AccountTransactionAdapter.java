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

import java.util.List;

/**
 * Created by Kyle on 1/15/14.
 */
public class AccountTransactionAdapter extends ArrayAdapter<AccountTransactions> {

    private AppPreferences _appPrefs;
    Activity activity;
    int resource;
    private int selectedPos;
    String response;
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

        holder.tvDate.setText(trans.TDate);
        holder.tvDate.setTag(position);

        holder.tvType.setText(trans.Type);
        holder.tvDescription.setText(trans.TDesc);
        holder.tvAmount.setText(String.valueOf(trans.Amount));
        holder.tvBalance.setText(String.valueOf(trans.Balance));


        return convertView;

    }

}
