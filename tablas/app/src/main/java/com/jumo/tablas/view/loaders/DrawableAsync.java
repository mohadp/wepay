package com.jumo.tablas.view.loaders;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by Moha on 9/13/15.
 */
public class DrawableAsync extends BitmapDrawable {
    private final WeakReference<BitmapTask> taskReference;

    public DrawableAsync(Resources res, Bitmap bitmap, BitmapTask task){
        super(res, bitmap);
        taskReference = new WeakReference<BitmapTask>(task);
    }

    public BitmapTask getBitmapTask(){
        return taskReference.get();
    }

}
