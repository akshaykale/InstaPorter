package com.akshayomkar.instavansporter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;

/**
 * Created by Akshay on 1/14/2016.
 */
public class SplashScreenActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SplashScreenActivity";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;

    private int CONNECTION_RETRY = 1;

    Context context;

    DataAccessStaticHelper dataAccessStaticHelper ;

    /** GCM*/
    GoogleCloudMessaging gcm;
    String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreenlayout);

        dataAccessStaticHelper = DataAccessStaticHelper.getInstance();

        Log.d("dada",""+dataAccessStaticHelper.getGCMID());
        if (dataAccessStaticHelper.getGCMID().equals("")){
            generateGCMID();
        }else {

        }

        if (checkLocation()) {
            //Check GooglePlayServices is installed
            int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
            if (status == ConnectionResult.SUCCESS) {
                Log.d("SUCCESS", "AVAILABLE");
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
            } else {
                int requestCode = 10;
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
                dialog.show();
            }
        }



    }

    private void generateGCMID() {

        Log.d(TAG, "Setting GCM");

        // First check for registration details

        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            dataAccessStaticHelper.setGCMID(regid);
            //trying this put becoz database isnt getting updated with gcmid and deliveryod
            registerInBackground();

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

    }

    private boolean checkLocation() {
        mLocationManager = null;
        boolean gps_enabled = false,
                network_enabled = false;

        if (mLocationManager == null)
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
//            network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (gps_enabled == false && network_enabled == false) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("GPS Not enabled");
            dialog.setPositiveButton("Open Location Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.create().show();
        }
        return true;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exiting");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("GPS is not enabled or Poor Internet connection");
        alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            Toast.makeText(getApplicationContext(), "Fetching Location...", Toast.LENGTH_SHORT).show();
            if (mLastLocation != null) {
                Constants.sCurrentLocation = mLastLocation;
                Log.d("Loc",String.valueOf(mLastLocation.getLatitude()));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        StartActivityWithCheck();
                    }
                }, 2000);
            } else {
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            }

        } catch (Exception e) {
            showSettingsAlert();
        } finally {
            mGoogleApiClient.disconnect();

        }

    }

    private void StartActivityWithCheck() {



        //already loged in
        if (DataAccessStaticHelper.getInstance().isLoggedIn()) {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        //need to login
        else{
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if(CONNECTION_RETRY >0){
            Toast.makeText(getApplicationContext(), "Reconnecting...", Toast.LENGTH_SHORT).show();
            mGoogleApiClient.reconnect();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status == ConnectionResult.SUCCESS) {
            Log.d("SUCCESS", "AVAILABLE");
            mGoogleApiClient.connect();
        } else {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }
        Toast.makeText(this, "resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }
















    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        9000).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }



    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);

        Log.i(TAG, "Saving regId on app reggggggg " + regId);
        dataAccessStaticHelper.setGCMID(regId);
        dataAccessStaticHelper.setAppVersion(""+appVersion);
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        String registrationId = dataAccessStaticHelper.getGCMID();
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = Integer.parseInt(dataAccessStaticHelper.getAppVersion());
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        Log.i(TAG, "Old Registration ID: " + registrationId);

        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(Constants.SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    dataAccessStaticHelper.setGCMID(regid);

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.v(TAG, msg);
            }
        }.execute(null, null, null);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Create a HashMap which stores Strings as the keys and values
        /*HashMap<String,String> args = new HashMap<String,String>();

        // Adding the userid to the HashMap
        args.put( AppVars.ARG_DELIVERYGUY_ID, String.valueOf(AppVars.DeliveryGuyId));
        args.put( "gcmid", regid);

        ServerComm server_comm = new ServerComm(context);
        int result = server_comm.upload(args, AppVars.UPLOAD_GCMID_URL);

        Log.v(AppVars.LOG_TAG, "Result of uploading GCMID: " + result);*/

    }
}
