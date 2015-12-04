package com.jumo.tablas.ui.controllers;

import android.graphics.Bitmap;

import com.jumo.tablas.model.Member;
import com.jumo.tablas.ui.util.CacheManager;

import java.lang.ref.WeakReference;
import java.util.HashSet;

/**
 * Created by Moha on 11/29/15.
 * Class will control the custom keyboard:
 * - Load all necessary data to show the data (reusing the fragment's cache instance
 * - Record all the keyboard selections
 * - Communicate all keyboard selections to the class
 */
public class KeyboardManager {
    private String mUserId;
    private long mGroupId;
    private WeakReference<CacheManager<Object, Bitmap>> mCacheMgrReference;
    private static KeyboardManager mKeyboardManager;




    private KeyboardManager(CacheManager<Object, Bitmap> cacheManager, String userId, long groupId){
        mUserId = userId;
        mGroupId = groupId;
        mCacheMgrReference = new WeakReference<CacheManager<Object, Bitmap>>(cacheManager);
    }



}
