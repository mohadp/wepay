package com.jumo.tablas.ui.views;
import android.view.View;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.*;

public class RoundImageView extends ImageView{
	
	private final static int RADIUS_SHORTENER = 1;
    private static final String TAG = "RoundImageView";

	public RoundImageView(Context context) { 
		super(context);  
	} 

	public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        /*
        Bitmap image = ((BitmapDrawable) this.getDrawable()).getBitmap();
        double width = image.getWidth(), height = image.getHeight();
        float radiusReducer = (height > width)? ((float) (width / height)) : ((float) (height /width));
        float diameter = (float)calculateDiameter(canvas.getWidth(), canvas.getHeight());

        float radius = ((1f*diameter)/2f);


        Paint paint = ((BitmapDrawable)getDrawable()).getPaint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(0x00000000);

        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(radius, radius, radius * radiusReducer - 1, paint);
        Xfermode oldMode = paint.getXfermode();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        super.onDraw(canvas);
        paint.setXfermode(oldMode);*/


        //Get the original bitmap, and create a circular bitmap
        Bitmap originalBmp = (getDrawable() == null)? null : ((BitmapDrawable) this.getDrawable()).getBitmap();
        if(originalBmp == null) {
            return;
        }

        double width = originalBmp.getWidth(), height = originalBmp.getHeight();

        //Log.d(TAG, "Image is recycled: "+ originalBmp.isRecycled());

        double bmpDiameter = calculateDiameter((int)width, (int)height);
        Bitmap newRoundBmp = createRoundBitmap(originalBmp, (int) bmpDiameter);
        //setImageBitmap(newRoundBmp);

        if(newRoundBmp != null) {
            //Draw the new circular bitmap
            double cavasDiameter = calculateDiameter(canvas.getWidth(), canvas.getHeight());
            Rect rectSrc = new Rect(0, 0, newRoundBmp.getWidth(), newRoundBmp.getWidth());
            Rect rect = new Rect(0, 0, (int) Math.floor(cavasDiameter), (int) Math.floor(cavasDiameter));
            canvas.drawBitmap(newRoundBmp, rectSrc, rect, null);
        }
    }

    private double calculateDiameter(double width, double height){
        return (width <= height) ? width : height;
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
		
		if(bmp == null && !bmp.isRecycled()) return null;
		
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		
		float radius = (float)Math.floor(diameter*1.0/2.0) - RADIUS_SHORTENER; //so the image looks round

		//Create new base bitmap over which the original image will be drawn, along with the circle effect.
		Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.drawARGB(0, 0, 0, 0); //Transparent black
		
		//Create parameters to draw circle (white circle).	
		Rect rect = new Rect(0,0,diameter, diameter);
		Paint paint = new Paint();
		paint.setAntiAlias(true); 
		paint.setFilterBitmap(true); 
		paint.setDither(true);
		
		//First draw circle
		paint.setColor(Color.parseColor("#FFFFFF"));
		canvas.drawCircle(diameter*1f/2f, diameter*1f/2f, radius, paint);
		
		//Draw original bitmap, but only keep the "pixels" from the bmp where there are other pixels in the exitent output bitmap
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bmp, null, rect, paint);
		
		//Include circular border
		paint.setColor(Color.parseColor("#000000"));
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
		canvas.drawBitmap(createCircularBorder(radius,diameter,diameter), null, rect,paint);

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
