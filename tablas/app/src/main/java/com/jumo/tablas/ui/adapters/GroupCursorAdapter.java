package com.jumo.tablas.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jumo.tablas.R;
import com.jumo.tablas.model.Group;
import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.EntityCursor;
import com.jumo.tablas.ui.views.RoundImageView;

/**
 * Created by Moha on 7/9/15.
 */
public class GroupCursorAdapter extends DrawableCursorAdapter {
    private static final String TAG = "GroupCursorAdapter";

    public GroupCursorAdapter(Context context, EntityCursor cursor) {
        super(context, cursor);
        //mCacheReference = new WeakReference<LruCache<Integer, Bitmap>>();
    }

    public GroupCursorAdapter(Context context, EntityCursor cursor, LruCache<Object, Bitmap> cache) {
        super(context, cursor, cache);
    }

    //Create view for each item
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // use a layout inflater to get a row view
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(R.layout.list_item_group, parent, false);

		return view;
    }

    //fill the view with data from the cursor.
    @Override
    public void bindView(View view, Context context, android.database.Cursor cursor) {
        EntityCursor groupCursor = (EntityCursor)getCursor();


        if(groupCursor == null) return;

        // get the run for the current row
        Group group = new Group(groupCursor.getEntity(TablasContract.Group.getInstance()));
        //Log.d(TAG, group.toString());

        // set up the start date text view
        RoundImageView roundImage = ((RoundImageView) view.findViewById(R.id.list_group_image));
        asyncSetBitmapInImageView(new ImageRetrieval(ImageRetrieval.RES_ID, R.drawable.moha), roundImage); //loaded in separate thread if not present in cache
        //roundImage.setImageBitmap(getBitmapFromCacheFirst(context, R.drawable.moha));

        ((TextView)view.findViewById(R.id.list_group_name)).setText(group.getName());
        ((TextView)view.findViewById(R.id.list_group_balance)).setText(String.format("%1$.2f", group.getUserBalance()));

    }

    public Group getItem(int i){
        EntityCursor groupCursor = (EntityCursor)getCursor();
        if(groupCursor == null) return null;

        groupCursor.moveToPosition(i);
        return new Group(groupCursor.getEntity(TablasContract.Group.getInstance()));
    }

}

