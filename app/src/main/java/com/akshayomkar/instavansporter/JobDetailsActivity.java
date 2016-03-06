package com.akshayomkar.instavansporter;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Akshay on 3/6/2016.
 */
public class JobDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "JobDetailsActivity";
    private ProgressDialog mProgressDialog;

    TextView tv_address, tv_amount,tv_date,tv_time,tv_status,tv_accept,tv_decline,tv_completed;

    private Job mJob;

    private GoogleMap mMap;

    private Marker srcMarker = null, destMarker = null;
    private MarkerOptions srcMarkerOpt, destMarkerOpt;
    private Polyline polyline = null;
    private PolylineOptions lineOptions = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_details_layout);



        int position = getIntent().getIntExtra("pos",-1);
        if(position<0){
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
        }else {
            mJob = new Job();
            mJob = MainActivity.sJobListAvailable.get(position);
            init();
            SetupMap();

            setData();

            tv_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateAccept();
                }
            });
            tv_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            tv_completed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateCompleted();
                }
            });

        }
    }

    private void updateAccept() {

        showProgressDialog();
        StringRequest strReq = new StringRequest( Request.Method.POST, Constants.API_URL_JOB_ACCEPT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        if (response.toLowerCase().contains("success")){
                            Toast.makeText(getApplicationContext(),"Job accepted! You gained 10 credits",Toast.LENGTH_SHORT).show();
                            DataAccessStaticHelper.getInstance().addCredit(10);
                        }
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(),
                            "Connection timeout\nPoor Internet Connection",
                            Toast.LENGTH_LONG).show();
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
                hideProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("pid",DataAccessStaticHelper.getInstance().getPid());
                params.put("job_id",mJob.getJob_id());
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.VOLLEY_TIMEOUT_MS,
                Constants.VOLLEY_RETRY_COUNT,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getsInstance().addToRequestQueue(strReq);
    }

    private void updateCompleted() {
        showProgressDialog();
        StringRequest strReq = new StringRequest( Request.Method.POST, Constants.API_URL_JOB_COMPLETED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        if (response.toLowerCase().contains("success")){
                            Toast.makeText(getApplicationContext(), "Congrats! You gained 50 credits", Toast.LENGTH_SHORT).show();
                            DataAccessStaticHelper.getInstance().addCredit(50);

                            /*
                            * job.setAmount(jsonObject.getString("AMOUNT"));
                    job.setDest_lat(jsonObject.getString("DEST_LAT"));
                    job.setDest_lng(jsonObject.getString("DEST_LNG"));
                    job.setNum_porter(jsonObject.getString("NUM_PORTER"));
                    job.setSrc_lat(jsonObject.getString("SRC_LAT"));
                    job.setSrc_lng(jsonObject.getString("SRC_LNG"));
                    job.setStatus(jsonObject.getString("STATUS"));
                    job.setStr_dest(jsonObject.getString("DEST_ADDR"));
                    job.setStr_src(jsonObject.getString("SRC_ADDR"));
                    job.setSrc_time(jsonObject.getString("SRC_TIME"));
                    job.setJob_id(jsonObject.getString("JOB_ID"));
                            * */

                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("AMOUNT",mJob.getAmount());
                                jsonObject.put("DEST_LAT",mJob.getDest_lat());
                                jsonObject.put("DEST_LNG",mJob.getDest_lng());
                                jsonObject.put("NUM_PORTER",mJob.getNum_porter());
                                jsonObject.put("SRC_LAT",mJob.getSrc_lat());jsonObject.put("STATUS",mJob.getStatus());
                                jsonObject.put("SRC_LNG",mJob.getSrc_lng());
                                jsonObject.put("DEST_ADDR",mJob.getStr_dest());
                                jsonObject.put("SRC_ADDR",mJob.getStr_src());
                                jsonObject.put("SRC_TIME",mJob.getSrc_time());jsonObject.put("JOB_ID",mJob.getJob_id());

                                DataAccessStaticHelper.getInstance().appendJobCompleted(jsonObject.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(),
                            "Connection timeout\nPoor Internet Connection",
                            Toast.LENGTH_LONG).show();
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
                hideProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("pid",DataAccessStaticHelper.getInstance().getPid());
                params.put("job_id",mJob.getJob_id());
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.VOLLEY_TIMEOUT_MS,
                Constants.VOLLEY_RETRY_COUNT,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getsInstance().addToRequestQueue(strReq);
    }

    private void setData() {

        tv_address.setText(mJob.getStr_src());
        String[] tt = mJob.getSrc_time().split(":");
        tv_time.setText("Time: "+tt[0]+"-"+tt[1]);
        tv_date.setText("Date: " + tt[2] + "-" + tt[3]);
        tv_amount.setText("Rs. "+mJob.getAmount());


        DownloadPathsTask downloadPathsTask = new DownloadPathsTask();
        //https://maps.googleapis.com/maps/api/directions/json?origin=18.5329849,73.8247899&destination=18.5373211,73.8858393

        LatLng latLng = new LatLng(Constants.sCurrentLocation.getLatitude(),
                Constants.sCurrentLocation.getLongitude()),

                destLatLng = new LatLng(Double.parseDouble(mJob.getDest_lat()),
                        Double.parseDouble(mJob.getDest_lng()));

        String pathUrl = getDirectionsUrl(latLng, destLatLng);
        Log.d("path url", pathUrl);
        downloadPathsTask.execute(pathUrl);
    }

    private void SetupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //mMap.setMyLocationEnabled(true);
        mapFragment.getMapAsync(this);

    }

    private void init() {
        //set Typefaces fonts
        Typeface tfRobotoLight = Typeface.createFromAsset(getAssets(), "RobotoLight.ttf");
        Typeface tfRobotoThin = Typeface.createFromAsset(getAssets(), "RobotoThin.ttf");
        Typeface tfRobotoThinItalic = Typeface.createFromAsset(getAssets(), "RobotoThinItalic.ttf");

        tv_accept = (TextView) findViewById(R.id.tv_jd_accept);
        tv_decline = (TextView) findViewById(R.id.tv_jd_decline);
        tv_completed = (TextView) findViewById(R.id.tv_jd_completed);

        tv_address = (TextView) findViewById(R.id.tv_jd_address);
        tv_amount = (TextView) findViewById(R.id.tv_jd_amount);
        tv_date = (TextView) findViewById(R.id.tv_jd_date);
        tv_time = (TextView) findViewById(R.id.tv_jd_time);
        //tv_status = (TextView) findViewById(R.id.tv_card_status_indicator);
        //set typeface
        tv_address.setTypeface(tfRobotoLight);
        tv_date.setTypeface(tfRobotoLight);tv_time.setTypeface(tfRobotoLight);
        tv_amount.setTypeface(tfRobotoThin);

        srcMarkerOpt = new MarkerOptions();
        srcMarkerOpt.position(new LatLng(Constants.sCurrentLocation.getLatitude(), Constants.sCurrentLocation.getLongitude())).title("Current location");
        destMarkerOpt = new MarkerOptions();
        destMarkerOpt.position(new LatLng(Double.parseDouble(mJob.getDest_lat()), Double.parseDouble(mJob.getDest_lng()))).title("Destination");

        lineOptions = new PolylineOptions();
        lineOptions.width(5);
        lineOptions.color(Color.BLUE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        srcMarker = mMap.addMarker(srcMarkerOpt);
        destMarker = mMap.addMarker(destMarkerOpt);
        polyline = mMap.addPolyline(lineOptions);


        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(mJob.getDest_lat()),Double.parseDouble(mJob.getDest_lng())));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

    }



    /**
     * Paths maps
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    // Fetches data from url passed
    private class DownloadPathsTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            Log.d("path", "DownloadPathsTask doInBackground");
            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = HttpManager.getData(url[0]);
                Log.d("path", "data doInBackground  --> " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("path", "DownloadPathsTask onPostExecute ");
            ParserPathsTask parserTask = new ParserPathsTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserPathsTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            Log.d("path", "ParserPathsTask doInBackground");
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            Log.d("path", "ParserPathsTask onPostExecute");
            if (result != null) {
                ArrayList<LatLng> points = null;

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();


                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }
                    polyline.remove();
                    // Adding all the points in the route to LineOptions
                    lineOptions = new PolylineOptions();
                    lineOptions.addAll(points);
                    polyline = mMap.addPolyline(lineOptions);

                    /**Change status text*/
                    // tv_path_status.setText("Search and compare nearby cabs");
                }
                // Drawing polyline in the Google Map for the i-th route

            } else {

                // tv_path_status.setText("Error fetching directions");
            }
        }
    }



    /*
--------------------
--Helper functions for progressbar
--------------------
*/
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
