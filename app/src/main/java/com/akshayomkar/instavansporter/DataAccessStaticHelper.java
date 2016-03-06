package com.akshayomkar.instavansporter;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Akshay on 2/11/2016.
 *
 * SINGLETON class
 *
 * Use for store data and access from a shared preferences files of the app
 *
 * Following Helper functions:
 *  1> Store SID / Retrieve SID
 *  2> Store Username / Retrieve Username
 *  3> Store Password / Retrieve Password
 *
 */
public class DataAccessStaticHelper extends SQLiteOpenHelper{

    public static final String DB_NAME = "ip_notifications.db";
    public static final int DB_VERSION = 1;
    public static final String DB_TABLE_NAME = "available_jobs";
    public static final String DB_COLOUMN_SRC_ADDR = "src_address";
    public static final String DB_COLOUMN_DEST_LNG = "dest_lng";
    public static final String DB_COLOUMN_SRC_LNG = "src_lng";
    public static final String DB_COLOUMN_AMOUNT = "amount";
    public static final String DB_COLOUMN_DEST_ADDR = "dest_address";
    public static final String DB_COLOUMN_SRC_LAT = "src_lat";
    public static final String DB_COLOUMN_DEST_LAT = "dest_lat";

    public static DataAccessStaticHelper sDataAccessStaticHelperInstance;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static SQLiteDatabase myWritableDb;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + DB_TABLE_NAME +
                    " (" + DB_COLOUMN_DEST_ADDR + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    DB_COLOUMN_DEST_LAT + TEXT_TYPE + " )";


    private DataAccessStaticHelper(){
        super(IPapplication.getAppContext(), DB_NAME, null, DB_VERSION);

        sharedPreferences = IPapplication.getAppContext().
                getSharedPreferences(Constants.SHAREDPREF_USER_INFO_FILE, IPapplication.getAppContext().MODE_PRIVATE);

        editor = sharedPreferences.edit();
    }

    public static DataAccessStaticHelper getInstance(){

        if (sDataAccessStaticHelperInstance == null){
            sDataAccessStaticHelperInstance = new DataAccessStaticHelper();
        }
        return sDataAccessStaticHelperInstance;
    }


    public SQLiteDatabase getMyWritableDatabase() {
        if ((myWritableDb == null) || (!myWritableDb.isOpen())) {
            myWritableDb = this.getWritableDatabase();
        }
        return myWritableDb;
    }






    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void close() {
        super.close();
        if (myWritableDb != null) {
            myWritableDb.close();
            myWritableDb = null;
        }
    }


    public void setUserName(String username){
        editor.putString("username", username);
        editor.apply();
    }
    public String getUsername(){
        return sharedPreferences.getString("username","");
    }

    public void setUserPassword(String password){
        editor.putString("password", password);
        editor.apply();
    }
    public String getUserPassword(){
        return sharedPreferences.getString("password","");
    }


    public void setEmail(String x){
        editor.putString("email", x);
        editor.apply();
    }
    public String getEmail(){
        return sharedPreferences.getString("email","");
    }


    public void setLoginStatus(String x){
        editor.putString("islogedin", x);
        editor.apply();
    }
    public boolean isLoggedIn(){
        String x = sharedPreferences.getString("islogedin","0");
        if(x.equals("0"))
            return false;
        else if (x.equals("1"))
            return true;
        return false;
    }

    public void setActiveStatus(String x){
        editor.putString("isactive", x);
        editor.apply();
    }
    public boolean isActiveUser(){
        String x = sharedPreferences.getString("isactive","0");
        if(x.equals("0"))
            return false;
        else if (x.equals("1"))
            return true;
        return false;
    }



    public void setGCMID(String x){
        editor.putString("gcmid", x);
        editor.apply();
    }
    public String getGCMID(){
        return sharedPreferences.getString("gcmid","");
    }

    public void setAppVersion(String x){
        editor.putString("appversion", x);
        editor.apply();
    }
    public String getAppVersion(){
        return sharedPreferences.getString("appversion","");
    }


    public void setPid(String x){
        editor.putString("pid", x);
        editor.apply();
    }
    public String getPid(){
        return sharedPreferences.getString("pid","");
    }

    public void setCurrentLocation(String lat,String lng){
        editor.putString("lat", lat);
        editor.putString("lng", lng);
        editor.apply();
    }
    public LatLng getCurrentLocation(){
        String lat = sharedPreferences.getString("lat","");
        String lng = sharedPreferences.getString("lng","");
        if(lat.equals("") || lng.equals(""))
            return null;
        return new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
    }








    public void appendNotiData(String x){
        String d = sharedPreferences.getString("available_jobs_noti", "");
        if (!d.equals(""))
            d = d + "#" + x;
        else
            d = x;
        editor.putString("available_jobs_noti", d);
        editor.apply();
    }
    public String getNotiData(){
        return sharedPreferences.getString("available_jobs_noti","");
    }


    public void appendJobCompleted(String x){
        String d = sharedPreferences.getString("completed_jobs_noti", "");
        if (!d.equals(""))
            d = d + "#" + x;
        else
            d = x;
        editor.putString("completed_jobs_noti", d);
        editor.apply();
    }
    public String getCompletedJobs(){
        return sharedPreferences.getString("completed_jobs_noti","");
    }


    public void saveJobDoneDialogInfo(String x){
        editor.putString("job_dialog", x);
        editor.apply();
    }
    public String getJobDoneDialogInfo(){
        return sharedPreferences.getString("job_dialog","");
    }

    public void addCredit(int x){
        String d = sharedPreferences.getString("credit","");
        if (!d.equals("")){
            d=""+(Integer.parseInt(d)+x);
        }
        else
            d = ""+x;
        editor.putString("credit", d);
        editor.apply();
    }
    public String getCredit(){
        return sharedPreferences.getString("credit","");
    }

}
