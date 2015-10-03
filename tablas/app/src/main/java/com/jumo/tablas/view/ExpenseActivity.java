package com.jumo.tablas.view;

import android.app.Fragment;
import android.view.KeyEvent;

/**
 * Created by Moha on 7/3/15.
 */
public class ExpenseActivity extends SingleFragmentActivity {

    private ExpenseFragment expenseFragment;

    protected Fragment createFragment(){
        String userName = getIntent().getStringExtra(ExpenseFragment.EXTRA_USER_ID);
        long groupId = getIntent().getLongExtra(ExpenseFragment.EXTRA_GROUP_ID, 0);

        //TODO: Verify that userName and groupId are not null
        return ExpenseFragment.newInstance(userName, groupId);
    }

}
