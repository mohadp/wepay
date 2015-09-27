package com.jumo.tablas.view.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.LruCache;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.jumo.tablas.provider.dao.EntityCursor;

import java.lang.ref.WeakReference;

/**
 * Created by Moha on 9/18/15.
 */
public abstract class DrawableCursorAdapter extends CursorAdapter {

    protected WeakReference<LruCache<String, Bitmap>> mCacheReference;

    public DrawableCursorAdapter(Context context, EntityCursor cursor) {
        super(context, cursor, 0);
    }

    public DrawableCursorAdapter(Context context, EntityCursor cursor, LruCache<String, Bitmap> cache) {
        super(context, cursor, 0);
        mCacheReference = new WeakReference<LruCache<String, Bitmap>>(cache);
    }

    protected void setBitmapInImageView(Context context, int resId, ImageView imageView){
        //Try to avoid reloading a bitmap if the ImageView already has its a valid bitmap
        String imageViewTag = (String)imageView.getTag();
        BitmapDrawable currBitmapDrawable = (BitmapDrawable)imageView.getDrawable();

        if(imageViewTag != null && imageViewTag.equals(String.valueOf(resId)) && currBitmapDrawable != null)
            return; // in this case, do not reload the bitmap because the ImageView already has the correct bitmap.

        Bitmap bitmap = null;
        if(mCacheReference != null && mCacheReference.get() != null) {
            LruCache<String, Bitmap> cache = mCacheReference.get();
            bitmap = cache.get(String.valueOf(resId));
        }

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
            imageView.setTag(String.valueOf(resId));
        }else{
            loadBitmap(context, resId, imageView, null);
        }
    }

    protected void loadBitmap(Context context, int resId, ImageView imageView, Bitmap placeHolder) {
        if (BitmapTask.cancelPotentialWork(resId, imageView)) {
            //Log.d(TAG, "loadBitmap: cancelPotentialWork is true");
            final BitmapTask task = new BitmapTask(imageView, context.getResources());
            final DrawableAsync drawableAsync = new DrawableAsync(context.getResources(), placeHolder, task);
            imageView.setImageDrawable(drawableAsync);
            task.execute(resId);
        }
        //Log.d(TAG, "loadBitmap: cancelPotentialWork is false");
    }
}
