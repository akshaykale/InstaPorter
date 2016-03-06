package com.akshayomkar.instavansporter;

import android.app.Application;
import android.content.Context;

/**
 * Created by Akshay on 2/10/2016.
 */
public class IPapplication extends Application {

    public static IPapplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static IPapplication getsInstance(){
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }
}
