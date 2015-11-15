package com.jumo.tablas.ui;

import android.app.Fragment;

/**
 * Created by Moha on 7/3/15.
 */
public class ExpenseActivity extends SingleFragmentActivity {

    private ExpenseFragment expenseFragment;

    protected Fragment createFragment(){
        String userName = getIntent().getStringExtra(ExpenseFragment.EXTRA_USER_ID);
        long groupId = getIntent().getLongExtra(ExpenseFragment.EXTRA_GROUP_ID, 0);

        return ExpenseFragment.newInstance(userName, groupId);
    }

    protected void setDefaultPreferences(){ return; }

}
