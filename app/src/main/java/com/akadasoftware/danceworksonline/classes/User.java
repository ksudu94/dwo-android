package com.akadasoftware.danceworksonline.classes;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by DJ on 12/27/13.
 */
public class User implements KvmSerializable {

    public int SchID, UserID, TransType, TransPaymentKind, TransChargeKind,
            StudentSort, StudentSelection, StudentsShown, StudentGroupCode,
            AccountSort, AccountSelection, AccountsShown, AccountGroupCode,
            ClassSort, ClassesShown, StaffSort, StaffShown,AccountListSize,
            StudentListSize,ClassListSize,StaffListSize;
    public long Access;
    public String UserName, EMailAddr, DisplayName,
            TransStartDate, TransEndDate,UserGUID;
    public boolean Admin, ProcReg, TransTypeOptions, TransDateOptions, TransDeleted, StudentOptions,
            AccountOptions, ClassOptions, StaffOptions, ShowNotes,
            ShowTransactions, ShowRegistrations;

    public User() {
    }

    public User(int schid, int userid, String username, String emailaddr, boolean admin,long access, boolean procreg,
                String displayname,boolean transtypeoptions, int transtype, int transpaymentkind,
                int transchargekind, boolean transdateoptions,String transstartdate, String transenddate,
                boolean transdeleted,boolean studentoptions, int studentsort, int studentselection,
                int studentsshown, int studentgroupcode, boolean accountoptions,
                int accountsort, int accountselection, int accountsshown,
                int accountgroupcode, boolean classoptions, int classsort,
                int classesshown, boolean staffoptions, int staffsort, int staffshown,
                boolean shownotes, boolean showtransactions, boolean showregistrations, int accountlistsize,
                int studentlistsize, int classlistsize, int stafflistsize, String userguid) {

        SchID = schid;
        UserID = userid;
        UserName = username;
        EMailAddr = emailaddr;
        Admin = admin;
        Access = access;
        ProcReg = procreg;
        DisplayName = displayname;
        TransTypeOptions = transtypeoptions;
        TransType = transtype;
        TransPaymentKind = transpaymentkind;
        TransChargeKind = transchargekind;
        TransDateOptions = transdateoptions;
        TransStartDate = transstartdate;
        TransEndDate = transenddate;
        TransDeleted = transdeleted;
        StudentOptions = studentoptions;
        StudentSort = studentsort;
        StudentSelection = studentselection;
        StudentsShown = studentsshown;
        StudentGroupCode = studentgroupcode;
        AccountOptions = accountoptions;
        AccountSort = accountsort;
        AccountSelection = accountselection;
        AccountsShown = accountsshown;
        AccountGroupCode = accountgroupcode;
        ClassOptions = classoptions;
        ClassSort = classsort;
        ClassesShown = classesshown;
        StaffOptions = staffoptions;
        StaffSort = staffsort;
        StaffShown = staffshown;
        ShowNotes = shownotes;
        ShowTransactions = showtransactions;
        ShowRegistrations = showregistrations;
        AccountListSize = accountlistsize;
        StudentListSize= studentlistsize;
        ClassListSize = classlistsize;
        StaffListSize = stafflistsize;
        UserGUID = userguid;
    }

    @Override
    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return SchID;
            case 1:
                return UserID;
            case 2:
                return UserName;
            case 3:
                return EMailAddr;
            case 4:
                return Admin;
            case 5:
                return ProcReg;
            case 6:
                return DisplayName;
            case 7:
                return TransTypeOptions;
            case 8:
                return TransType;
            case 9:
                return TransPaymentKind;
            case 10:
                return TransChargeKind;
            case 11:
                return TransDateOptions ;
            case 12:
                return TransStartDate;
            case 13:
                return TransEndDate;
            case 14:
                return TransDeleted;
            case 15:
                return StudentOptions;
            case 16:
                return StudentSort;
            case 17:
                return StudentSelection;
            case 18:
                return StudentsShown;
            case 19:
                return StudentGroupCode;
            case 20:
                return AccountOptions;
            case 21:
                return AccountSort;
            case 22:
                return AccountSelection;
            case 23:
                return AccountsShown;
            case 24:
                return AccountGroupCode;
            case 25:
                return ClassOptions;
            case 26:
                return ClassSort;
            case 27:
                return ClassesShown;
            case 28:
                return StaffOptions;
            case 29:
                return StaffSort;
            case 30:
                return StaffShown;
            case 31:
                return ShowNotes;
            case 32:
                return ShowTransactions;
            case 33:
                return ShowRegistrations;
            case 34:
                return AccountListSize;
            case 35:
                return StudentListSize;
            case 36:
                return ClassListSize;
            case 37:
                return StaffListSize;
            case 38:
                return UserGUID;
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        // TODO Auto-generated method stub
        return 39;
    }

    @Override
    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch (index) {
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "SchID";
                break;
            case 1:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "UserID";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "UserName";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "EMailAddr";
                break;
            case 5:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "Admin";
                break;
            case 6:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "ProcReg";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DisplayName";
                break;
            case 8:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "UsesNewPassword";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ResetString";
                break;
            case 10:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "Converted";
                break;
            case 11:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "TransTypeOptions";
                break;
            case 12:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "TransType";
                break;
            case 13:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "TransPaymentKind";
                break;
            case 14:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "TransChargeKind";
                break;
            case 15:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "TransDateOptions";
                break;
            case 16:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TransStartDate";
                break;
            case 17:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TransEndDate";
                break;
            case 18:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "TransDeleted";
                break;
            case 19:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "StudentOptions";
                break;
            case 20:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "StudentSort";
                break;
            case 21:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "StudentSelection";
                break;
            case 22:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "StudentsShown";
                break;
            case 23:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "StudentGroupCode";
                break;
            case 24:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "AccountOptions";
                break;
            case 25:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "AccountSort";
                break;
            case 26:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "AccountSelection";
                break;
            case 27:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "AccountsShown";
                break;
            case 28:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "AccountGroupCode";
                break;
            case 29:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "ClassOptions";
                break;
            case 30:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "ClassSort";
                break;
            case 31:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "ClassesShown";
                break;
            case 32:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "StaffOptions";
                break;
            case 33:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "StaffSort";
                break;
            case 34:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "StaffShown";
                break;
            case 35:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "ShowNotes";
                break;
            case 36:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "ShowTransactions";
                break;
            case 37:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "ShowRegistrations";
                break;
            case 38:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "ShowRegistrationsPopup";
                break;
            default:
                break;
        }

    }

    @Override
    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                SchID = Integer.parseInt(value.toString());
                break;
            case 1:
                UserID = Integer.parseInt(value.toString());
                break;
            case 2:
                UserName = value.toString();
                break;
            case 4:
                EMailAddr = value.toString();
                break;
            case 5:
                Admin = Boolean.parseBoolean(value.toString());
                break;
            case 6:
                ProcReg = Boolean.parseBoolean(value.toString());
                break;
            case 7:
                DisplayName = value.toString();
                break;
            case 8:
                UsesNewPassword = Boolean.parseBoolean(value.toString());
                break;
            case 9:
                ResetString = value.toString();
                break;
            case 10:
                Converted = Boolean.parseBoolean(value.toString());
                break;
            case 11:
                TransTypeOptions = Boolean.parseBoolean(value.toString());
                break;
            case 12:
                TransType = Integer.parseInt(value.toString());
                break;
            case 13:
                TransPaymentKind = Integer.parseInt(value.toString());
                break;
            case 14:
                TransChargeKind = Integer.parseInt(value.toString());
                break;
            case 15:
                TransDateOptions = Boolean.parseBoolean(value.toString());
                break;
            case 16:
                TransStartDate = value.toString();
                break;
            case 17:
                TransEndDate = value.toString();
                break;
            case 18:
                TransDeleted = Boolean.parseBoolean(value.toString());
                break;
            case 19:
                StudentOptions = Boolean.parseBoolean(value.toString());
                break;
            case 20:
                StudentSort = Integer.parseInt(value.toString());
                break;
            case 21:
                StudentSelection = Integer.parseInt(value.toString());
                break;
            case 22:
                StudentsShown = Integer.parseInt(value.toString());
                break;
            case 23:
                StudentGroupCode = Integer.parseInt(value.toString());
                break;
            case 24:
                AccountOptions = Boolean.parseBoolean(value.toString());
                break;
            case 25:
                AccountSort = Integer.parseInt(value.toString());
                break;
            case 26:
                AccountSelection = Integer.parseInt(value.toString());
                break;
            case 27:
                AccountsShown = Integer.parseInt(value.toString());
                break;
            case 28:
                AccountGroupCode = Integer.parseInt(value.toString());
                break;
            case 29:
                ClassOptions = Boolean.parseBoolean(value.toString());
                break;
            case 30:
                ClassSort = Integer.parseInt(value.toString());
                break;
            case 31:
                ClassesShown = Integer.parseInt(value.toString());
                break;
            case 32:
                StaffOptions = Boolean.parseBoolean(value.toString());
                break;
            case 33:
                StaffSort = Integer.parseInt(value.toString());
                break;
            case 34:
                StaffShown = Integer.parseInt(value.toString());
                break;
            case 35:
                ShowNotes = Boolean.parseBoolean(value.toString());
                break;
            case 36:
                ShowTransactions = Boolean.parseBoolean(value.toString());
                break;
            case 37:
                ShowRegistrations = Boolean.parseBoolean(value.toString());
                break;
            case 38:
                ShowRegistrationsPopup = Boolean.parseBoolean(value.toString());
                break;
            default:
                break;
        }

    }
}
