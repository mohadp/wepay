package com.jumo.tablas.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Moha on 10/18/15.
 */
public class ContactSyncService extends Service {

    private static final String TAG = "ContactSyncService";

    private static final Object syncAdapterLock = new Object();
    private static ContactSyncAdapter syncAdapter = null;


    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(TAG, "ContactSyncService Created");
        synchronized(syncAdapterLock){
            if(syncAdapter == null){
                syncAdapter = new ContactSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ContactSyncService destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
