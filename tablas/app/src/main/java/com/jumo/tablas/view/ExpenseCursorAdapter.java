package com.jumo.tablas.view;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.HandlerThread;
import android.util.LruCache;

import com.jumo.tablas.provider.TablasContract;

import android.content.Context;
import android.view.*;
import com.jumo.tablas.model.*;
import android.widget.*;

import com.jumo.tablas.R;
import com.jumo.tablas.provider.dao.*;
import com.jumo.tablas.view.custom.ImageViewRow;
import com.jumo.tablas.view.custom.RoundImageView;
import com.jumo.tablas.view.loaders.DrawableCursorAdapter;
import com.jumo.tablas.view.loaders.ExpenseUserThreadHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

class ExpenseCursorAdapter extends DrawableCursorAdapter {
	private static final String TAG = "ExpenseCursorAdapter";
	
	//To get balances on a per-user basis for a particular group
	private String mUserName;
	private long groupId;
	private WeakReference<LruCache<String, Bitmap>> mCacheReference;
    private WeakReference<HandlerThread> mHandlerReference;


    public ExpenseCursorAdapter(Context context, EntityCursor cursor, LruCache<String, Bitmap> cache, HandlerThread handler) {
        super(context, cursor, cache);
        mHandlerReference = new WeakReference<HandlerThread>(handler);

    }

    //Create view for each item
    @Override
    public View newView(Context context, android.database.Cursor cursor, ViewGroup parent) {
        // use a layout inflater to get a row view
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.list_item_message, parent, false);

 		return view;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        return super.getView(position, convertView, parent);
    }


    //fill the view with data from the cursor.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();
        if(holder == null) {
            holder =  new ViewHolder();
            holder.desc = (TextView) view.findViewById(R.id.list_message_desc);
            holder.balance = (TextView) view.findViewById(R.id.list_message_balance);
            holder.total = (TextView) view.findViewById(R.id.list_message_total);
            holder.date = (TextView) view.findViewById(R.id.list_message_date);
            holder.location = (TextView) view.findViewById(R.id.list_message_location);
            holder.image = (ImageView) view.findViewById(R.id.list_message_image);
            holder.category = (ImageView) view.findViewById(R.id.list_message_category);
            holder.payerImages = (ImageViewRow) view.findViewById(R.id.list_message_payers_images);
        }

        EntityCursor expenseCursor = (EntityCursor)getCursor();
        if(expenseCursor == null)
            return;

        // get the run for the current row
        final Expense expense = new Expense(expenseCursor.getEntity(TablasContract.Expense.getInstance()));
        //Log.d(TAG, "Position " + getCursor().getPosition() + ": " + expense.toString());


        // set up the start date text view
        //((ImageView)view.findViewById(R.id.list_message_image)).setImageResource(R.drawable.ic_launcher);
        setBitmapInImageView(context, R.drawable.moha, holder.image); //loaded in separate thread if not present in cache
        setBitmapInImageView(context, R.drawable.ic_launcher, holder.category);  //TODO: will load image of category once I have the category images
        loadPayersForExpense(expense, holder.payerImages);

		/*int rand = (int) Math.round(Math.random()*100) % 5 + 1;

        holder.payerImages.removeAllImageViews();
        for(int i = 0; i < rand; i++){
			RoundImageView img = new RoundImageView(context);
            //img.setImageBitmap(null);
            //roundImages.add(img);
            holder.payerImages.addImageView(img);
            setBitmapInImageView(context, R.drawable.moha, img);
		}*/

		holder.desc.setText(expense.getMessage());
		holder.balance.setText(String.format("%1$.2f", expense.getUserBalance()));
		holder.total.setText(String.format("%1$.2f", expense.getAmount()));
		holder.date.setText(expense.getCreatedOn().toLocaleString());
		holder.location.setText("Washington, DC, USA");

        //sets the view holder object
        view.setTag(holder);
    }

    private void loadPayersForExpense(Expense expense, ImageViewRow payerImages){
        //Let the loading of the images for the payers be done on a separate thread only if they have not yet been loaded.
        Long imageRowTag = (Long)payerImages.getTag();
        final long expenseId = expense.getId();

        //Load all the bitmaps representing all the payers per expense. We then update the imageRows once the bitmaps are loaded.
        ExpenseUserThreadHandler payerWorker = (ExpenseUserThreadHandler) mHandlerReference.get();
        payerWorker.queueExpensePayers(payerImages, expense.getId());
        payerWorker.setOnImagesLoaded(new ExpenseUserThreadHandler.OnImagesLoaded() {
            @Override
            public void onImagesLoaded(ImageViewRow imgRow, ArrayList<String> bitmapIds, ArrayList<Bitmap> images) {
                setRoundImageViewBitmaps(imgRow, bitmapIds, images);
            }
        });
    }

    public void setRoundImageViewBitmaps( ImageViewRow imgRow, ArrayList<String> bitmapIds, ArrayList<Bitmap> bitmaps){

        int currentBitmap;
        for (currentBitmap = 0; currentBitmap < bitmaps.size(); currentBitmap++) {
            ImageView imgView = (ImageView) imgRow.getChildAt(currentBitmap);
            Bitmap img = bitmaps.get(currentBitmap);
            boolean newImageView = false;

            if(imgView == null){
                imgView = new RoundImageView(imgRow.getContext());
                newImageView = true;
            }
            String imgViewTag = (String)imgView.getTag();

            //We verify that the tag has the ID of the bitmap. If it is the same, we do not update the image (it already has the correct bitmap).
            if(imgViewTag == null || !imgViewTag.equals(bitmapIds.get(currentBitmap))) {
                imgView.setImageBitmap(img);
                imgView.setTag(bitmapIds.get(currentBitmap));
            }
            //If the image was just created, then we add it ot the imgRow
            if (newImageView) {
                imgRow.addImageView(imgView);
            }
        }

        ImageView imgView = (ImageView) imgRow.getChildAt(currentBitmap);
        while (imgView != null) {
            imgView.setImageBitmap(null);
            imgView = (ImageView) imgRow.getChildAt(++currentBitmap);
        }
    }

    private class ViewHolder{
        ImageView image;
        ImageView category;
        ImageViewRow payerImages;
        TextView desc;
        TextView balance;
        TextView total;
        TextView date;
        TextView location;


        public String toString(){
            StringBuffer sb = new StringBuffer();
            sb.append(desc.getText()).append("; ").append(balance.getText()).append("; ").append(total.getText()).append("; ").append(date.getText()).append("; ").append(location.getText());
            return sb.toString();
        }
    }
}
