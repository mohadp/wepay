package com.jumo.wepay.view;

import android.app.Fragment;

/**
 * Created by Moha on 7/3/15.
 */
public class GroupActivity extends SingleFragmentActivity {


    protected Fragment createFragment(){
        return GroupFragment.newInstance("mohadp@gmail.com");
    }
}
