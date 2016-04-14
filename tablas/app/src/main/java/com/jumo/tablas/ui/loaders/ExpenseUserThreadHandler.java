package com.jumo.tablas.ui.loaders;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.ContactsContract;
import android.widget.ImageView;

import com.jumo.tablas.common.TablasManager;
import com.jumo.tablas.ui.util.BitmapCache;
import com.jumo.tablas.ui.util.BitmapLoader;
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

    private final static int MSG_TYPE_SHOULD_PAY_USERS = 0;
    private final static int MSG_TYPE_PAID_USERS = 1;
    /**
     * The object represents the object that will be filled with images (the payers) for an expense (the key).
     * The value for that key is the information needed to query the payers (the expense ID).
     *
     */
    Map<Object, String> requestMap = Collections.synchronizedMap(new HashMap<Object, String>());
    private final WeakReference<Context> mContextReference;

    //Handler that will process all the messages in the looper
    private Handler mHandler;

    //Handler that represents the UI thread
    private Handler mResponseHandler;


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

    public void queueExpensePayers(OnContactInfoLoaded request, String users){
        requestMap.put(request, users);
        //Create a new message with the ImageViewRow as object, once we have all the information to query its images with teh expense ID.
        mHandler.obtainMessage(MSG_TYPE_SHOULD_PAY_USERS, request).sendToTarget();
    }

    public void queueExpensePayers(OnBitmapsLoaded request, String users){
        requestMap.put(request, users);
        mHandler.obtainMessage(MSG_TYPE_PAID_USERS, request).sendToTarget();
    }


    public void clearQueue(){
        requestMap.clear();
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

            switch(msg.what){
                case MSG_TYPE_SHOULD_PAY_USERS:
                    handleShouldPayMessage(msg);
                    break;
                case MSG_TYPE_PAID_USERS:
                    handlePaidMessage(msg);
                    break;
            }
        }

        private void handleShouldPayMessage(Message msg){
            final OnContactInfoLoaded request = (OnContactInfoLoaded)msg.obj;
            final String users = requestMap.get(request);
            ArrayList<String> userIds = parseUserIds(users);
            final Cursor cursor = TablasManager.getInstance(mContextReference.get()).getContactsByUserId(userIds.toArray(new String[]{}));

            mResponseHandler.post(new Runnable(){
                public void run(){
                    if(requestMap.get(request) == null || requestMap.get(request).equals(users)) {
                        requestMap.remove(request);
                        request.onImagesLoaded(cursor);
                    }
                }
            });
        }

        private void handlePaidMessage(Message msg){
            final OnBitmapsLoaded request = (OnBitmapsLoaded)msg.obj;
            final String users = requestMap.get(request);
            ArrayList<String> userIds = parseUserIds(users);

            final Cursor cursor = TablasManager.getInstance(mContextReference.get()).getContactsByUserId(userIds.toArray(new String[]{}));

            final ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
            while(cursor != null && cursor.getCount() > 0 && cursor.moveToNext()) {
                String photoUriStr = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                BitmapLoader.ImageRetrieval retrieval = new BitmapLoader.ImageRetrieval(
                        BitmapLoader.ImageRetrieval.CONTENT_URI,
                        photoUriStr,
                        ContactsContract.CommonDataKinds.Photo.PHOTO);
                Bitmap bmp = BitmapLoader.decodeBitmapFromUri(mContextReference.get(), BitmapCache.getInstance(), retrieval);
                bitmaps.add(bmp);
            }
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }

            mResponseHandler.post(new Runnable(){
                public void run(){
                    if(requestMap.get(request) == null || requestMap.get(request).equals(users)) {
                        requestMap.remove(request);
                        request.onBitmapsLoaded(bitmaps);
                    }
                }
            });
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

    public static abstract class OnContactInfoLoaded {
        private ImageViewRow mImageViewRow;
        private long mRequestId;

        protected OnContactInfoLoaded(long requestId, ImageViewRow imgRow){
            mRequestId = requestId;
            mImageViewRow = imgRow;
        }

        public abstract void onImagesLoaded(Cursor cursor);

        public long getRequestId() {
            return mRequestId;
        }

        public void setRequestId(long mRequestId) {
            this.mRequestId = mRequestId;
        }

        public ImageViewRow getImageViewRow() {
            return mImageViewRow;
        }

        public void setImageViewRow(ImageViewRow mImageViewRow) {
            this.mImageViewRow = mImageViewRow;
        }
    }

    /**
     * Implement functionality to make sure that the results correspond to the initial request. Verify that the requestId in this
     * AsyncTask is for the caller/initiater of this task. E.g. if the request is to load a bunch of images for a view that uses
     * these bitmas, when loading the images, make sure that the view for which the images were loaded still need these images.
     */
    public static abstract class OnBitmapsLoaded {
        private ImageView mImageView;
        private long mRequestId;

        protected OnBitmapsLoaded(long expenseId, ImageView image){
            mRequestId = expenseId;
            mImageView = image;
        }
        public abstract void onBitmapsLoaded(ArrayList<Bitmap> bitmaps);

        public ImageView getImageView() {
            return mImageView;
        }

        public void setImageView(ImageView mImageView) {
            this.mImageView = mImageView;
        }

        public long getRequestId() {
            return mRequestId;
        }

        public void setRequestId(long mRequestId) {
            this.mRequestId = mRequestId;
        }
    }
}
