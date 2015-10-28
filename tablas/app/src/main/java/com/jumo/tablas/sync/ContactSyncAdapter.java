package com.jumo.tablas.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import com.jumo.tablas.account.AccountService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


/**
 * Created by Moha on 10/10/15.
 */
public class ContactSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "ContactSyncAdapter";
    private ContentResolver mResolver;
    private static HashSet<String> existentServiceAccounts = new HashSet<String>();
    static{
        existentServiceAccounts.add("+17036566202");
        existentServiceAccounts.add("+17037173160");
        existentServiceAccounts.add("+17036566203");
        existentServiceAccounts.add("+19192188457");
        existentServiceAccounts.add("+5215534007789");
        existentServiceAccounts.add("+5215540840084");
        existentServiceAccounts.add("+5215513725485");
        existentServiceAccounts.add("+12026790071");
        existentServiceAccounts.add("+16504557014");
    }

    public ContactSyncAdapter(Context context, boolean autoInitialize){
        super(context, autoInitialize);
        mResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //First synchronize contacts; we identify existent contacts in phone. If each is in the cloud, create a new one for
        //this account type

        //deleteContacts(AccountService.ACCOUNT_TYPE);

        //This is to recognize newly added contacts.
        //Get all the local phone numbers that have corresponding tablas account and
        //Keep only the set of phones that have a corresponding accounts online
        HashMap<String, String> contactsToCreate = getContactsWithExistentAccountOnline(getPhoneNumbersWithoutAccount());
        //Create raw contacts for phones that "exist online"
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        Iterator<String> itPhone = contactsToCreate.keySet().iterator();
        //int indexOfLastRawContactInsert = 0;
        while(itPhone.hasNext()){
            String phone = itPhone.next();
            //Add 3 rows (three operations) to the Contacts provider: a new raw contact (account name is the phone),
            //plus two data rows (one for Phone number, and one for Display Name)
            addRawContact(ops, phone, contactsToCreate.get(phone));
        }

        try {
            mResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        }catch(OperationApplicationException e){

            Log.d(TAG, "Sync Failed: ApplyBatch of operations failed: ", e);
        }catch(RemoteException e){
            Log.d(TAG, "Sync Failed: ApplyBatch of operations failed", e);
        }

        //TODO: Then we need to recognize deleted contacts (probably based on sync columns in contact provider)

        //TODO: We also need to identify which contacts have been modified (also probably using the sync columns)
    }

    private void deleteContacts(String accountType){
        String filter = ContactsContract.RawContacts.ACCOUNT_TYPE + " = ?";
        String[] filterVals = new String[]{ accountType };

        mResolver.delete(ContactsContract.RawContacts.CONTENT_URI,filter, filterVals);
    }

    private void addRawContact(ArrayList<ContentProviderOperation> ops, String phone, String displayName){
        // Before adding new operations, the size of the array represents also the position where
        // the next insert operation will be, the position of the operation where the next raw contact insertion
        // is done (first operation below)
        int indexOfLastRawContactInsert = ops.size();

        ContentProviderOperation.Builder opNewRawContact = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, AccountService.ACCOUNT_TYPE)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, phone);
        ops.add(opNewRawContact.build());

        ContentProviderOperation.Builder opPhoneData = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, indexOfLastRawContactInsert)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
        ops.add(opPhoneData.build());

        ContentProviderOperation.Builder opNameData = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, indexOfLastRawContactInsert)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName);
        opNameData.withYieldAllowed(true);
        ops.add(opNameData.build());
    }


    private HashMap<String, String> getPhoneNumbersWithoutAccount(){
        //Get all the contacts that have a normalized phone number. For the phone numbers present in the Tablas service,
        //I keep the corresponding contact information: contact's contact_id, name, phone number.
        HashMap<String, String> numberContacts = new HashMap<String, String>();

        ////////// GET ALL THE PHONE NUMBERS THAT HAVE NO CORRESPONDING RAW CONTACT/////////////
        Uri contactPhonesUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[]{
                //ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                //ContactsContract.CommonDataKinds.Phone.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                //ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET
        };

        String filter = ContactsContract.Contacts.Entity.MIMETYPE + " = ?  AND " + ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER + " IS NOT NULL"; //" AND " + ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET + " <> ?";
        String[] filterVals = new String[]{ ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}; // , AccountService.ACCOUNT_TYPE};
        String sortBy = ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER + " ASC"; //sorted by number
        Cursor allPhonesCursor = mResolver.query(contactPhonesUri, projection, filter, filterVals, sortBy);


        if(allPhonesCursor != null && allPhonesCursor.getCount() > 0) {
            String previousPhone = null;            //iterate over numbers; several rows will have the same number. If any row is part of a Tablas contact, then remove the phone number
            boolean hasTablasAccount = false;       //by default, add phone number

            while (allPhonesCursor.moveToNext()) {
                //verify if the contact already exists; if not, create
                String phone = allPhonesCursor.getString(allPhonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                String name = allPhonesCursor.getString(allPhonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String accType = allPhonesCursor.getString(allPhonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET));

                if(previousPhone == null  || !previousPhone.equals(phone)){
                    previousPhone = phone;
                    hasTablasAccount = false;
                }

                if(accType.equals(AccountService.ACCOUNT_TYPE)){ //hasTablasAccount stays true once encountered until we start processing a different phone
                    hasTablasAccount = true;
                }

                if(hasTablasAccount){
                    numberContacts.remove(phone);  //remove will be called several times; we rely on HashMap to not doing anything when trying to remove an already removed entry
                }else{
                    numberContacts.put(phone, name);
                }
            }
            allPhonesCursor.close();
        }
        return numberContacts;
    }

    /**
     * This is what I would send online; this would give me the list of users I still
     * need to create: the set of users from who I have a phone number that have an online account
     * @param phoneNumbers
     * @return
     */
    private HashMap<String, String> getContactsWithExistentAccountOnline(HashMap<String, String> phoneNumbers){
        HashMap<String, String> availableOnline = new HashMap<String, String>();

        Iterator<String> it = phoneNumbers.keySet().iterator();
        while(it.hasNext()){
            String phone = it.next();
            if(existentServiceAccounts.contains(phone)){
                availableOnline.put(phone, phoneNumbers.get(phone));
            }
        }
        return availableOnline;
    }

    private String getDisplayName(int rawContactId){
        //Uri uri = ContactsContract.Contacts.Entity
        return null;
    }

    private ContentProviderOperation newRawContact(){
        return null;
    }


    private class ContactDetails {
        public String name;
        public String accountType;

        public ContactDetails(String n, String accType){
            name = n;
            accountType = accType;
        }
    }

}
