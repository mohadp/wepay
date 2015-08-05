package com.jumo.wepay.view;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.shapes.*;
import android.graphics.drawable.*;
import android.util.*;

public class RoundImageView extends ImageView{
	
	private final static int RADIUS_SHORTENER = 1;
	
	public RoundImageView(Context context) { 
		super(context);  
	} 

	public RoundImageView(Context context, AttributeSet attrs) { 
		super(context, attrs); 
	} 

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) { 
		super(context, attrs, defStyle); } 
	
	@Override
	protected void onDraw(Canvas canvas){
		
		
		//Get the original bitmap
		Bitmap originalBmp = ((BitmapDrawable)this.getDrawable()).getBitmap();
		
		//Get diameter of circle for the pic, of the size of the smaller magnitude between width and height of imageview ( not of bitmap)
		
		Rect rect = new Rect();
		this.getDrawingRect(rect);
		
		int width = rect.width();  // this.getWidth();
		int height = rect.height(); // this.getHeight();
		int diameter = (width <= height)? width : height;
		
		Bitmap newRoundBmp = getRoundBitmap(originalBmp, diameter);
		canvas.drawBitmap(newRoundBmp, 0, 0, null);	
		this.setAdjustViewBounds(true);
	
	}
	
	private Bitmap getRoundBitmap(Bitmap bmp, int diameter) { 
		
		if(bmp == null) return null;
		
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		
		float radius = (float)Math.floor(diameter*1.0/2.0) - RADIUS_SHORTENER; //so the image looks round
		
		float proportion = 0f;
		Bitmap resizedBmp = bmp;

		//Resize the original bitmap so that at least the height or width is equal or less to the diameter
		//First find out if at least one, the hieght or width are of size diameter.
		if(width >= diameter && height >= diameter){
			//find out smaller and resize proportionally to diameter size
			if(height <= width){ //make height equal to diameter
				proportion =  diameter*1f / height*1f;
			} else{
				proportion =  diameter*1f / width*1f;
			}
			
			width = Math.round(width * proportion);
			height = Math.round(height * proportion);
			
			resizedBmp = Bitmap.createScaledBitmap(bmp, width, height, false); 
		}
		
		
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
		canvas.drawBitmap(resizedBmp, null, rect, paint);
		
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
