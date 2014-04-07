package com.akadasoftware.danceworksonline.classes;

/**
 * Created by Kyle on 4/7/2014.
 * Globally available methods that you might use anywhere.
 */
public class Globals {


    public String BuildQuery(int selection, int sort) {

        String Query, Sort = "", Select = "";


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

        Query = Select + Sort;


        return Query;
    }
}
