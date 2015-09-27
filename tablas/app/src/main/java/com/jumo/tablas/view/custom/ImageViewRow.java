package com.jumo.tablas.view.custom;
import android.widget.*;
import android.content.*;

import java.util.*;
import android.util.*;
import android.view.*;


public class ImageViewRow extends LinearLayout{

	private final String TAG = "ImageViewRow";

	public ImageViewRow(Context context){
		super(context);
	}

	public ImageViewRow(Context context, String[] imageNames){
		super(context);
		//Not yet implemented, but will be loading from file system eventually
	}
	
	public ImageViewRow(Context context, int[] resIds){
		super(context);
	}
	
	
	public ImageViewRow(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public ImageViewRow(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
	}

	/*public RoundImageViewRow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
		super(context, attrs, defStyleAttr, defStyleRes);
	}*/
	
	public void addRoundImageViews(int[] resIds){
		for(int i : resIds){
			RoundImageView roundImage = new RoundImageView(getContext());
			roundImage.setImageResource(i);
			roundImage.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			roundImage.setPadding(0,0,2,0);
			this.addView(roundImage);
		}
	}

	public void addImageView(ImageView view){
		view.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		view.setPadding(0, 0, 2, 0);
		addView(view);
	}
	
	public void addImageViews(ArrayList<ImageView> imageViews){
		for(ImageView imgView : imageViews){
			this.addImageView(imgView);
		}
	}

    /**
     * Method removes all RoundImageViews; all their bitmaps are recycled.
     */
    public void removeAllImageViews(){
        int count = this.getChildCount();
        for(int i = count-1; i >= 0; i--){
            ImageView image = (ImageView)this.getChildAt(0);
			image.setImageBitmap(null);
            this.removeViewAt(i);
        }
    }
}
