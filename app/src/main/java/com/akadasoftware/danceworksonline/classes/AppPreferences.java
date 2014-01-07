package com.akadasoftware.danceworksonline.classes;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DJ on 12/28/13.
 */
public class AppPreferences {
    public static final String ApplicationPreferences = "com.akada.danceworksonline.studio.Login";
    public String putEmail = "email";
    public static final String putCCProcessor = "";
    public static final String putSchID = "SchID";
    public static final String putUserID = "UserID";
    public static final String putStuID = "StuID";
    public static final String putAcctID = "AcctID";
    public static final String putSessionID = "SessionID";
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    Gson gson = new Gson();
    String jsonAccounts;
    String jsonUser;


    public AppPreferences(Context context) {

        this.sharedPrefs = context.getSharedPreferences(ApplicationPreferences,
                0);
        this.prefsEditor = sharedPrefs.edit();

    }

    public String getEmailPref() {
        return sharedPrefs.getString(putEmail, "");
    }

    public void saveEmail(String text) {
        prefsEditor.putString(putEmail, text);
        prefsEditor.commit();
    }

    public String getCCProcessor() {
        return sharedPrefs.getString(putCCProcessor, "");
    }

    public void saveCCProcessor(String text) {
        prefsEditor.putString(putCCProcessor, text);
        prefsEditor.commit();
    }

    public void saveSchID(int SchID) {
        prefsEditor.putInt(putSchID, SchID);
        prefsEditor.commit();
    }

    public int getSchID() {
        return sharedPrefs.getInt(putSchID, 0);
    }

    public void saveUserID(int UserID) {
        prefsEditor.putInt(putUserID, UserID);
        prefsEditor.commit();
    }

    public int getUserID() {
        return sharedPrefs.getInt(putUserID, 0);
    }

    public int getStuID() {
        return sharedPrefs.getInt(putStuID, 0);
    }

    public void saveStuID(int StuID) {
        prefsEditor.putInt(putStuID, StuID);
        prefsEditor.commit();
    }

    public int getAcctID() {
        return sharedPrefs.getInt(putAcctID, 0);
    }

    public void saveAcctID(int AcctID) {
        prefsEditor.putInt(putAcctID, AcctID);
        prefsEditor.commit();
    }

    public int getSessionID() {
        return sharedPrefs.getInt(putSessionID, 0);
    }

    public void saveSessionID(int SessionID) {
        prefsEditor.putInt(putSessionID, SessionID);
        prefsEditor.commit();
    }

    public User getUser(){
        User user = new User();
        jsonUser = sharedPrefs.getString("User", "None found");
        if(jsonUser != "None found"){
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(jsonUser).getAsJsonArray();
            user = gson.fromJson(array.get(0),User.class);
        }
        return user;
    }

    public void saveUser(User user){
        Gson gson = new Gson();
        jsonUser = gson.toJson(user);

        prefsEditor.putString("User", jsonUser);
        prefsEditor.commit();
    }

    public ArrayList<Account> getAccounts() {
        ArrayList<Account> accounts = new ArrayList<Account>();
        jsonAccounts = sharedPrefs.getString("Accounts", "None found");
        if(jsonAccounts != "None found"){
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(jsonAccounts).getAsJsonArray();
            for(int i = 0; i < array.size()-1; i ++){
                Account account = gson.fromJson(array.get(i), Account.class);
                accounts.add(account);
            }
        }
        return accounts;
    }

    public void saveAccounts(ArrayList<Account> accounts) {
        Gson gson = new Gson();
        jsonAccounts = gson.toJson(accounts);

        prefsEditor.putString("Accounts", jsonAccounts);
        prefsEditor.commit();
    }

}