package com.jumo.tablas.ui.loaders;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.jumo.tablas.common.TablasManager;
import com.jumo.tablas.ui.views.ImageViewRow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Moha on 8/29/15. Queries the Contacts provider to get user photo URIs based on user ID (in this case,
 * normalized telephone numbers).
 */
public class ExpenseUserThreadHandler extends HandlerThread {
    private static final String TAG = "ExpenseUserThreadHandler";

    private final static int MSG_TYPE_EXPENSE_USERS = 0;
    /**
     * The object represents the object that will be filled with images (the payers) for an expense (the key).
     * The value for that key is the information needed to query the payers (the expense ID).
     *
     */
    Map<ImageViewRow, String> requestMap = Collections.synchronizedMap(new HashMap<ImageViewRow, String>());
    private final WeakReference<Context> mContextReference;

    //Handler that will process all the messages in the looper
    private Handler mHandler;

    //Handler that represents the UI thread
    private Handler mResponseHandler;

    //Listener which onContactInfoLoaded method will be called, to be done by the UI thread.
    private OnContactInfoLoaded onContactInfoLoaded;



    //Probably need to pass the cache to be able to reuse the cache; if not, just load in this same thread.
    //I want to load individual payers for all the expenses in one thread, instead of many; one is enough to load all.
    /*public ExpenseUserThreadHandler(){
        super(TAG);
        mCacheReference = null;
        mContextReference = null;
    }*/

    public ExpenseUserThreadHandler(Context context, Handler responseHandler){
        super(TAG);
        mContextReference = new WeakReference<Context>(context);
        mResponseHandler = responseHandler;

    }

    /**
     * Here, initializing handler
     */
    @Override
    protected void onLooperPrepared(){
        //Set the handler for this HandlerThread and looper
        mHandler = new UserImageGetter();
    }

    public void queueExpensePayers(ImageViewRow imgRow, String users){
        requestMap.put(imgRow, users);
        //Create a new message with the ImageViewRow as object, once we have all the information to query its images with teh expense ID.
        mHandler.obtainMessage(MSG_TYPE_EXPENSE_USERS, imgRow).sendToTarget();
    }

    public void clearQueue(){
        requestMap.clear();
    }

    public void setOnContactInfoLoaded(OnContactInfoLoaded onContactInfoLoaded) {
        this.onContactInfoLoaded = onContactInfoLoaded;
    }

    private class UserImageGetter extends Handler{

        /**
         * Process the message: get the set of users for the expense from ContentProvider.
         * Then for every user, get the user's image: verify if the image is in some cache. If not,
         * query the contacts provider for the image, add it to cache, and add it RoundImageViewRow
         *
         * @param msg message object
         */
        @Override
        public void handleMessage(Message msg){
            int messageId = msg.what;

            switch(msg.what){
                case MSG_TYPE_EXPENSE_USERS:
                    final ImageViewRow imageRow = (ImageViewRow)msg.obj;
                    final String users = requestMap.get(imageRow);
                    ArrayList<String> userIds = parseUserIds(users);
                    final Cursor cursor = TablasManager.getInstance(mContextReference.get()).getContactsByUserId(userIds.toArray(new String[]{}));

                    mResponseHandler.post(new Runnable(){
                        public void run(){
                            if(requestMap.get(imageRow) == null || requestMap.get(imageRow).equals(users)) {
                                requestMap.remove(imageRow);
                                onContactInfoLoaded.onImagesLoaded(cursor, imageRow);
                            }
                        }
                    });
                    return;
            }
        }

        private ArrayList<String> parseUserIds(String users){
            String regex = "[,;\\s]*([^,]*)[,;\\s]*";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(users);

            ArrayList<String> userIds = new ArrayList<String>();
            while(matcher.find()){
                String id = matcher.group(1);
                userIds.add(id);
            }
            return userIds;
        }
    }

    public interface OnContactInfoLoaded {
        public void onImagesLoaded(Cursor cursor, ImageViewRow imgRow);
    }
}
