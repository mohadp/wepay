package com.jumo.wepay.view;
import android.view.View;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.*;

public class RoundImageView extends ImageView{
	
	private final static int RADIUS_SHORTENER = 1;

	public RoundImageView(Context context) { 
		super(context);  
	} 

	public RoundImageView(Context context, AttributeSet attrs) {
        //TODO: need to update size since the beginning; default constructors draw original image first in original size.
        super(context, attrs);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) { 
		//TODO: need to update size since the beginning; default constructors draw original image first in original size.
        super(context, attrs, defStyle);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //Get the original bitmap
        Bitmap originalBmp = ((BitmapDrawable) this.getDrawable()).getBitmap();

        int width = originalBmp.getWidth();  // this.getWidth();
        int height = originalBmp.getHeight(); // this.getHeight();
        int diameter = (width <= height) ? width : height;

        Bitmap newRoundBmp = createRoundBitmap(originalBmp, diameter);

        this.setImageDrawable(null);
        this.setImageBitmap(newRoundBmp);
        super.onDraw(canvas);
        //originalBmp.recycle(); Cannot call this because apparently it is still used by canvas to create new image.
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        int h = View.MeasureSpec.getSize(heightMeasureSpec);

        //The RoundImageView will be of circular shape, which means the circle can fit in a square. Will always make width and height the same (whichever is the smallest)
        if(w < h){
            h = w;
        }else{
            w = h;
        }

        w = View.MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
        h = View.MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);

        setMeasuredDimension(w,h);
    }


    private Bitmap createRoundBitmap(Bitmap bmp, int diameter) {
		
		if(bmp == null) return null;
		
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		
		float radius = (float)Math.floor(diameter*1.0/2.0) - RADIUS_SHORTENER; //so the image looks round

		//Create new base bitmap over which the original image will be drawn, along with the circle effect.
		Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.drawARGB(0, 0, 0, 0); //Transparent black
		
		//Create parameters to draw circle (white circle).	
		Rect rect = new Rect(0,0,width, height);
		Paint paint = new Paint();
		paint.setAntiAlias(true); 
		paint.setFilterBitmap(true); 
		paint.setDither(true);
		
		//First draw circle
		paint.setColor(Color.parseColor("#FFFFFF"));
		canvas.drawCircle(width*1f/2f, height*1f/2f, radius, paint);
		
		//Draw original bitmap, but only keep the "pixels" from the bmp where there are other pixels in the exitent output bitmap
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bmp, null, rect, paint);
		
		//Include circular border
		paint.setColor(Color.parseColor("#000000"));
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
		canvas.drawBitmap(createCircularBorder(radius,width,height), null, rect,paint);

		//After painting on the "output" bitmap, return that output.
		return output;	
	} 
	
	private Bitmap createCircularBorder(float radius, int width, int height){
		//Create new base bitmap over which the original image will be drawn, along with the circle effect.
		Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.drawARGB(0, 0, 0, 0); //Transparent black
		
		Paint paint = new Paint();
		paint.setAntiAlias(true); 
		paint.setFilterBitmap(true); 
		paint.setDither(true);
		
		//Include circular border
		paint.setColor(Color.parseColor("#C0C0C0"));
		canvas.drawCircle(width*1f/2f, height*1f/2f, radius+RADIUS_SHORTENER, paint);
		
		paint.setColor(Color.parseColor("#FFFFFF"));
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		canvas.drawCircle(width*1f/2f, height*1f/2f, radius, paint);
		
		return output;
	}
	
}
