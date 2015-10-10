package com.jumo.tablas.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Created by Moha on 10/10/15.
 */
public class TablasContactSyncAdapter extends AbstractThreadedSyncAdapter {


    //TODO: Need to create a Service for the synch adapter that returns this synch adapters @Binder.

    public TablasContactSyncAdapter(Context context, boolean autoInitialize){
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //First synchronize contacts; we identify existent contacts in phone. If each is in the cloud, create a new one for
        //this account type
    }
}
