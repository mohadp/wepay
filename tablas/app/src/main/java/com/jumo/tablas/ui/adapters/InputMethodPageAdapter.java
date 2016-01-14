package com.jumo.tablas.ui.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.HashMap;

/**
 * Created by Moha on 1/9/16.
 */
public class InputMethodPageAdapter extends FragmentStatePagerAdapter {

    public static final int FRAGMENT_PAID = 0;
    public static final int FRAGMENT_SHOULD_PAY = 1;
    public static final int FRAGMENT_CURRENCY = 2;
    public static final int FRAGMENT_CATEGORY = 3; //Todo: Now implemented up to currency; category is not yet ready.

    private HashMap<Integer, Fragment> mFragmentPages;

    public InputMethodPageAdapter(FragmentManager fm, HashMap<Integer, Fragment> pages){
        super(fm);
        mFragmentPages = pages;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentPages.get(position);
    }


    @Override
    public int getCount() {
        return mFragmentPages.size();
    }

    public HashMap<Integer, Fragment> getFragmentPages() {
        return mFragmentPages;
    }

    public void setFragmentPages(HashMap<Integer, Fragment> mFragmentPages) {
        this.mFragmentPages = mFragmentPages;
    }
}
