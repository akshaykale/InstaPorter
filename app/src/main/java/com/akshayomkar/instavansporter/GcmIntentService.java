package com.akshayomkar.instavansporter;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock. create a notification of data that the app might recieve as a push notification
 */
public class GcmIntentService extends IntentService {
    private static final String TAG = "GcmIntentService";
    public static int NOTIFICATION_ID = 1;

    DataAccessStaticHelper dataAccessStaticHelper;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private int mId = 1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"handle  ");
        dataAccessStaticHelper = DataAccessStaticHelper.getInstance();
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            // If it's a regular GCM message, do some work.
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                //sendNotification(extras);
                Log.i(TAG, "Received: " + extras.toString());

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("My notification")
                                .setContentText("Hello World!");
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(this, SplashScreenActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(SplashScreenActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(mId, mBuilder.build());

                parseNotificationData(extras.toString());

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReciever.completeWakefulIntent(intent);
    }

    private void parseNotificationData(String response) {

        String newResp = response.replace("Bundle[","");newResp = newResp.replace("]","");
        Log.d(TAG,"rm b "+newResp);
        try {
            JSONObject jsonObject = new JSONObject(newResp);

            JSONObject dataJO = jsonObject.getJSONObject("sc_description");

            dataAccessStaticHelper.appendNotiData(dataJO.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle extras) {

        // DO NOT SHOW NOTIFICATION IF NOT LOGGED IN
        // Start Downloading data - Once done, call the next activity from postExecute



        if(dataAccessStaticHelper.getPid().equals(""))
        {
            // The user is logged out, ignore this message
            Log.e(TAG, "Notification Activity: Empty User ID!");
            return;
        }

        //Displaying JSON data recieved via GCM and sending it for getting stored in to LoaddData;

        Log.d(TAG,"ffff   "+extras.toString());

        String data = extras.getString("sc_title");





//		int type = Integer.parseInt(extras.getString("sc_type"));
//		int id = Integer.parseInt(extras.getString("sc_id"));


        NOTIFICATION_ID++;

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        /*
         *
         * Create a notification containing all parsed data
         *
         */
        Intent resultIntent = new Intent(this, SplashScreenActivity.class);
//        resultIntent.putExtra(AppVars.NOTIFICATION_ITEM_ID, id);
//        resultIntent.putExtra(AppVars.NOTIFICATION_TYPE, type);
        resultIntent.putExtra("sc_title", data+"\n");
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
                resultIntent, PendingIntent.FLAG_ONE_SHOT);


        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;
//        defaults = defaults | Notification.FLAG_AUTO_CANCEL;

        String title = getApplicationContext().getResources().getString(R.string.app_name);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(data))
                        .setDefaults(defaults)
                        .setContentText(data);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }




}
