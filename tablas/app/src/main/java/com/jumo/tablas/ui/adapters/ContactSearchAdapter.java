package com.jumo.tablas.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jumo.tablas.R;
import com.jumo.tablas.provider.dao.EntityCursor;

import java.lang.ref.WeakReference;

/**
 * Created by Moha on 10/28/15.
 */
public class ContactSearchAdapter extends DrawableCursorAdapter {

    protected WeakReference<LruCache<Object, Bitmap>> mCacheReference;
    protected WeakReference<Context> mContextReference;


    public ContactSearchAdapter(Context context, Cursor cursor, LruCache<Object, Bitmap> cache) {
        super(context, cursor, cache);
        mCacheReference = new WeakReference<LruCache<Object, Bitmap>>(cache);
        mContextReference = new WeakReference<Context>(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // use a layout inflater to get a row view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_contact_search, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();
        if(holder == null){
            holder = new ViewHolder();
            holder.contactImage = (ImageView)view.findViewById(R.id.list_contact_image);
            holder.contactName = (TextView)view.findViewById(R.id.list_contact_name);
            holder.contactTel = (TextView)view.findViewById(R.id.list_contact_tel);
            holder.contactTelLabel = (TextView)view.findViewById(R.id.list_tel_type);
            view.setTag(holder);
        }

        holder.contactName.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        holder.contactTel.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)));
        holder.contactTelLabel.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL)));

        String photoUriStr = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
        if(photoUriStr != null){
            ImageRetrieval imgRetrieval = new ImageRetrieval(ImageRetrieval.CONTENT_URI, photoUriStr, ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
            asyncSetBitmapInImageView(imgRetrieval, holder.contactImage);
        }
    }

    private class ViewHolder {
        protected ImageView contactImage;
        protected TextView contactName;
        protected TextView contactTel;
        protected TextView contactTelLabel;
    }
}
