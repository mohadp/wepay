package com.jumo.tablas.ui.loaders;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.LruCache;

import com.jumo.tablas.account.AccountService;
import com.jumo.tablas.provider.dao.Table;
import com.jumo.tablas.ui.views.ImageViewRow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Moha on 10/26/15.
 */
public class SearchContactThreadHandler extends HandlerThread {
    private static final String TAG = "SearchContactThdHdlr";
    private static final int WHAT_SEARCH_CONTACTS = 0;

    private final WeakReference<Context> mContextReference;
    private final WeakReference<LruCache<Object, Bitmap>> mCache;
    private Handler mHandler;
    private Handler mResponseHandler;
    private OnSearchCompleted mOnSearchCompleted;




    public SearchContactThreadHandler(Context context, LruCache<Object, Bitmap> cache, Handler responseHandler, OnSearchCompleted callback){
        super(TAG);
        mContextReference = new WeakReference<Context>(context);
        mCache = new WeakReference<LruCache<Object,Bitmap>>(cache);
        mResponseHandler = responseHandler;
        mOnSearchCompleted = callback;
    }

    /**
     * Here, initializing handler
     */
    @Override
    protected void onLooperPrepared(){
        //Set the handler for this HandlerThread and looper
        mHandler = new ContactSearcher();
    }

    public void searchContacts(String query){
        mHandler.obtainMessage(WHAT_SEARCH_CONTACTS, query).sendToTarget();
    }


    /**
     * Handler to search for contacts. We will query the
     * Contacts Provider to get the list of users
     */
    private class ContactSearcher extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_SEARCH_CONTACTS) {
                String displayName = (String)msg.obj;
                // retrieve DisplayName, Phone Label, Normalized Number, and Photo_thumbnail
                Uri uri = /*Uri.withAppendedPath(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.RawContacts.Entity.CONTENT_DIRECTORY);*/  ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                Context context = mContextReference.get();
                if(context == null)
                    return;

                String[] projection = new String[]{/*ContactsContract.RawContacts._ID */ ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
                        , /*ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY */ ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        , ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
                        , ContactsContract.CommonDataKinds.Phone.LABEL
                        , ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI};

                //TODO:  first, look for raw contacts with that name (which does not work now); then, get the same data as now.
                StringBuilder filter = new StringBuilder();
                filter.append("")
                        .append(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME).append(" like ? AND ")
                        .append(ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET).append(" = ?");

                String[] filterVals = new String[]{"%"+ displayName + "%", AccountService.ACCOUNT_TYPE};
                String sortBy = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " ASC";

                Cursor c = context.getContentResolver().query(uri, projection, filter.toString(), filterVals, sortBy);
                final Cursor cursor = new ContactsCursor(c);
                Log.d(TAG, cursor.toString());

                //Pass the cursor so it can be used in the adapter.
                mResponseHandler.post(new Runnable() {
                    public void run() {
                        mOnSearchCompleted.handleSearchResults(cursor);
                    }
                });
            }
        }
    }

    private Cursor getRawContactsWithName(String name){
        //Try to get all the data rows of a contact
        return null;
    }

    public interface OnSearchCompleted{
        public void handleSearchResults(Cursor cursor);
    }

    public class ContactsCursor extends CursorWrapper{
        public ContactsCursor(Cursor cursor){
            super(cursor);
        }

        @Override
        public int getColumnIndex(String name){
            if(name.equals(ContactsContract.CommonDataKinds.Phone._ID)){
                return this.getWrappedCursor().getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID);
            }else{
                return getWrappedCursor().getColumnIndex(name);
            }
        }

        @Override
        public int getColumnIndexOrThrow(String name) throws IllegalArgumentException{
            if(name.equals(ContactsContract.CommonDataKinds.Phone._ID)){
                return getWrappedCursor().getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID);
            }else{
                return getWrappedCursor().getColumnIndexOrThrow(name);
            }
        }

        public String toString(){
            int pos = this.getPosition();
            StringBuilder sb =  new StringBuilder("Contact Search Results:").append("\n");

            moveToFirst();
            while(!isAfterLast()){
                sb.append("\t");
                for(int i = 0; i < getColumnCount(); i++){
                    sb.append(getColumnName(i)).append(": ").append(getString(i));
                    if(i < getColumnCount() - 1)
                        sb.append(", ");
                }
                sb.append("\n");
                moveToNext();
            }

            this.moveToPosition(pos);
            return sb.toString();
        }


    }


}
