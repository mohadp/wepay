package com.jumo.tablas.ui.util;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Moha on 1/12/16.
 */
public class BitmapCache implements CacheManager<Object, Bitmap> {
    private static final int PART_OF_MAX_MEM_FOR_CACHE = 8;


    private LruCache<Object, Bitmap> mCache;
    private static BitmapCache bitmapCache;

    public static BitmapCache getInstance(){
        if(bitmapCache == null){
            bitmapCache = new BitmapCache();
        }
        return bitmapCache;
    }

    private BitmapCache(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / PART_OF_MAX_MEM_FOR_CACHE;  // Use 1/8th of the available memory for this memory cache.
        mCache = new LruCache<Object, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(Object key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024; // The cache size will be measured in kilobytes
            }
        };
    }

    @Override
    public synchronized void addToCache(Object key, Bitmap bitmap) {
        if(mCache.get(key) == bitmap) {
            return;
        } else {
            mCache.put(key, bitmap);
        }
    }

    @Override
    public Bitmap retrieveFromCache(Object key) {
        return (key == null)? null : mCache.get(key);
    }

}
