package com.jumo.tablas.ui;

import android.app.Fragment;
import android.preference.PreferenceManager;

import com.jumo.tablas.common.SyncUtil;
import com.jumo.tablas.common.TablasPrefs;

/**
 * Created by Moha on 7/3/15.
 */
public class GroupActivity extends SingleFragmentActivity {


    protected Fragment createFragment(){
        String accountName = PreferenceManager.getDefaultSharedPreferences(this).getString(TablasPrefs.ACCOUNT_NAME, null);
        return GroupFragment.newInstance(accountName);
    }

    protected void setDefaultPreferences(){
        SyncUtil.createSyncAccount(this, "+17036566202");
    }
}
