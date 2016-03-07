package com.akshayomkar.instavansporter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.akshayomkar.instavansporter.utils.Utils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    //UI
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private static ProgressDialog mProgressDialog;
    private FloatingActionButton fab;

    DataAccessStaticHelper dataAccessStaticHelper;

    public static ArrayList<Job> sJobListAvailable,sJobListCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //check Internet Connection And Make Network Request
        checkInternetConnectionAndMakeNetworkRequest();
    }

    private void initNavDrawer() {
        DrawerFragment navDrawer = (DrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_nav_drawer);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navDrawer.setUpDrawer(drawerLayout, toolbar);
    }

    private void init() {
        sJobListAvailable = new ArrayList<>();
        sJobListCompleted = new ArrayList<>();

        dataAccessStaticHelper = DataAccessStaticHelper.getInstance();

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mViewPager = (ViewPager) findViewById(R.id.view_pager_tabs);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        initSlidingTabs();
        initNavDrawer();
    }

    private void initSlidingTabs() {
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return R.color.colorAccent;
            }
        });
        mSlidingTabLayout.setViewPager(mViewPager);
    }


    /**############
     * Network Data Request SECTION
     * ############
     * */
    private void checkInternetConnectionAndMakeNetworkRequest() {
        //Check Internet Connectivity
        if(Utils.isOnline(getApplicationContext())){
            showProgressDialog();
            /** network req*/

            getAvailableJobs();

            getCompletedJobs();

            hideProgressDialog();
        }else {
            //Check is offline blog is available
            showConnectivityErrorDialog();
        }
    }

    private void getCompletedJobs() {
        String rawData = dataAccessStaticHelper.getCompletedJobs();
        Log.d(TAG,"rawdata = "+rawData);
        if (!rawData.equals("")) {
            String[] dataArr = rawData.split("#");
            for (int i = 0; i < dataArr.length; i++) {
                try {
                    JSONObject jsonObject = new JSONObject(dataArr[i]);
                    Job job = new Job();
                    job.setAmount(jsonObject.getString("AMOUNT"));
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
                    //job.setPorterslist(jsonObject.getString("PORTER_LIST"));
                    sJobListCompleted.add(job);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void getAvailableJobs() {

        String rawData = dataAccessStaticHelper.getNotiData();
        Log.d(TAG, "rawdata = " + rawData);
        if (!rawData.equals("")) {
            String[] dataArr = rawData.split("#");
            for (int i = 0; i < dataArr.length; i++) {
                if (dataArr[i].toLowerCase().contains("job_id")) {
                    try {
                        JSONObject jsonObject = new JSONObject(dataArr[i]);
                        Job job = new Job();
                        job.setAmount(jsonObject.getString("AMOUNT"));
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
                        //job.setPorterslist(jsonObject.getString("PORTER_LIST"));
                        sJobListAvailable.add(job);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Inflate your Menu

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.getItem(0);
        if(!dataAccessStaticHelper.isActiveUser())
            item.setIcon(R.drawable.toggle_off);
        return true;
    }

    private void updateUserActiveStatus() {
        showProgressDialog();
        StringRequest strReq = new StringRequest( Request.Method.POST, Constants.API_URL_UPDATE_ACTIVE_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            if (response.contains("SUCCESS")){
                                hideProgressDialog();
                                if(dataAccessStaticHelper.isActiveUser())
                                    Toast.makeText(getApplicationContext(),"You are now visible",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getApplicationContext(),"You are Invisible",Toast.LENGTH_SHORT).show();
                            }

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
                Toast.makeText(getApplicationContext(),"Error updating status",Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("active_status","1");
                params.put("porter_id",dataAccessStaticHelper.getPid());
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.VOLLEY_TIMEOUT_MS,
                Constants.VOLLEY_RETRY_COUNT,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getsInstance().addToRequestQueue(strReq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_toggleservice){
            if (dataAccessStaticHelper.isActiveUser()){
                dataAccessStaticHelper.setActiveStatus("0");
                item.setIcon(R.drawable.toggle_off);
                updateUserActiveStatus();
            }else {
                dataAccessStaticHelper.setActiveStatus("1");
                item.setIcon(R.drawable.toggle_on);
                updateUserActiveStatus();
            }
        }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
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

    private void showConnectivityErrorDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
        builder1.setMessage("Connectivity Error:\nCheck internet connection");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkInternetConnectionAndMakeNetworkRequest();
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton(
                "Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
