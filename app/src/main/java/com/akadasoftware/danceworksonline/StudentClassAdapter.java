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
import com.akadasoftware.danceworksonline.classes.StudentClass;

import java.util.List;

/**
 * Created by Kyle on 4/9/2014.
 */
public class StudentClassAdapter extends ArrayAdapter<StudentClass> {

    private AppPreferences _appPrefs;
    Activity activity;
    int resource;
    //Context newContext;
    List<StudentClass> classes;

    public StudentClassAdapter(Context context, int resource, List<StudentClass> items) {
        super(context, resource, items);
        //newContext = context;
        this.resource = resource;
        classes = items;

    }

    public class ViewHolder {
        TextView tvClassType;
        TextView tvClassLevel;
        TextView tvDescription;
        TextView tvInstructor;
        TextView tvDay;
        TextView tvStart;
        TextView tvStop;
        TextView tvRoom;
    }

    /**
     * The holder is the container for each list item defined in the ViewHolder class. Below we
     * define them and find out what the equivalent is in our xml file
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("ConvertView", String.valueOf(position));
        ViewHolder holder = null;


        // Inflate the view
        if (convertView == null) {

            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            convertView = vi.inflate(R.layout.item_studentclass, null);

            holder = new ViewHolder();

            holder.tvClassType = (TextView) convertView
                    .findViewById(R.id.tvClassType);

            holder.tvClassLevel = (TextView) convertView
                    .findViewById(R.id.tvClassLevel);

            holder.tvDescription = (TextView) convertView
                    .findViewById(R.id.tvDescription);

            holder.tvInstructor = (TextView) convertView
                    .findViewById(R.id.tvInstructor);

            holder.tvDay = (TextView) convertView
                    .findViewById(R.id.tvDay);

            holder.tvStart = (TextView) convertView
                    .findViewById(R.id.tvStart);

            holder.tvStop = (TextView) convertView
                    .findViewById(R.id.tvStop);

            holder.tvRoom = (TextView) convertView
                    .findViewById(R.id.tvRoom);


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        StudentClass stuClass = classes.get(position);
        String Day = "";
        holder.tvClassType.setText(stuClass.ClType);
        holder.tvClassType.setTag(position);


        holder.tvClassLevel.setText(" - " + stuClass.ClLevel + " - ");
        holder.tvDescription.setText(stuClass.ClDescription);
        holder.tvInstructor.setText(stuClass.ClInstructor);

        if (stuClass.MultiDay) {
            if (stuClass.Monday) {
                Day = "Mon";
            }
            if (stuClass.Tuesday) {
                if (Day.isEmpty()) {
                    Day = "Tue";
                } else {
                    Day = Day + "/Tue";
                }
            }
            if (stuClass.Wednesday) {
                if (Day.isEmpty()) {
                    Day = "Wed";
                } else {
                    Day = Day + "/Wed";
                }
            }
            if (stuClass.Thursday) {
                if (Day.isEmpty()) {
                    Day = "Thu";
                } else {
                    Day = Day + "/Thu";
                }
            }
            if (stuClass.Friday) {
                if (Day.isEmpty()) {
                    Day = "Fri";
                } else {
                    Day = Day + "/Fri";
                }
            }
            if (stuClass.Saturday) {
                if (Day.isEmpty()) {
                    Day = "Sat";
                } else {
                    Day = Day + "/Sat";
                }
            }
            if (stuClass.Sunday) {
                if (Day.isEmpty()) {
                    Day = "Sun";
                } else {
                    Day = Day + "/Sun";
                }
            }
        } else {
            Day = stuClass.ClDay;
        }
        holder.tvDay.setText(Day + " from ");
        holder.tvStart.setText(stuClass.ClStart + " - ");
        holder.tvStop.setText(stuClass.ClStop);
        holder.tvRoom.setText(stuClass.ClRoom);

        return convertView;

    }
}
