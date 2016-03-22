package com.jumo.tablas.ui.views;
import android.view.View;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.*;

import java.util.ArrayList;

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

    private static double calculateDiameter(double width, double height){
        return (width <= height) ? width : height;
    }


    /**
     * //The RoundImageView will be of circular shape, which means the circle can fit in a square.
     * Will always make width and height the same (whichever is the smallest)
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
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


    public static Bitmap createRoundBitmap(Bitmap bmp, int diameter) {
		
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
	
	public static Bitmap createCircularBorder(float radius, int width, int height){
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

    public static Bitmap createCollagedBitmap(ArrayList<Bitmap> origBitmaps){
        double minHeight = origBitmaps.get(0).getWidth();
        double minWidth = origBitmaps.get(0).getHeight();
        double diameter = 0;

        for(Bitmap bm : origBitmaps){
            minWidth = (bm.getWidth() < minWidth)? bm.getWidth() : minWidth;
            minHeight = (bm.getHeight() < minHeight)? bm.getHeight() : minHeight;
        }
        diameter = calculateDiameter(minWidth, minHeight);

        Bitmap output = Bitmap.createBitmap((int)diameter, (int)diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0); //Transparent black

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        int noBmps = origBitmaps.size();
        for(int position = 0; position < noBmps; position++){
            Bitmap bmp = origBitmaps.get(position);
            Position targetLocation = getPosition((int)diameter, noBmps, position);
            Rect srcRect = new Rect(targetLocation.x, targetLocation.y, targetLocation.getRight(), targetLocation.getBottom());
            Position srcLocation = getCenterRectPosition(bmp, targetLocation.width, targetLocation.height);
            Rect tgtRect = new Rect(srcLocation.x, srcLocation.y, srcLocation.getRight(), srcLocation.getBottom());

            canvas.drawBitmap(bmp, srcRect, tgtRect, paint);
        }
        return output;
    }

    /**
     * Helper method that gives the position and targetPicSize of where a picture should be positioned in a new picture composed of
     * up to 4 sections.
     * There is Maximum four pictures in a pie picture.
     * If 1 cuadrant, then, one picture the targetPicSize of the new picture.
     * If 2 cuadrants, each cuadrant uses half the new picture.
     * If 3 cuadrants, then the first cuadrant uses the left half of the picture, and the other two split the second right half equally.
     * If 4 cuadrants, then all pictures use a quarter of the new picture.
     * @param targetPicSize is the targetPicSize of the target/final square-shaped picture (width or height of a square-sized target picture)
     * @param noCuadrants total number of cuadrants the final picture will ahve
     * @param currCuadrant the cuadrant for which the position will be calculated
     * @return
     */
    private static Position getPosition(int targetPicSize, int noCuadrants, int currCuadrant){
        int targetWidth = 0, targetHeight = 0;
        int targetX = 0, targetY = 0;
        if(noCuadrants == 1){
            targetWidth = targetHeight = (int)targetPicSize;
        }else if(noCuadrants == 2){
            targetWidth = (int)(targetPicSize/2);
            targetHeight = (int)targetPicSize;
            targetX = (currCuadrant == 1)? (int)(targetPicSize/2) : 0;
        }else if((noCuadrants == 3 )){
            targetWidth = (int) (targetPicSize/2);
            targetHeight = (currCuadrant == 0)? (int)targetPicSize : (int)(targetPicSize / 2);
            targetX = (currCuadrant >= 1)? (int)(targetPicSize/2) + 1 : 0; //adding one to add a bit of space between sections.
            targetY = (currCuadrant == 2)? (int)(targetPicSize/2) + 1 : 0; //adding one to add a bit of space between sections.
        }else{
            targetWidth = targetHeight = (int) (targetPicSize/2);
            targetX = (currCuadrant == 1 || currCuadrant == 2)? (int)(targetPicSize/2) + 1 : 0; //adding one to add a bit of space between sections.
            targetY = (currCuadrant == 2 || currCuadrant == 3)? (int)(targetPicSize/2) + 1 : 0; //adding one to add a bit of space between sections.
        }

        return new Position(targetX, targetY, targetWidth, targetHeight);
    }


    /**
     * Gets the location/position of a rectangle in the center of the input bitmap, of the size of the target width/height.
     * @param bmp the bmp from which the center rectangle will be retrieved.
     * @param width the width of the rectangle in input BMP that will be retrieved.
     * @param height the height of the rectangle in input BMP that will be retrieved.
     * @return
     */
    private static Position getCenterRectPosition(Bitmap bmp, int width, int height){
        Position srcPos =  new Position();
        int centerX = bmp.getWidth() / 2;
        int centerY = bmp.getHeight() / 2;

        srcPos.x = centerX - width/2;
        srcPos.y = centerY - height/2;
        srcPos.width = width;
        srcPos.height = height;

        return srcPos;
    }

    private static class Position{
        protected int x;
        protected int y;
        protected int width;
        protected int height;

        protected Position(){
            this.x = this.y = this.width = this.height = 0;
        }

        protected Position(int x, int y, int width, int height){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        protected int getRight(){
            return x + width;
        }

        protected int getBottom(){
            return y + height;
        }
    }
	
}


