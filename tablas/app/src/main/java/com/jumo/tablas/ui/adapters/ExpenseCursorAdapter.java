package com.jumo.tablas.ui.adapters;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.HandlerThread;

import com.jumo.tablas.provider.TablasContract;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import com.jumo.tablas.model.*;
import android.widget.*;

import com.jumo.tablas.R;
import com.jumo.tablas.provider.dao.*;
import com.jumo.tablas.ui.util.BitmapLoader;
import com.jumo.tablas.ui.util.CacheManager;
import com.jumo.tablas.ui.views.ImageViewRow;
import com.jumo.tablas.ui.views.RoundImageView;
import com.jumo.tablas.ui.loaders.ExpenseUserThreadHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ExpenseCursorAdapter extends RecyclerView.Adapter<ExpenseCursorAdapter.ExpenseViewHolder> {
	private static final String TAG = "ExpenseCursorAdapter";
	
	//To get balances on a per-user basis for a particular group
    protected WeakReference<Context> mContextReference;
    private WeakReference<CacheManager> mCacheContainerReference;
    private WeakReference<HandlerThread> mHandlerReference; //Initially, this is to load payers; we will change this and handle all here, by asynchcronously loading images for contacts.
    private EntityCursor mCursor;

    public ExpenseCursorAdapter(Context context, EntityCursor cursor, CacheManager cacheManager, HandlerThread handler) {
        super();
        mContextReference = new WeakReference<Context>(context);
        mHandlerReference = new WeakReference<HandlerThread>(handler);
        mCacheContainerReference = new WeakReference<CacheManager>(cacheManager);
        mCursor = cursor;
    }


    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_item_message, parent, false);
        ExpenseViewHolder viewHolder = new ExpenseViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {

        if(mCursor == null || mCursor.isClosed())
            return;

        mCursor.moveToPosition(position);

        // get the run for the current row
        final Entity entity = mCursor.getEntity(TablasContract.Compound.ExpenseBalance.getInstance());
        final Expense expense = new Expense(entity);
        //Log.d(TAG, "Position " + getCursor().getPosition() + ": " + expense.toString());

        // set up the start date text view
        BitmapLoader.asyncSetBitmapInImageView(
                new BitmapLoader.ImageRetrieval(BitmapLoader.ImageRetrieval.RES_ID, R.drawable.moha),
                holder.image, mContextReference.get(), mCacheContainerReference.get()); //loaded in separate thread if not present in cache

        BitmapLoader.asyncSetBitmapInImageView( //TODO: will load image of category once I have the category images
                new BitmapLoader.ImageRetrieval(BitmapLoader.ImageRetrieval.RES_ID, R.drawable.ic_launcher),
                holder.category, mContextReference.get(), mCacheContainerReference.get());

        loadPayersForExpense(expense, holder.payerImages); //TODO: replace this with BitmapLoader loading of class expenses (since we can use submit URI


        holder.desc.setText(expense.getMessage());
        holder.balance.setText(String.format("%1$.2f", entity.getDouble(TablasContract.Compound.ExpenseBalance.USER_BALANCE)));
        holder.total.setText(String.format("%1$.2f", expense.getAmount()));
        holder.date.setText(expense.getCreatedOn().toLocaleString());
        holder.location.setText("Washington, DC, USA");

    }

    @Override
    public int getItemCount() {
        if(mCursor == null || mCursor.isClosed()){
            return 0;
        }
        return mCursor.getCount();
    }


    private void loadPayersForExpense(Expense expense, ImageViewRow payerImages){
        //Let the loading of the images for the payers be done on a separate thread only if they have not yet been loaded.
        Long imageRowTag = (Long)payerImages.getTag();
        final long expenseId = expense.getId();

        //Load all the bitmaps representing all the payers per expense. We then update the imageRows once the bitmaps are loaded.
        ExpenseUserThreadHandler payerWorker = (ExpenseUserThreadHandler) mHandlerReference.get();
        payerWorker.setOnImagesLoaded(new ExpenseUserThreadHandler.OnImagesLoaded() {
            @Override
            public void onImagesLoaded(ImageViewRow imgRow, ArrayList<String> bitmapIds, ArrayList<Bitmap> images) {
                setRoundImageViewBitmaps(imgRow, bitmapIds, images);
            }
        });
        payerWorker.queueExpensePayers(payerImages, expense.getId());

    }

    public void setRoundImageViewBitmaps(ImageViewRow imgRow, ArrayList<String> bitmapIds, ArrayList<Bitmap> bitmaps){

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

    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * Modifies the cursor for this adapter. It also notifies the dataset that the whole dataset has changed (this calls
     * notifyDataSetChanged().
     * @param cursor
     * @return returns the old cursor.
     */
    public Cursor swapCursor(EntityCursor cursor){
        Cursor oldCursor = mCursor;
        mCursor = cursor;
        this.notifyDataSetChanged();
        return oldCursor;
    }

    /**
     * Does the same as swapCursor, except for the fact that the previous cursor is closed after swaping cursors.
     * @param cursor
     */
    public void changeCursor(EntityCursor cursor){
        Cursor oldCursor = swapCursor(cursor);
        if(oldCursor != null && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        protected ImageView image;
        protected ImageView category;
        protected ImageViewRow payerImages;
        protected TextView desc;
        protected TextView balance;
        protected TextView total;
        protected TextView date;
        protected TextView location;
        //Todo: here we should add a "listener" member that will contain the callback defined in the listener.

        public ExpenseViewHolder(View itemView){
            super(itemView);
            this.desc = (TextView) itemView.findViewById(R.id.list_message_desc);
            this.balance = (TextView) itemView.findViewById(R.id.list_message_balance);
            this.total = (TextView) itemView.findViewById(R.id.list_message_total);
            this.date = (TextView) itemView.findViewById(R.id.list_message_date);
            this.location = (TextView) itemView.findViewById(R.id.list_message_location);
            this.image = (ImageView) itemView.findViewById(R.id.list_message_image);
            this.category = (ImageView) itemView.findViewById(R.id.list_message_category);
            this.payerImages = (ImageViewRow) itemView.findViewById(R.id.list_message_payers_images);
        }


        public String toString(){
            StringBuffer sb = new StringBuffer();
            sb.append(desc.getText()).append("; ").append(balance.getText()).append("; ").append(total.getText()).append("; ").append(date.getText()).append("; ").append(location.getText());
            return sb.toString();
        }
    }
}
