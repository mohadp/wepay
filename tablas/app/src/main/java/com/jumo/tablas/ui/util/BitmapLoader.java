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
        //Try to avoid reloading a bitmap if the ImageView already has its a valid bitmap
        String imageViewTag = (String)imageView.getTag();
        BitmapDrawable currBitmapDrawable = (BitmapDrawable)imageView.getDrawable();

        if(imageViewTag != null && imageViewTag.equals(resource.id.toString()) && currBitmapDrawable != null)
            return; // in this case, do not reload the bitmap because the ImageView already has the correct bitmap.

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


    public static class ImageRetrieval{
        public static final int RES_ID = BitmapTask.LOAD_FROM_RES_ID;
        public static final int CONTENT_URI = BitmapTask.LOAD_FROM_CONTENT_URI;
        //public static final int FILE = 2;

        public int how;
        public Object id;
        public Object[] params;

        public ImageRetrieval(int howToRetrieve, Object identifier, Object... args){
            how = howToRetrieve;
            id = identifier;
            params = args;
        }
    }
}
