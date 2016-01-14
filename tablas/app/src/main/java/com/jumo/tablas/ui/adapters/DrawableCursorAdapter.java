package com.jumo.tablas.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.jumo.tablas.ui.util.BitmapCache;
import com.jumo.tablas.ui.util.BitmapLoader;
import com.jumo.tablas.ui.util.CacheManager;

import java.lang.ref.WeakReference;

/**
 * Created by Moha on 9/18/15.
 */
public abstract class DrawableCursorAdapter extends CursorAdapter{

    protected WeakReference<Context> mContextReference;

    public DrawableCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mContextReference = new WeakReference<Context>(context);
    }


    protected void loadBitmap(BitmapLoader.ImageRetrieval resource, ImageView imageView){
        if(mContextReference == null)
            return;

        Context context = mContextReference.get();
        CacheManager cacheManager = BitmapCache.getInstance();
        if(context == null || cacheManager == null)
            return;

        BitmapLoader.asyncSetBitmapInImageView(resource, imageView, context, cacheManager);
    }

}
