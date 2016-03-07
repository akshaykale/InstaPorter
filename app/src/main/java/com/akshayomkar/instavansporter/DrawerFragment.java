package com.akshayomkar.instavansporter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import java.io.File;


public class DrawerFragment extends android.support.v4.app.Fragment {

    public DataAccessStaticHelper mDataAccessStaticHelper;

    CircularImageView iv_profile;
    TextView tv_title;
    LinearLayout ll_feedback,ll_share,ll_logout,ll_locate,ll_exit;

    TextView tv_login_logout;
    ImageView iv_login_logout;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;



    public DrawerFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDataAccessStaticHelper = DataAccessStaticHelper.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drawer_layout, container, false);


        ll_feedback = (LinearLayout) view.findViewById(R.id.ll_feedback);
        ll_share = (LinearLayout) view.findViewById(R.id.ll_Share);

        ll_logout = (LinearLayout) view.findViewById(R.id.ll_logout);
        ll_locate = (LinearLayout) view.findViewById(R.id.ll_Locate);
        ll_exit = (LinearLayout) view.findViewById(R.id.ll_Exit);

        tv_title  = (TextView) view.findViewById(R.id.tv_maintext_drawer);

        iv_login_logout = (ImageView) view.findViewById(R.id.iv_login_logout);
        tv_login_logout = (TextView) view.findViewById(R.id.tv_login_logout);


        Log.d("aaa",DataAccessStaticHelper.getInstance().getCredit());
        tv_title.setText(DataAccessStaticHelper.getInstance().getUsername() + "\nCredits: " + DataAccessStaticHelper.getInstance().getCredit());



        /*ll_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intentPeople = new Intent(getActivity(),HelpActivity.class);
                //startActivity(intentPeople);
                mDrawerLayout.closeDrawers();
            }
        });*/


        ll_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentEmail = new Intent(Intent.ACTION_SEND);
                intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"vkapoor@caravancabs.com","akshay.kale@caravancabs.com"});
                intentEmail.putExtra(Intent.EXTRA_SUBJECT, "User Feedback");
                intentEmail.putExtra(Intent.EXTRA_TEXT, "Feedback:");
                intentEmail.setType("message/rfc822");

                startActivity(Intent.createChooser(intentEmail, "Choose an email provider :"));

                mDrawerLayout.closeDrawers();
            }
        });
        ///////
        ll_share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "CaravanCabs");
                    String sAux = "\nA one-stop portal to find all cabs nearest to you, from multiple service providers at the cheapest prices! Making travel faster, cheaper and convenient!\n\n";
                    sAux = sAux
                            + "https://play.google.com/store/apps/details?id=com.caravan.app";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Share via"));
                } catch (Exception e) { // e.toString();
                }
                mDrawerLayout.closeDrawers();
            }
        });
        ll_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("google.navigation:q="+Constants.sCurrentLocation.getLatitude()+","+Constants.sCurrentLocation.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                mDrawerLayout.closeDrawers();
            }
        });

        ll_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getActivity(), SearchActivity.class);
                //i.putExtra("exit", "exit");
                //startActivity(i);
                getActivity().finish();
                mDrawerLayout.closeDrawers();
                //System.exit(0);
            }
        });

        ll_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataAccessStaticHelper.getInstance().setLoginStatus("0");

            }
        });


        return view;
    }

    public void setUpDrawer(DrawerLayout drawerLayout, Toolbar toolbar) {

        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }


    public void clearApplicationData() {
        File cache = getActivity().getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));

                if (!success) {
                    Log.i("TAG", "*****");
                    return false;
                }
            }
        }

        return dir.delete();
    }

}
