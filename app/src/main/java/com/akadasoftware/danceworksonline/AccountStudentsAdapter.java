package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.Student;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Kyle on 3/22/2014.
 */
public class AccountStudentsAdapter extends ArrayAdapter<Student> {

    private AppPreferences _appPrefs;
    Activity activity;
    int resource;
    List<Student> Students;

    public AccountStudentsAdapter(Context context, int resource, List<Student> items) {
        super(context, resource, items);
        this.resource = resource;
        Students = items;
    }


    public class ViewHolder {
        TextView tvFNameLName;
        TextView tvStatus;

    }

    /**
     * The holder is the container for each list item defined in the ViewHolder class. Below we
     * define them and find out what the equivalent is in our xml file
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("ConertView", String.valueOf(position));
        ViewHolder holder = null;


        // Inflate the view
        if (convertView == null) {

            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            convertView = vi.inflate(R.layout.item_accountstudents, null);

            holder = new ViewHolder();
            holder.tvFNameLName = (TextView) convertView
                    .findViewById(R.id.tvFNameLName);

            holder.tvStatus = (TextView) convertView
                    .findViewById(R.id.tvStatus);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }



        Student stu = Students.get(position);
        String name = stu.FName + " " + stu.LName;
        String status = "";

        switch (stu.Status) {
            case 0:
                status = "Active";
                break;
            case 1:
                status = "Inactive";
                break;
            case 2:
                status = "Prospect";
                break;
            case 3:
                status = "Deleted";
                break;
        }

        holder.tvFNameLName.setText(name + "     ");
        holder.tvFNameLName.setTag(position);

        holder.tvStatus.setText(status);






        return convertView;

    }
}
