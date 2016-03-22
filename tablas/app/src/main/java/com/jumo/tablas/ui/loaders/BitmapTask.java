package com.jumo.tablas.ui.loaders;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.jumo.tablas.ui.util.BitmapLoader;
import com.jumo.tablas.ui.util.CacheManager;

import java.lang.ref.WeakReference;

/**
 * Created by Moha on 9/13/15; following/reusing the code and guidelines in http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
 */
public class BitmapTask extends AsyncTask<Object, Void, Bitmap> {
    private final static String TAG = "BitmapTask";
    public final static int LOAD_FROM_RES_ID = 0;
    public final static int LOAD_FROM_CONTENT_URI = 1;


    private final WeakReference<ImageView> mImageViewReference;
    private final WeakReference<Resources> mResReference;
    private final WeakReference<Context> mContextReference;
    private final WeakReference<CacheManager> mCacheContainerReference;
    private Object mData;


    public BitmapTask(ImageView imgThatNeedsBitmap, Context context, CacheManager cacheManager){
        mImageViewReference = new WeakReference<ImageView>(imgThatNeedsBitmap);
        mResReference = new WeakReference<Resources>(context.getResources());
        mCacheContainerReference = new WeakReference<CacheManager>(cacheManager);
        mContextReference = new WeakReference<Context>(context);
    }

    /**
     * Decode image in background. The params argument determines the following:
     * - Type of retreival: LOAD_FROM_RES_ID or LOAD_FROM_CONTENT_URI; first one is loading from a resource ID or from an URI.
     * - Other parameters:
     *      + if type is from ResourceId, second paramter determines the ID of the resource.
     *      + If type is from URI, then second parameter determines the URI of the picture, and the second parameter determines the column name where the picture is returned.
     * @param params determines how to decode the image.
     * @return
     */

    @Override
    protected Bitmap doInBackground(Object[] params){
        if(params.length == 1){ //to be backwards compatible with existent calls.
            return executeFromResource((Integer)params[0]);
        }

        //We verify how an image can be retrieved, and based on that, get necessary parameters.
        int type = (Integer)params[0];
        switch(type){
            case LOAD_FROM_RES_ID:
                return executeFromResource((Integer)params[1]);
            case LOAD_FROM_CONTENT_URI:
                return executeFromResource((String)params[1], (String)params[2]);
        }
        return null;
    }

    /**
     * Helper function to decode an image from a resource file.
     * @param resId
     * @return
     */
    private Bitmap executeFromResource(int resId){
        mData = resId; //This sets the resource ID as an "identifier of the task", to know whether this task is loading this resource ID, useful to not run this again while it is running.

        if(mImageViewReference == null)
            return null;

        Bitmap bitmap = BitmapLoader.decodeSampledBitmapFromResource(mResReference.get(), (Integer) mData, 100, 100, null);

        if(mCacheContainerReference != null && mCacheContainerReference.get() != null){
            mCacheContainerReference.get().addToCache(mData, bitmap);
        };
        return bitmap;
    }

    /**
     * Helper function to load a bitmap from a content provider URI
     * @param uri
     * @return
     */
    private Bitmap executeFromResource(String uri, String column){
        mData = uri; //This sets the uri as an "identifier of the task", to know whether this task is loading this resource ID, useful to not run this again while it is running.
        Context context = mContextReference.get();

        if(mImageViewReference == null || context == null)
            return null;

        Bitmap bitmap = BitmapLoader.decodeBitmapFromUri(context, uri, column);

        if(mCacheContainerReference != null && mCacheContainerReference.get() != null){
            mCacheContainerReference.get().addToCache(mData, bitmap);
        };

        return bitmap;
    }



    @Override
    protected void onPostExecute(Bitmap bitmap){
        if(isCancelled()){
            bitmap = null;
        }

        if (mImageViewReference != null && bitmap != null) {
            //Log.d(TAG, "Worker's bitmap is recycled: " + bitmap.isRecycled());
            final ImageView imageView = mImageViewReference.get();
            final BitmapTask bitmapTask = getBitmapTask(imageView);
            if (this == bitmapTask && imageView != null) { //Verify that this ImageView's BitmapDrawable is still linked to this task
                imageView.setImageBitmap(bitmap);
                imageView.setTag(mData.toString());
            }
        }
    }

    public static boolean cancelPotentialWork(Object data, ImageView imageView) {
        final BitmapTask bitmapWorkerTask = getBitmapTask(imageView);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.mData;
            // If bitmapData is not yet set or it differs from the new mData
            if (bitmapData == null || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    public static BitmapTask getBitmapTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DrawableAsync) {
                final DrawableAsync drawableAsync = (DrawableAsync) drawable;
                return drawableAsync.getBitmapTask();
            }
        }
        return null;
    }

}
