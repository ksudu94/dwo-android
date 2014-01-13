package com.akadasoftware.danceworksonline;

/**
 * Created by Kyle on 1/7/14.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.akadasoftware.danceworksonline.AccountListFragment.OnAccountSelectedListener;
import com.akadasoftware.danceworksonline.classes.Account;
import com.akadasoftware.danceworksonline.classes.AppPreferences;

import java.util.List;


public class AccountListAdapater extends ArrayAdapter<Account> {

    private AppPreferences _appPrefs;
    Activity activity;
    int resource;
    private int selectedPos;
    String response;
    Context context;
    List<Account> accounts;

    OnAccountSelectedListener mCallback;

    public AccountListAdapater(Context context, int resource,
                               List<Account> items) {
        super(context, resource, items);
        this.resource = resource;
        accounts = items;
    }

    public void setSelectedPosition(int pos) {

        selectedPos = pos;

        // inform the view of this change
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPos;
    }

    public class ViewHolder {
        TextView tvAccountInformation;
        TextView tvPhone;
        TextView tvEmail;
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
            convertView = vi.inflate(R.layout.item_accountlist, null);

            holder = new ViewHolder();
            holder.tvAccountInformation = (TextView) convertView
                    .findViewById(R.id.tvAccountInformation);

            holder.tvPhone = (TextView) convertView
                    .findViewById(R.id.tvPhone);
            holder.tvPhone.setVisibility(View.GONE);

            holder.tvEmail = (TextView) convertView
                    .findViewById(R.id.tvEmail);
            holder.tvEmail.setVisibility(View.GONE);

            holder.tvBalance = (TextView) convertView
                    .findViewById(R.id.tvBalance);
            holder.tvBalance.setVisibility(View.GONE);


            /*
            if (selectedPos == position) {
                holder.tvAccountInformation.setBackgroundColor(Color
                        .parseColor("#625d9f"));
                holder.tvPhone.setBackgroundColor(Color.parseColor("#625d9f"));
                holder.tvPhone.setBackgroundColor(Color.parseColor("#625d9f"));
                holder.tvEmail.setBackgroundColor(Color.parseColor("#625d9f"));
                holder.tvBalance.setBackgroundColor(Color
                        .parseColor("#625d9f"));
            }
            */
            convertView.setTag(holder);

            holder.tvAccountInformation
                    .setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            _appPrefs = new AppPreferences(v.getContext());
                            TextView tvAccountInformation = (TextView) v;

                            //Account account = (Account) tvAccountInformation
                            //     .getTag();
                            int pos = (Integer) tvAccountInformation.getTag();

                            /*When clicked, OnAccountSelected is called which is in the Home.java
                            page and from there a new activity is created. mCallBack is the listener
                            defined out specifically for when new accounts are selected.
                             */
                            try {
                                mCallback = (OnAccountSelectedListener) v
                                        .getContext();
                                if (mCallback != null) {
                                    mCallback.OnAccountSelected(pos);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //_appPrefs.saveAcctID(account.AcctID);


                        }
                    });

           /* holder.tvAccountInformation
                    .setOnLongClickListener(new View.OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            // TODO Auto-generated method stub
                            TextView tvAccountInformation = (TextView) v;
                            View parentView = (View) v.getParent();

                            TextView tvEmail = (TextView) parentView
                                    .findViewById(R.id.tvEmail);

                            TextView tvPhone = (TextView) parentView
                                    .findViewById(R.id.tvPhone);

                            TextView tvBalance = (TextView) parentView
                                    .findViewById(R.id.tvBalance);

                            Boolean collapsed = true;

                            if (tvEmail.getVisibility() == 0
                                    || tvPhone.getVisibility() == 0
                                    || tvBalance.getVisibility() == 0)
                                collapsed = false;

                            if (collapsed) {

                                Account account = (Account) tvAccountInformation
                                        .getTag();
                                String result = "";
                                switch (account.Status) {
                                    case 0:
                                        result = "Active";
                                        break;
                                    case 1:
                                        result = "Inactive";
                                        break;
                                    case 2:
                                        result = "Prospect";
                                        break;
                                    case 3:
                                        result = "Deleted";
                                        break;
                                }

                                final SpannableStringBuilder sb = new SpannableStringBuilder(
                                        account.LNameFName);
                                final StyleSpan bss = new StyleSpan(
                                        android.graphics.Typeface.BOLD);
                                sb.setSpan(bss, 0, account.LNameFName.length(),
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                                NumberFormat format = NumberFormat
                                        .getCurrencyInstance();

                                tvAccountInformation.setText(sb + " ("
                                        + account.AcctNo + ") " + result);
                                tvAccountInformation.setTypeface(null,
                                        Typeface.BOLD);

                                String strPhone = account.Phone;
                                if (strPhone.trim().length() > 0) {
                                    tvPhone.setText(strPhone);
                                    tvPhone.setTextColor(Color.BLUE);
                                    tvPhone.setPaintFlags(tvEmail
                                            .getPaintFlags()
                                            | Paint.UNDERLINE_TEXT_FLAG);
                                    tvPhone.setVisibility(View.VISIBLE);
                                }

                                String strEmail = account.EMail;
                                if (strEmail.trim().length() > 0) {
                                    tvEmail.setText(strEmail);
                                    tvEmail.setTextColor(Color.BLUE);
                                    tvEmail.setPaintFlags(tvEmail
                                            .getPaintFlags()
                                            | Paint.UNDERLINE_TEXT_FLAG);
                                    tvEmail.setVisibility(View.VISIBLE);
                                }

                                tvBalance.setText(format
                                        .format(account.Balance));
                                tvBalance.setVisibility(View.VISIBLE);
                            } else {
                                tvEmail.setVisibility(View.GONE);

                                tvPhone.setVisibility(View.GONE);

                                tvBalance.setVisibility(View.GONE);
                            }

                            return true;
                        }
                    });*/

            holder.tvPhone.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    View parentView = (View) v.getParent();
                    Context cont = v.getContext();
                    TextView tvPhone = (TextView) parentView
                            .findViewById(R.id.tvPhone);

                    String phone = tvPhone.getText().toString();
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri
                            .parse("tel:" + phone));

                    try {
                        cont.startActivity(callIntent);
                    } catch (Exception e) {
                        tvPhone.setText(e.toString());
                    }

                }
            });

            holder.tvEmail.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    TextView tvAccountInformation = (TextView) v;
                    Account account = (Account) tvAccountInformation
                            .getTag();

                    View parentView = (View) v.getParent();
                    Context cont = v.getContext();
                    TextView tvEmail = (TextView) parentView
                            .findViewById(R.id.tvEmail);

                    String email = tvEmail.getText().toString();

                    Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri
                            .fromParts("mailto", email, null));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                            "Test Akada Account");
                    emailIntent.setType("plain/text");

                    try {
                        cont.startActivity(Intent.createChooser(emailIntent,
                                "Send email..."));

                    } catch (Exception e) {
                        tvEmail.setText(e.toString());
                    }
                }
            });

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        Account account = accounts.get(position);

        holder.tvAccountInformation.setText(account.LName + ", " + account.FName);
        holder.tvAccountInformation.setTag(position);

        holder.tvAccountInformation.setTextSize(20);

        return convertView;

    }

}
