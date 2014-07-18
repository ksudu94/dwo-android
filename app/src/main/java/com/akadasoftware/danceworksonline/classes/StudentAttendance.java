package com.akadasoftware.danceworksonline.Classes;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Kyle on 4/16/2014.
 */
public class StudentAttendance implements KvmSerializable {

    public int ClAID, ClDayNo, ClID;

    public String ClType, ClLevel, ClDescription, ClStart, ADate, Present;

    public Boolean MultiDay;

    public StudentAttendance() {
    }

    public StudentAttendance(int claid, Boolean multiday, int cldayno, int clid, String cltype,
                             String cllevel, String cldescription, String clstart, String adate,
                             String present) {

        claid = ClAID;
        multiday = MultiDay;
        cldayno = ClDayNo;
        clid = ClID;
        cltype = ClType;
        cllevel = ClLevel;
        cldescription = ClDescription;
        clstart = ClStart;
        adate = ADate;
        present = Present;
    }

    @Override
    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return ClAID;
            case 1:
                return MultiDay;
            case 2:
                return ClDayNo;
            case 3:
                return ClID;
            case 4:
                return ClType;
            case 5:
                return ClLevel;
            case 6:
                return ClDescription;
            case 7:
                return ClStart;
            case 8:
                return ADate;
            case 9:
                return Present;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 10;
    }

    @Override
    public void getPropertyInfo(int index, Hashtable hashtable, PropertyInfo info) {
        switch (index) {
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "ClAID";
                break;
            case 1:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "MultiDay";
                break;
            case 2:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "ClDayNo";
                break;
            case 3:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "ClID";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ClType";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ClLevel";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ClDescription";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ClStart";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ADate";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Present";
                break;
        }
    }

    @Override
    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                ClAID = Integer.parseInt(value.toString());
                break;
            case 1:
                MultiDay = Boolean.parseBoolean(value.toString());
                break;
            case 2:
                ClDayNo = Integer.parseInt(value.toString());
                break;
            case 3:
                ClID = Integer.parseInt(value.toString());
                break;
            case 4:
                if (value.equals("anyType{}")) {
                    ClType = "";
                } else {
                    ClType = value.toString();
                }
                break;
            case 5:
                if (value.equals("anyType{}")) {
                    ClLevel = "";
                } else {
                    ClLevel = value.toString();
                }
                break;
            case 6:
                if (value.equals("anyType{}")) {
                    ClDescription = "";
                } else {
                    ClDescription = value.toString();
                }
            case 7:
                if (value.equals("anyType{}")) {
                    ClStart = "";
                } else {
                    ClStart = value.toString();
                }
                break;
            case 8:
                if (value.equals("anyType{}")) {
                    ADate = "";
                } else {
                    ADate = value.toString();
                }
                break;
            case 9:
                if (value.equals("anyType{}")) {
                    Present = "";
                } else {
                    Present = value.toString();
                }
                break;
            default:
                break;

        }
    }

}
