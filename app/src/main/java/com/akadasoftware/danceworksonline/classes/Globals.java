package com.akadasoftware.danceworksonline.classes;

import android.app.Activity;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;

/**
 * Created by Kyle on 4/7/2014.
 * Globally available methods that you might use anywhere.
 */
public class Globals {

    private AppPreferences _appPrefs;

    public String BuildQuery(int selection, int sort, String mTitle) {

        String Query, Sort = "", Select = "";

        if (mTitle.equals("Accounts") || mTitle.equals("Home")) {
            switch (sort) {
                case 0:
                    Sort = " ORDER BY LName,FName,AcctNo";
                    break;
                case 1:
                    Sort = " ORDER BY FName,LName,AcctNo";
                    break;
                case 2:
                    Sort = " ORDER BY AcctNo";
                    break;
                case 3:
                    Sort = " ORDER BY Phone,LName,FName,AcctNo";
                    break;
                case 4:
                    Sort = " ORDER BY Email,LName,FName,AcctNo";
                    break;
                default:
                    Sort = " ORDER BY LName,FName,AcctNo";
                    break;
            }

            switch (selection) {
                case 0:
                    Select = " AND Status=0";
                    break;
                case 1:
                    Select = " AND Status=1";
                    break;
                case 2:
                    Select = " AND (Status=0 or Status=1)";
                    break;
                case 3:
                    Select = " AND Status=2";
                    break;
                case 4:
                    Select = " AND Status=3";
                    break;
                default:
                    Select = "";
                    break;
            }
        } else if (mTitle.equals("Students")) {
            switch (sort) {
                case 0:
                    Sort = " ORDER BY LName,FName,StuID";
                    break;
                case 1:
                    Sort = " ORDER BY FName,LName,StuID";
                    break;
                case 2:
                    Sort = " ORDER BY Phone,LName,FName,StuID";
                    break;
                case 3:
                    Sort = "ORDER BY Email,LName,FName,StuID";
                    break;
                case 4:
                    Sort = " ORDER BY Birthdate,LName,FName,StuID";
                    break;
                case 5:
                    Sort = " ORDER BY AcctNo,StuID";
                    break;
            }

            switch (selection) {
                case 0:
                    Select = " AND Status=0";
                    break;
                case 1:
                    Select = " AND Status=1";
                    break;
                case 2:
                    Select = " AND (Status=0 or Status=1)";
                    break;
                case 3:
                    Select = " AND Status=2";
                    break;
                default:
                    Select = "";
                    break;

            }
        }
        Query = Select + Sort;


        return Query;
    }


    public void updateAccount(Account account, int position, Activity activity) {

        _appPrefs = new AppPreferences(activity);
        ArrayList<Account> AccountsArray = new ArrayList<Account>();
        AccountsArray = _appPrefs.getAccounts();
        AccountsArray.set(position, account);
        _appPrefs.saveAccounts(AccountsArray);

    }

    public static SoapObject GetSoapObject(String NAMESPACE, String MethodName) {
        return new SoapObject(NAMESPACE, MethodName);
    }



}
