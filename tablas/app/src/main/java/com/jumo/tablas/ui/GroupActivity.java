package com.jumo.tablas.ui;

import android.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jumo.tablas.common.SyncUtil;
import com.jumo.tablas.common.TablasManager;
import com.jumo.tablas.common.TablasPrefs;

/**
 * Created by Moha on 7/3/15.
 */
public class GroupActivity extends SingleFragmentActivity {
    private static final String TAG = "GroupActivity";

    protected Fragment createFragment(){
        String accountName = PreferenceManager.getDefaultSharedPreferences(this).getString(TablasPrefs.ACCOUNT_NAME, null);
        //Log.d(TAG, "Creating GroupFragment with ID: " + accountName);
        return GroupFragment.newInstance(accountName);
    }

    protected void setDefaultPreferences(){
        SyncUtil.createSyncAccount(this, TablasManager.CURRENT_USER);
    }
}
