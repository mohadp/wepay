package com.jumo.tablas.ui;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jumo.tablas.common.TablasPrefs;

/**
 * Created by Moha on 10/27/15.
 */
public class CreateGroupActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        String userId = PreferenceManager.getDefaultSharedPreferences(this).getString(TablasPrefs.ACCOUNT_NAME, "");
        return CreateGroupFragment.newInstance(userId);
    }

    @Override
    protected void setDefaultPreferences() { }
}
