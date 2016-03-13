package com.jumo.tablas.ui.views;

import android.content.Context;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by Moha on 12/25/15.
 */
public class KeyboardController {
    //private final static int

    private WeakReference<Context> mContextRef;
    private WeakReference<View> mHostViewRef;
    private KeyboardController mKeyboard;

    public KeyboardController(Context context, View view){
        mContextRef = new WeakReference<Context>(context);
        mHostViewRef = new WeakReference<View>(view);

    }

    public KeyboardController getInstance(Context context, View view){
        if(mKeyboard == null){
            mKeyboard = new KeyboardController(context, view);
        }
        return mKeyboard;
    }



}
