package com.jumo.tablas.ui.adapters;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.HandlerThread;

import com.jumo.tablas.provider.TablasContract;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import com.jumo.tablas.model.*;
import android.widget.*;

import com.jumo.tablas.R;
import com.jumo.tablas.provider.dao.*;
import com.jumo.tablas.ui.util.BitmapCache;
import com.jumo.tablas.ui.util.BitmapLoader;
import com.jumo.tablas.ui.views.ImageViewRow;
import com.jumo.tablas.ui.views.RoundImageView;
import com.jumo.tablas.ui.loaders.ExpenseUserThreadHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ExpenseCursorAdapter extends RecyclerView.Adapter<ExpenseCursorAdapter.ExpenseViewHolder> {
	private static final String TAG = "ExpenseCursorAdapter";

    private final static int MESSAGE_TYPE_OTHERS = 0;
    private final static int MESSAGE_TYPE_ME = 1;
	
	//To get balances on a per-user basis for a particular group
    protected WeakReference<Context> mContextReference;
    private WeakReference<HandlerThread> mHandlerReference; //Initially, this is to load payers; we will change this and handle all here, by asynchcronously loading images for contacts.
    private EntityCursor mCursor;

    public ExpenseCursorAdapter(Context context, EntityCursor cursor, HandlerThread handler) {
        super();
        mContextReference = new WeakReference<Context>(context);
        mHandlerReference = new WeakReference<HandlerThread>(handler);
        mCursor = cursor;

    }


    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutId = (viewType == MESSAGE_TYPE_ME)? R.layout.list_item_message_me : R.layout.list_item_message;
        View view = inflater.inflate(layoutId, parent, false);
        ExpenseViewHolder viewHolder = new ExpenseViewHolder(view);

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position){
        if(mCursor == null || mCursor.isClosed()){
            return MESSAGE_TYPE_OTHERS;
        }
        mCursor.moveToPosition(position);

        Entity entity = mCursor.getEntity(TablasContract.Compound.ExpenseBalance.getInstance());
        boolean hasCurrUserPaid = entity.getInt(TablasContract.Compound.ExpenseBalance.CURR_USER_PAID) == 1;

        return (hasCurrUserPaid)? MESSAGE_TYPE_ME : MESSAGE_TYPE_OTHERS;
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

        asynchLoadPaidImages(expense, holder.image);

        BitmapLoader.asyncSetBitmapInImageView( //TODO: will load image of category once I have the category images
                new BitmapLoader.ImageRetrieval(BitmapLoader.ImageRetrieval.RES_ID, R.drawable.ic_launcher),
                holder.category, mContextReference.get(), BitmapCache.getInstance());

        asynchLoadShouldPayImages(expense, holder.payerImages);


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


    private void asynchLoadPaidImages(Expense expense, ImageView expenseImage){
        final String users = expense.getText(TablasContract.Compound.ExpenseBalance.USERS_WHO_PAID);
        expenseImage.setTag(expense.getId());
        ExpenseUserThreadHandler payerWorker = (ExpenseUserThreadHandler) mHandlerReference.get();

        PaidUsersLoaded onPaidUserPhotosLoaded = new PaidUsersLoaded(expense.getId(), expenseImage);
        payerWorker.queueExpensePayers(onPaidUserPhotosLoaded, users);
    }

    private void asynchLoadShouldPayImages(Expense expense, ImageViewRow payerImages) {
        //Let the loading of the images for the payers be done on a separate thread only if they have not yet been loaded.
        final String users = expense.getText(TablasContract.Compound.ExpenseBalance.USERS_WHO_SHOULD_PAY);
        payerImages.setTag(expense.getId());

        //Load all the bitmaps representing all the payers per expense. We then update the imageRows once the bitmaps are loaded.
        ExpenseUserThreadHandler payerWorker = (ExpenseUserThreadHandler) mHandlerReference.get();
        ShouldPayUsersLoaded onShouldPayInfoLoaded = new ShouldPayUsersLoaded(expense.getId(), payerImages);
        payerWorker.queueExpensePayers(onShouldPayInfoLoaded, users); //pass not the expense ID, but the list of strings
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

    private class PaidUsersLoaded extends ExpenseUserThreadHandler.OnBitmapsLoaded{

        protected PaidUsersLoaded(long expenseId, ImageView image){
            super(expenseId, image);
        }

        @Override
        public void onBitmapsLoaded(ArrayList<Bitmap> bitmaps){
            //Check whether the imageView reference has not been used for a different expense (e.g. when recyclying elements in
            // RecycleView) than the one for which the images were loaded.
            /*if(mImageView.getTag() == null || !mImageView.getTag().equals(mRequestId)){
                return;
            }*/
            Bitmap collagedImage = RoundImageView.createCollagedBitmap(bitmaps);
            getImageView().setImageBitmap(collagedImage);
        }
    }

    private class ShouldPayUsersLoaded extends ExpenseUserThreadHandler.OnContactInfoLoaded{

        protected ShouldPayUsersLoaded(long expenseId, ImageViewRow imageRow){
            super(expenseId, imageRow);
        }

        @Override
        public void onImagesLoaded(Cursor cursor) {
            ImageViewRow payerImages = getImageViewRow();
            int iterator = 0;
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    //get Imageview at this location to set its bitmap
                    ImageView imgView = (ImageView) payerImages.getChildAt(iterator);
                    if (imgView == null) {
                        imgView = new RoundImageView(payerImages.getContext());
                        payerImages.addImageView(imgView);
                    }
                    //Get the URI for the photo, retrieve it, and load bitmap asynchronously.
                    String photoUriStr = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    BitmapLoader.ImageRetrieval retrieval = new BitmapLoader.ImageRetrieval(
                            BitmapLoader.ImageRetrieval.CONTENT_URI,
                            photoUriStr,
                            ContactsContract.CommonDataKinds.Photo.PHOTO);
                    BitmapLoader.asyncSetBitmapInImageView(retrieval, imgView, mContextReference.get(), BitmapCache.getInstance());
                    iterator++;
                }
                cursor.close();
            }
            //Here, we unset bitmaps for ImageViews that will not be used for current ImageRow
            while(iterator < payerImages.getChildCount()){
                ImageView imgView = (ImageView)payerImages.getChildAt(iterator);
                if (imgView != null) {
                    imgView.setImageBitmap(null);
                }
                iterator++;
            }
        }

    }
}
