package com.akshayomkar.instavansporter;

import android.location.Location;

/**
 * Created by Akshay on 3/5/2016.
 */
public class Constants {

    /**
     * Volley connection constants
     * */
    public static int VOLLEY_RETRY_COUNT = 3;
    public static int VOLLEY_TIMEOUT_MS = 5000;

    /**
     * GCM
     * */
    public static final String SENDER_ID = "123578857004";



    /**
     * API
     * */
    public static final String API_URL_SIGNUP= "http://omkya.besaba.com/INSTAVAN/signup.php/";
    public static final String API_URL_LOGIN= "http://omkya.besaba.com/INSTAVAN/login.php/";
    public static final String API_URL_UPDATE_LOC= "http://omkya.besaba.com/INSTAVAN/updateloc.php/";
    public static final String API_URL_UPDATE_ACTIVE_STATUS= "http://omkya.besaba.com/INSTAVAN/logout.php/";
    public static final String API_URL_ALL_JOBS= "http://omkya.besaba.com/INSTAVAN/alljobs.php/";
    public static final String API_URL_JOB_ACCEPT= "http://omkya.besaba.com/INSTAVAN/afterjobaccepted.php/";

    public static final String API_URL_JOB_COMPLETED= "http://omkya.besaba.com/INSTAVAN/jobcompleted.php/";

    public static Location sCurrentLocation;

    public static String SHAREDPREF_USER_INFO_FILE = "sharedpref_ip";

}
