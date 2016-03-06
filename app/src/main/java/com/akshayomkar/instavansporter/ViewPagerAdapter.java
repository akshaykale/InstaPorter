package com.akshayomkar.instavansporter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Akshay on 1/9/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    String[] tabs = {/*"Community",*/ "Jobs Available", "Jobs Completed"};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        //if(position==0){
        //    return FragmentCommunity.newInstance();
        //}
        if(position==0){
            return new FragmentJobAvailable();//FragmentDiscover.newInstance();
        }if(position==1){
            return FragmentJobCompleted.newInstance();
        }



        return fragment;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }
}
