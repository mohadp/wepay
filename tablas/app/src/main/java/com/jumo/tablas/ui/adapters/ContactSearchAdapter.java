package com.jumo.tablas.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jumo.tablas.R;
import com.jumo.tablas.ui.util.BitmapLoader;
import com.jumo.tablas.ui.util.CacheManager;

import java.util.HashMap;

/**
 * Created by Moha on 10/28/15.
 */
public class ContactSearchAdapter extends DrawableCursorAdapter {

    public ContactSearchAdapter(Context context, Cursor cursor) {
        super(context, cursor);
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
        if(cursor == null) {
            return;
        }

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
            BitmapLoader.ImageRetrieval imgRetrieval = new BitmapLoader.ImageRetrieval(BitmapLoader.ImageRetrieval.CONTENT_URI, photoUriStr, ContactsContract.CommonDataKinds.Photo.PHOTO);
            loadBitmap(imgRetrieval, holder.contactImage);
        }
    }

    public HashMap<String, String> get(int position){
        Cursor cursor = getCursor();

        if(cursor == null || cursor.isClosed())
            return null;

        int oldPosition = cursor.getPosition();

        cursor.moveToPosition(position);
        HashMap<String, String> contactInfo = new HashMap<String, String>();


        contactInfo.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        contactInfo.put(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)));
        contactInfo.put(ContactsContract.CommonDataKinds.Phone.LABEL, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL)));
        contactInfo.put(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)));

        cursor.moveToPosition(oldPosition);

        return contactInfo;
    }


    private class ViewHolder {
        protected ImageView contactImage;
        protected TextView contactName;
        protected TextView contactTel;
        protected TextView contactTelLabel;
    }
}
