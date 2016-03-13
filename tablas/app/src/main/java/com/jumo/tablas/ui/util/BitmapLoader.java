package com.jumo.tablas.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.jumo.tablas.ui.loaders.BitmapTask;
import com.jumo.tablas.ui.loaders.DrawableAsync;

/**
 * Created by Moha on 11/4/15.
 */
public  class BitmapLoader {


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
         * @param args For CONTENT_URI, this is the column that holds the image from the URI.
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
