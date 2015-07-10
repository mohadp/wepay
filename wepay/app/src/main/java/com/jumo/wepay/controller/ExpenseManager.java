package com.jumo.wepay.controller;

import android.content.Context;
import android.net.Uri;

import com.jumo.wepay.provider.Cursors;
import com.jumo.wepay.provider.WepayContract;

/**
 * Created by Moha on 7/10/15.
 */
public class ExpenseManager {

    private Context mContext;

    public ExpenseManager(Context context){
        mContext = context;

        Uri uri = new Uri.Builder().scheme(WepayContract.SCHEME).authority(WepayContract.AUTHORITY)
                .appendPath(WepayContract.Group.TABLE_NAME).build();

        Cursors.Group groupCursor = (Cursors.Group) mContext.getContentResolver().query(uri, null, null, null, null);
        if(groupCursor.isBeforeFirst() || groupCursor.isAfterLast()){
            createSampleData();
        }



    }

    public void createSampleData(){
        //
    }

}
