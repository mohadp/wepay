package com.jumo.tablas.ui.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.LruCache;

import com.jumo.tablas.ui.views.ImageViewRow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Moha on 10/26/15.
 */
public class SearchContactThreadHandler extends HandlerThread {
    private static final String TAG = "SearchContactThreadHandler";
    private static final int WHAT_SEARCH_CONTACTS = 0;

    private final WeakReference<Context> contextReference;
    private Handler mHandler;
    private Handler mResponseHandler;
    private OnSearchCompleted onSearchCompleted;
    private LruCache<String, Bitmap> mCache;



    public SearchContactThreadHandler(Context context, Handler responseHandler, OnSearchCompleted callback){
        super(TAG);
        contextReference = new WeakReference<Context>(context);
        mResponseHandler = responseHandler;
        onSearchCompleted = callback;
    }

    /**
     * Here, initializing handler
     */
    @Override
    protected void onLooperPrepared(){
        //start a cache for the contact images
        initializeImageCache();
        //Set the handler for this HandlerThread and looper
        mHandler = new ContactSearcher();
    }

    public void searchContacts(String query){
        mHandler.obtainMessage(WHAT_SEARCH_CONTACTS, query).sendToTarget();
    }

    /**
     * Initialize the image cache for the contacts being searched
     */
    private void initializeImageCache(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;  // Use 1/8th of the available memory for this memory cache.
        mCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024; // The cache size will be measured in kilobytes
            }
        };
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
                // retrieve DisplayName, Phone Type, Normalized Number, and Photo_thumbnail

                //Pass the cursor so it can be used in the adapter.
                mResponseHandler.post(new Runnable() {
                    public void run() {

                    }
                });
                return;
            }
        }
    }

    public interface OnSearchCompleted{
        public void handleSearchResults();
    }


}
