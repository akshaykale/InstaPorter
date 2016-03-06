package com.akshayomkar.instavansporter;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by Akshay on 2/10/2016.
 */
public class VolleySingleton {

    public static VolleySingleton sInstance;
    private RequestQueue mRequestQueue;

    private VolleySingleton(){

        //heavy process so we created a singleton
        mRequestQueue = Volley.newRequestQueue(IPapplication.getAppContext());

    }

    public static VolleySingleton getsInstance(){
        if(sInstance == null){
            sInstance = new VolleySingleton();
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }

    public void addToRequestQueue(StringRequest request){
        getRequestQueue().add(request);
    }

    public void clearCacheEntries() {
        Cache cache = getRequestQueue().getCache();
        cache.clear();
    }
}
