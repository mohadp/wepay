package com.jumo.tablas.view.custom;

import android.view.KeyEvent;

/**
 * Created by Moha on 9/30/15; expanding Fragment to be able to pass on the key events to the fragments
 */
public interface OnKeyEventListener {
    public boolean onKeyPress(int keyCode, KeyEvent event);
}
