package com.jumo.tablas.ui.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.ImageView;

import com.jumo.tablas.ui.loaders.BitmapTask;
import com.jumo.tablas.ui.loaders.DrawableAsync;

import java.util.ArrayList;

/**
 * Created by Moha on 11/4/15.
 */
public  class BitmapLoader {

    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 100;


    public static void asyncSetBitmapInImageView(ImageRetrieval resource, ImageView imageView, Context context, CacheManager<Object, Bitmap> cacheManager){
        if(!resource.isValid()){
            return;
        }
        //Try to avoid reloading a bitmap if the ImageView already has its a valid bitmap
        String imageViewTag = (String)imageView.getTag();
        BitmapDrawable currBitmapDrawable = (BitmapDrawable)imageView.getDrawable();

        if(imageViewTag != null && imageViewTag.equals(resource.id.toString()) && currBitmapDrawable != null) { //Todo: use the setTag(key, value) for this case, so the general setTag(Object) can be used for other applicaiton-specific logic.
            return; // in this case, do not reload the bitmap because the ImageView already has the correct bitmap.
        }

        Bitmap bitmap = cacheManager.retrieveFromCache(resource.id);

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
            imageView.setTag(resource.id.toString());
        }else{
            loadBitmap(resource, imageView, null, context, cacheManager);
        }
    }

    private static void loadBitmap(ImageRetrieval resource, ImageView imageView, Bitmap placeHolder, Context context, CacheManager cacheManager) {
        if (BitmapTask.cancelPotentialWork(resource.id, imageView)) {
            //Log.d(TAG, "loadBitmap: cancelPotentialWork is true");
            if(context == null)
                return;

            final BitmapTask task = new BitmapTask(imageView, context, cacheManager);
            final DrawableAsync drawableAsync = new DrawableAsync(context.getResources(), placeHolder, task);
            imageView.setImageDrawable(drawableAsync);
            executeTask(task, resource);
        }
    }


    private static void executeTask(BitmapTask task, ImageRetrieval resource){
        switch(resource.how){
            case ImageRetrieval.RES_ID:
                task.execute(BitmapTask.LOAD_FROM_RES_ID, resource.id);
                return;
            case ImageRetrieval.CONTENT_URI:
                if(resource.params.length < 1) //we expect the ImageRetrieval object to have an additional parameter, the column to retrieve.
                    return;
                task.execute(BitmapTask.LOAD_FROM_CONTENT_URI, resource.id, resource.params[0]);
                return;
        }
    }


    public static ArrayList<Bitmap> decodeBitmaps(Context context, BitmapCache cache, ImageRetrieval[] retrievals){
        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();

        //We verify how an image can be retrieved, and based on that, get necessary parameters.
        for(ImageRetrieval retrieval : retrievals){
            Bitmap bmp = cache.retrieveFromCache(retrieval.id);
            if(retrieval.how == ImageRetrieval.RES_ID){
                bmp = (bmp != null)? bmp : decodeSampledBitmapFromResource(context.getResources(), (Integer)retrieval.id);
            }else if(retrieval.how == ImageRetrieval.CONTENT_URI){
                bmp = decodeBitmapFromUri(context, (String)retrieval.id, (String)retrieval.params[0]);
            }
            if(bmp != null && !bmp.isRecycled()) {
                bitmaps.add(bmp);
                cache.addToCache(retrieval.id, bmp);
            }
        }
        return bitmaps;
    }

    public static Bitmap decodeBitmapFromUri(Context context, BitmapCache cache, ImageRetrieval retrieval) {
        Bitmap bmp = cache.retrieveFromCache(retrieval.id);
        bmp = (bmp != null && !bmp.isRecycled())?
                bmp :
                decodeBitmapFromUri(context, (String)retrieval.id, (String)retrieval.params[0]);

        return bmp;
    }

    public static Bitmap decodeBitmapFromUri(Context context, String uri, String column){
        byte[] imgData = null;
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
     * Method slightly modified from Android documentation: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     * The images is sampled so that the image is scaled down to 100 x 100 pixels.
     * @param res
     * @param resId
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);


        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, DEFAULT_WIDTH, DEFAULT_HEIGHT);

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


    /**
     * Determines how to retrieve an image through the BitmapLoader.
     *      */
    public static class ImageRetrieval{
        public static final int RES_ID = BitmapTask.LOAD_FROM_RES_ID;
        public static final int CONTENT_URI = BitmapTask.LOAD_FROM_CONTENT_URI;
        //public static final int FILE = 2;

        public int how;
        public Object id;
        public Object[] params;

        /**
         *
         * @param howToRetrieve determines whether the image will be loaded from a resource (RES_ID) or a uri (CONTENT_URI)
         * @param identifier for RES_ID, this is the integer representing the drawable resource. For CONTENT_URI, this is the URI string
         * @param args For CONTENT_URI, this is the column that holds the image from the URI in position 0.
         */
        public ImageRetrieval(int howToRetrieve, Object identifier, Object... args){
            how = howToRetrieve;
            id = identifier;
            params = args;
        }

        public boolean isValid(){
            boolean valid = (id != null) && (how == RES_ID || how == CONTENT_URI);
            return valid;
        }
    }
}
