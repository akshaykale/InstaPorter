package com.akshayomkar.instavansporter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Akshay on 3/5/2016.
 */
public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private ProgressDialog mProgressDialog;

    EditText et_name,et_pass,et_conf_pass,et_phone;
    LinearLayout ll_camera,ll_gallery;


    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        init();

        ll_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        ll_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

    }

    private void init() {

        et_name = (EditText) findViewById(R.id.et_signup_username_input);
        et_phone = (EditText) findViewById(R.id.et_signup_phone_input);
        et_pass = (EditText) findViewById(R.id.et_signup_password_input);
        et_conf_pass = (EditText) findViewById(R.id.et_signup__conferm_pass_input);

        ll_camera = (LinearLayout) findViewById(R.id.ll_signup_camera);
        ll_gallery = (LinearLayout) findViewById(R.id.ll_signup_gallery);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //ll_camera.setImageBitmap(imageBitmap);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signup) {
            Log.d(TAG,"signup click");
            String name = "",pass = "",conf_pass = "",phone = "";
            name = et_name.getText().toString();
            pass = et_pass.getText().toString();
            conf_pass = et_conf_pass.getText().toString();
            phone = et_phone.getText().toString();

            if(validate(name,phone,pass,conf_pass)){
                Log.d(TAG,"signup valid");
                signUpBtClicked(name,phone,pass,"");
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validate(String name, String phone, String pass, String conf_pass) {
        if(name.equals("") || phone.equals("") || pass.equals("") || conf_pass.equals(""))
            return false;
        else if (!pass.equals(conf_pass))
            return false;
        return true;
    }





    private void signUpBtClicked(final String name, final String phone, final String pass, final String img) {
        showProgressDialog();
        Log.d(TAG,"signup click");
        StringRequest strReq = new StringRequest( Request.Method.POST, Constants.API_URL_SIGNUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        processSignUpResponse(response);
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
                params.put("pname",name);
                params.put("gcmid",DataAccessStaticHelper.getInstance().getGCMID());
                params.put("pwd",pass);
                params.put("phone_no",phone);
                params.put("validid",img);
                Log.d(TAG,"len "+DataAccessStaticHelper.getInstance().getGCMID());
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.VOLLEY_TIMEOUT_MS,
                Constants.VOLLEY_RETRY_COUNT,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getsInstance().addToRequestQueue(strReq);
    }

    private void processSignUpResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("retcode").equals("success")){

                String pid = jsonObject.getString("pid");
                DataAccessStaticHelper.getInstance().setPid(pid);

                Log.d(TAG, "pid " + pid);

                hideProgressDialog();

                DataAccessStaticHelper.getInstance().setLoginStatus("1");

                startAlarmManagerService();

                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }else {
                hideProgressDialog();
                Toast.makeText(this,"SignUp failed",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startAlarmManagerService() {

        Calendar c= Calendar.getInstance();
        Long t2=c.getTimeInMillis();
        Long t3=20000L;// time after which the alarm gets called
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(this, AlarmReciever.class);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,t2,t3, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

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
