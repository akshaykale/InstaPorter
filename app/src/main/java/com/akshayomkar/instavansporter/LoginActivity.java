package com.akshayomkar.instavansporter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Akshay on 3/5/2016.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private DataAccessStaticHelper dataAccessStaticHelper;

    EditText et_email,et_password;
    TextView tv_signup,tv_signin;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpBtClicked();
            }
        });

        tv_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInBtClicked();
            }
        });

    }

    private void signUpBtClicked() {
        Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void signInBtClicked() {
        showProgressDialog();
        final String username = et_email.getText().toString();
        final String password = et_password.getText().toString();

        StringRequest strReq = new StringRequest( Request.Method.POST, Constants.API_URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        processLoginResponse(response);
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
                params.put("porter_id",username);
                params.put("pwd",password);
                params.put("login",""+1);

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                Constants.VOLLEY_TIMEOUT_MS,
                Constants.VOLLEY_RETRY_COUNT,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getsInstance().addToRequestQueue(strReq);
    }

    private void processLoginResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("retcode").equals("SUCCESS")){
                dataAccessStaticHelper.setLoginStatus("1");

                startAlarmManagerService();

                hideProgressDialog();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }else {
                hideProgressDialog();
                Toast.makeText(this,"Login failed",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startAlarmManagerService() {

        Calendar c= Calendar.getInstance();
        Long t2=c.getTimeInMillis();
        Long t3=120000L;// time after which the alarm gets called
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(this, AlarmReciever.class);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,t2,t3, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

    }

    private void init() {

        dataAccessStaticHelper = DataAccessStaticHelper.getInstance();

        et_email = (EditText) findViewById(R.id.et_login_username_input);
        et_password = (EditText) findViewById(R.id.et_login_password_input);

        et_email.setText(dataAccessStaticHelper.getPid());

        tv_signin = (TextView) findViewById(R.id.tv_ip_signin);
        tv_signup = (TextView) findViewById(R.id.tv_ip_signup);

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
                        signInBtClicked();
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
