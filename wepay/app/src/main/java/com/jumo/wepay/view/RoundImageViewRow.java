package com.jumo.wepay.view;
import android.widget.*;
import android.content.*;
import android.graphics.drawable.*;
import android.graphics.*;
import java.util.*;
import android.util.*;
import android.view.*;


public class RoundImageViewRow extends LinearLayout{
	
	public RoundImageViewRow(Context context, String[] imageNames){
		super(context);
		//Not yet implemented, but will be loading from file system eventually
	}
	
	public RoundImageViewRow(Context context, int[] resIds){
		super(context);
	}
	
	
	public RoundImageViewRow(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public RoundImageViewRow(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
	}

	/*public RoundImageViewRow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
		super(context, attrs, defStyleAttr, defStyleRes);
	}*/
	
	public void addRoundImageViews(int[] resIds){
		for(int i : resIds){
			RoundImageView roundImage = new RoundImageView(getContext());
			roundImage.setImageResource(i);
			roundImage.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			roundImage.setPadding(0,0,2,0);
			this.addView(roundImage);
		}
	}
	
	public void addRoundImageViews(ArrayList<RoundImageView> images){
		for(RoundImageView i : images){
			this.addView(i);
		}
	}

    /**
     * Method removes all RoundImageViews; all their bitmaps are recycled.
     */
    public void removeAllRoundImageViews(){
        int count = this.getChildCount();
        for(int i = count-1; i >= 0; i--){
            RoundImageView image = (RoundImageView)this.getChildAt(0);
            ((BitmapDrawable)image.getDrawable()).getBitmap().recycle();
            this.removeViewAt(i);
        }
    }
}
