package com.jumo.tablas.ui.loaders;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Moha on 9/13/15; following/reusing the code and guidelines in http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
 */
public class BitmapTask extends AsyncTask<Object, Void, Bitmap> {
    private final static String TAG = "BitmapTask";
    private final static int IMG_FROM_RES_ID = 0;
    private final static int IMG_FROM_URI = 1;


    private final WeakReference<ImageView> mImageViewReference;
    private final WeakReference<Resources> mResReference;
    private final WeakReference<Context> mContextReference;
    private final WeakReference<LruCache<Object, Bitmap>> mCacheReference;
    private Object mData;


    public BitmapTask(ImageView imgThatNeedsBitmap, Context context, LruCache<Object, Bitmap> cache){
        mImageViewReference = new WeakReference<ImageView>(imgThatNeedsBitmap);
        mResReference = new WeakReference<Resources>(context.getResources());
        mCacheReference = new WeakReference<LruCache<Object, Bitmap>>(cache);
        mContextReference = new WeakReference<Context>(context);
    }

    //Decode image in background
    @Override
    protected Bitmap doInBackground(Object[] params){
        if(params.length == 1){ //to be backwards compatible with existent calls.
            return executeFromResource((Integer)params[0]);
        }

        //We verify how an image can be retrieved, and based on that, get necessary parameters.
        int type = (Integer)params[0];
        switch(type){
            case IMG_FROM_RES_ID:
                return executeFromResource((Integer)params[1]);
            case IMG_FROM_URI:
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

        Bitmap bitmap = decodeSampledBitmapFromResource(mResReference.get(), (Integer)mData, 100, 100, null);
        addToCache(mData, bitmap);
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
        byte[] imgData = null;

        if(mImageViewReference == null || context == null)
            return null;

        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(Uri.parse(uri), new String[]{column}, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            imgData = cursor.getBlob(cursor.getColumnIndex(column));
            cursor.close();
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);

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

    /**
     * Adds an image to the cache so that it can be reused wherever it used.
     * @param key
     * @param pic
     */
    private void addToCache(Object key, Bitmap pic){
        if(mCacheReference == null)
            return;

        LruCache<Object, Bitmap> cache = mCacheReference.get();
        if(cache == null)
            return;

        synchronized (cache) {
            if (cache.get(key) == pic) {
                return;
            } else {
                cache.put(key, pic);
            }
        }
    }

    /**
     * Method slightly modified from Android documentation: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     * When imgView is passed on, the required width and height are calculated from the ImageView drawing area (drawingRect).
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @param imgView
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight, ImageView imgView) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        if(imgView != null){
            Rect rect = new Rect();
            imgView.getDrawingRect(rect);
            reqWidth = rect.width();
            reqHeight = rect.height();
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Method from Android documentation: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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
