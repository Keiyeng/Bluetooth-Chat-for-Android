package com.example.utar.bluetoothchat;

/**
 * Created by Yumiko on 6/17/2016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabConnectionFragment tab1 = new TabConnectionFragment();
                return tab1;
            case 1:
                TabChatFragment tab2 = new TabChatFragment();
                return tab2;
            case 2:
                TabDistanceFragment tab3 = new TabDistanceFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}