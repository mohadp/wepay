package com.jumo.tablas.ui.loaders;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.LruCache;

import com.jumo.tablas.R;
import com.jumo.tablas.common.TablasManager;
import com.jumo.tablas.model.Member;
import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.EntityCursor;
import com.jumo.tablas.ui.util.BitmapCache;
import com.jumo.tablas.ui.util.CacheManager;
import com.jumo.tablas.ui.views.ImageViewRow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Moha on 8/29/15.
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

    //Listener which onImagesLoaded method will be called, to be done by the UI thread.
    private OnImagesLoaded onImagesLoaded;



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

    public void queueExpensePayers(ImageViewRow imageRow, long expenseId){
        requestMap.put(imageRow, String.valueOf(expenseId));
        //Create a new message with the ImageViewRow as object, once we have all the information to query its images with teh expense ID.
        mHandler.obtainMessage(MSG_TYPE_EXPENSE_USERS, imageRow).sendToTarget();
    }

    public void clearQueue(){
        requestMap.clear();
    }

    public void setOnImagesLoaded(OnImagesLoaded onImagesLoaded) {
        this.onImagesLoaded = onImagesLoaded;
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
                    final String expenseId = requestMap.get(imageRow);
                    final ArrayList<Bitmap> images = new ArrayList<Bitmap>();
                    final ArrayList<String> imageIds = new ArrayList<String>();
                    getExpenseUserImages(expenseId, imageIds, images); //this adds ids and bitmaps to the ArrayLists

                    mResponseHandler.post(new Runnable(){
                        public void run(){
                            if(requestMap.get(imageRow) == null || requestMap.get(imageRow).equals(expenseId)) {
                                requestMap.remove(imageRow);
                                onImagesLoaded.onImagesLoaded(imageRow, imageIds, images);
                            }
                        }
                    });
                    return;
            }
        }

        private void getExpenseUserImages(String expenseId, ArrayList<String> imageIds, ArrayList<Bitmap> images){
            Cursor cursor = TablasManager.getInstance(mContextReference.get()).getPayingMembersForExpense(Long.valueOf(expenseId));
            EntityCursor entityCursor = new EntityCursor(cursor);

            if(entityCursor == null)
                return;

            int counter = 0;
            while(entityCursor.moveToNext()){
                Member user = new Member(entityCursor.getEntity(TablasContract.Member.getInstance()));
                //TODO: Need to update the image to load dynamically based on user
                Bitmap bitmap = getUserBitmap(R.drawable.moha);
                images.add(bitmap);
                imageIds.add(String.valueOf(R.drawable.moha));
            }
            cursor.close();
        }

        private Bitmap getUserBitmap(int resId){
            //first check in the cahce; if not, retrieve from wherever
            CacheManager<Object, Bitmap> cacheManager = BitmapCache.getInstance();
            Bitmap bitmap = cacheManager.retrieveFromCache(resId);
            Resources resources = mContextReference.get().getResources();

            if(bitmap == null){
                bitmap = BitmapTask.decodeSampledBitmapFromResource(resources, resId, 100, 100, null);
                cacheManager.addToCache(resId, bitmap);
            }
            return bitmap;
        }
    }

    public interface OnImagesLoaded{
        public void onImagesLoaded(ImageViewRow imgRow, ArrayList<String> bitmapIds, ArrayList<Bitmap> images);
    }
}
