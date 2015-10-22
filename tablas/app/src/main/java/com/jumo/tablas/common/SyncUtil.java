package com.jumo.tablas.common;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import com.jumo.tablas.account.AccountService;

/**
 * Created by Moha on 10/20/15.
 */
public class SyncUtil {

    public static void createSyncAccount(Context context, String accountName){
        boolean newAccount = false;

        // This creates an account for the provided account, of type AccountService.ACCOUNT_TYPE (com.jumo)
        Account account = AccountService.getAccount(accountName);
        AccountManager accountManager =   (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        if (accountManager.addAccountExplicitly(account, null, null)) {
            ContentResolver.setIsSyncable(account, ContactsContract.AUTHORITY, 1);  // Inform the system that this account supports sync
            ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);  // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.addPeriodicSync(account, ContactsContract.AUTHORITY, new Bundle(), TablasPrefs.SYNC_FREQ_SEC_DEFAULT); // Recommend a schedule for automatic synchronization. The system may modify this based on other scheduled syncs and network utilization.
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(TablasPrefs.ACCOUNT_NAME, accountName).commit();
            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount) {
            TriggerRefresh(accountName);
        }
    }

    public static void TriggerRefresh(String accountName) {
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(AccountService.getAccount(accountName), ContactsContract.AUTHORITY, b);                                             // Extras
    }
}
