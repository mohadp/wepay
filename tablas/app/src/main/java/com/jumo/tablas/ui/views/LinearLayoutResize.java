package com.jumo.tablas.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Moha on 11/28/15.
 */
public class LinearLayoutResize extends LinearLayout {

    private OnSizeChange mOnSizeChange;

    public LinearLayoutResize(Context context){
        super(context);
    }

    public LinearLayoutResize(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public LinearLayoutResize(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LinearLayoutResize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        if(mOnSizeChange != null){
            mOnSizeChange.onSizeChanged(w, h, oldw, oldh);
        }
    }

    public OnSizeChange getOnSizeChange() {
        return mOnSizeChange;
    }

    public void setOnSizeChange(OnSizeChange mOnSizeChange) {
        this.mOnSizeChange = mOnSizeChange;
    }


    public interface OnSizeChange{
        public void onSizeChanged(int w, int h, int oldw, int oldh);
    }

}
