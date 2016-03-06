package com.akshayomkar.instavansporter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.LocationListener;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Akshay on 3/5/2016.
 */
public class AlarmReciever extends BroadcastReceiver implements LocationListener, android.location.LocationListener{
    private static final String TAG = "AlarmReciever";
    Hashtable<String, Integer> ht;
    SharedPreferences prefs;
    private static double lat, longi;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 12 * 1;
    protected LocationManager locationManager;
    boolean gps_enabled,network_enabled;
    Location Loca;
    Context con;
    SharedPreferences settings;
    AsyncTask sharetask;




    @Override
    public void onReceive(Context con1, Intent arg1) {
        // TODO Auto-generated method stub

        con=con1;
        locationManager = (LocationManager) con.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        network_enabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);

        //choose the best location provide and check if they are not null also configure them to display lastknown location of user to minimize delay in finding location.
        if (network_enabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            Log.d(TAG, "praying to be here");
            if (locationManager != null)
            {
                Log.d(TAG,"dangerous circumstances");

                Loca = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if(Loca!=null)
                {			Log.d(TAG,"this could be bad");

                    onLocationChanged(Loca);

                }

                locationManager.requestLocationUpdates(provider, 0, 0,this);
            }

        } else if (gps_enabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }

        else if (!gps_enabled && !network_enabled)
        {
            // no network provider is enabled
            Log.d(TAG,"i am finished");
        }

    }




    //come here whenver delivery guy's location updates i.e after min_distance and min_time as specified above
    @Override
    public void onLocationChanged(Location loca) {



        // TODO Auto-generated method stub
        try
        {
            //if(loca!=null)
            {
                lat = loca.getLatitude();
                longi = loca.getLongitude();

                DataAccessStaticHelper.getInstance().setCurrentLocation("" + lat, "" + longi);

                if(DataAccessStaticHelper.getInstance().isActiveUser())
                    updateToServer(lat,longi);

                /*//send location and delivery guy id in an async task.
                sharetask = new AsyncTask<String, Integer, Integer>()
                {
                    protected Integer doInBackground(String... params)
                    {
                        Log.d(TAG,"Alarm Reciever request has been made");
                        HashMap<String,String> args = new HashMap<String,String>();
                        // Adding the userid to the HashMap
                        args.put( "pid", DataAccessStaticHelper.getInstance().getPid());
                        args.put( "latitude", String.valueOf(lat));
                        args.put( "longitude", String.valueOf(longi));


                        ServerComm server_comm = new ServerComm(con);
                        int result = server_comm.upload(args, Constants.API_URL_UPDATE_LOC);
                        return result;
                    }
                    protected void onPostExecute(Integer result)
                    {
                        Log.v(TAG, "Result Querying deliveries: "+result);
                        // Log.d("Requested Data :- ",AppVars.LastServerCommResult+" --  ");
                        String requested_data=AppVars.LastServerCommResult;

                        try {

                            //parse the recied delivery data in json format and store it in aglobal variable in appvars

                            AppVars.DeliveryJSON=new JSONObject(requested_data.substring(requested_data.indexOf("{"), requested_data.lastIndexOf("}") + 1));


                            setDataStructures();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        catch(NullPointerException ne)
                        {
                            ne.printStackTrace();
                            //Toast.makeText(con,"No new data recieved", Toast.LENGTH_SHORT).show();
                        }
                        catch(StringIndexOutOfBoundsException se)
                        {
                            se.printStackTrace();
                            //Toast.makeText(con,"No useful data recieved", Toast.LENGTH_SHORT).show();
                        }
//			        catch(UnknownHostException uhe)
//			        {
//			        	uhe.printStackTrace();
//			        	//Toast.makeText(con,"No useful data recieved", Toast.LENGTH_SHORT).show();
//			        }

                    }

                }.execute(null, null, null);*/




            }
        }
    catch(NullPointerException ne)
        {
            ne.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private void updateToServer(final double lat, final double longi) {
        StringRequest strReq = new StringRequest( Request.Method.POST, Constants.API_URL_UPDATE_LOC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Update Successfully"+response);
                        // Log.d("Requested Data :- ",AppVars.LastServerCommResult+" --  ");

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                } else if (error instanceof AuthFailureError) {
                    //TODO
                    Log.d("ERRRR Handel", "AuthFailureError");
                } else if (error instanceof ServerError) {
                    //TODO
                    Log.d("ERRRR Handel", "ServerError");
                } else if (error instanceof NetworkError) {
                    //TODO
                    Log.d("ERRRR Handel", "NetworkError");
                } else if (error instanceof ParseError) {
                    //TODO
                    Log.d("ERRRR Handel", "ParseError");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put( "porter_id", DataAccessStaticHelper.getInstance().getPid());
                params.put( "latitude", String.valueOf(lat));
                params.put( "longitude", String.valueOf(longi));
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.VOLLEY_TIMEOUT_MS,
                Constants.VOLLEY_RETRY_COUNT,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getsInstance().addToRequestQueue(strReq);

    }
    }