package com.jumo.tablas.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.LruCache;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.jumo.tablas.provider.dao.EntityCursor;
import com.jumo.tablas.ui.loaders.BitmapTask;
import com.jumo.tablas.ui.loaders.DrawableAsync;

import java.lang.ref.WeakReference;

/**
 * Created by Moha on 9/18/15.
 */
public abstract class DrawableCursorAdapter extends CursorAdapter {

    protected WeakReference<LruCache<Object, Bitmap>> mCacheReference;
    protected WeakReference<Context> mContextReference;

    public DrawableCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mContextReference = new WeakReference<Context>(context);
    }

    public DrawableCursorAdapter(Context context, Cursor cursor, LruCache<Object, Bitmap> cache) {
        super(context, cursor, 0);
        mCacheReference = new WeakReference<LruCache<Object, Bitmap>>(cache);
        mContextReference = new WeakReference<Context>(context);
    }


    protected void asyncSetBitmapInImageView(ImageRetrieval resource, ImageView imageView){
        //Try to avoid reloading a bitmap if the ImageView already has its a valid bitmap
        String imageViewTag = (String)imageView.getTag();
        BitmapDrawable currBitmapDrawable = (BitmapDrawable)imageView.getDrawable();

        if(imageViewTag != null && imageViewTag.equals(resource.id.toString()) && currBitmapDrawable != null)
            return; // in this case, do not reload the bitmap because the ImageView already has the correct bitmap.

        Bitmap bitmap = null;
        if(mCacheReference != null && mCacheReference.get() != null) {
            LruCache<Object, Bitmap> cache = mCacheReference.get();
            bitmap = cache.get(resource.id);
        }

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
            imageView.setTag(resource.id.toString());
        }else{
            loadBitmap(resource, imageView, null);
        }
    }

    private void loadBitmap(ImageRetrieval resource, ImageView imageView, Bitmap placeHolder) {
        if (BitmapTask.cancelPotentialWork(resource.id, imageView)) {
            //Log.d(TAG, "loadBitmap: cancelPotentialWork is true");
            Context context = mContextReference.get();
            if(context == null)
                return;

            final BitmapTask task = new BitmapTask(imageView, context, mCacheReference.get());
            final DrawableAsync drawableAsync = new DrawableAsync(context.getResources(), placeHolder, task);
            imageView.setImageDrawable(drawableAsync);
            executeTask(task, resource);
        }
    }


    private void executeTask(BitmapTask task, ImageRetrieval resource){
        switch(resource.how){
            case ImageRetrieval.RES_ID:
                task.execute(BitmapTask.LOAD_FROM_RES_ID, resource.id);
                return;
            case ImageRetrieval.CONTENT_URI: //we expect the ImageRetrieval object to have an additional parameter, the column to retrieve.
                if(resource.params.length <= 1)  return;
                task.execute(BitmapTask.LOAD_FROM_CONTENT_URI, resource.id, resource.params[0]);
                return;
        }
    }


    public class ImageRetrieval{
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
