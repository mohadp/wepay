package com.jumo.tablas.ui.loaders;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Moha on 9/13/15; following/reusing the code and guidelines in http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
 */
public class BitmapTask extends AsyncTask<Integer, Void, Bitmap> {
    private final static String TAG = "BitmapTask";
    private final WeakReference<ImageView> mImageViewReference;
    private final WeakReference<Resources> mResReference;
    private final WeakReference<LruCache<String, Bitmap>> mCacheReference;
    private int mData;

    public BitmapTask(ImageView imgThatNeedsBitmap, Resources res){
        //Weak reference so that imageView can be garbage-collected.
        mImageViewReference = new WeakReference<ImageView>(imgThatNeedsBitmap);
        mResReference = new WeakReference<Resources>(res);
        mCacheReference = null;
    }

    public BitmapTask(ImageView imgThatNeedsBitmap, Resources res, LruCache<String, Bitmap> cache){
        mImageViewReference = new WeakReference<ImageView>(imgThatNeedsBitmap);
        mResReference = new WeakReference<Resources>(res);
        mCacheReference = new WeakReference<LruCache<String, Bitmap>>(cache);
    }

    //Decode image in background
    @Override
    protected Bitmap doInBackground(Integer[] params){
        mData = params[0]; //This sets the resource ID as an "identifier of the task", to know whether this task is loading this resource ID, useful to not run this again while it is running.

        if(mImageViewReference == null)
            return null;

        Bitmap bitmap = decodeSampledBitmapFromResource(mResReference.get(), mData, 100, 100, null);
        addToCache(String.valueOf(mData), bitmap);
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
                imageView.setTag(String.valueOf(mData));
                //imageView.invalidate();
            }
        }
    }

    private void addToCache(String key, Bitmap pic){
        if(mCacheReference == null)
            return;

        LruCache<String, Bitmap> cache = mCacheReference.get();
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

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapTask bitmapWorkerTask = getBitmapTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.mData;
            // If bitmapData is not yet set or it differs from the new mData
            if (bitmapData == 0 || bitmapData != data) {
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
