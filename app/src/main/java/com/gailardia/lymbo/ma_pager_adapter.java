package com.gailardia.lymbo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

/**
 * Created by Dr.h3cker on 14/03/2015.
 */
public class ma_pager_adapter extends FragmentPagerAdapter {
    public ma_pager_adapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                riderReport t1 = new riderReport();
                return t1;
            case 1:
                driverReport t2 = new driverReport();
                return t2;
            case 2:
                allReport t3 = new allReport();
                return t3;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }//set the number of tabs

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return "Ride Report";
            case 1:
                return "Driver Report";
            case 2:
                return "all Report";
        }
        return null;
    }



}
