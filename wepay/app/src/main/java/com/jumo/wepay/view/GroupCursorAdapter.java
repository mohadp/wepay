package com.jumo.wepay.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.jumo.wepay.R;
import com.jumo.wepay.provider.Cursors;

/**
 * Created by Moha on 7/9/15.
 */
public class GroupCursorAdapter extends CursorAdapter {

    private Cursors.Group mGroupCursor;

    public GroupCursorAdapter(Context context, Cursors.Group cursor) {
        super(context, cursor, 0);
        mGroupCursor = cursor;
    }

    //Create view for each item
    @Override
    public View newView(Context context, android.database.Cursor cursor, ViewGroup parent) {
        // use a layout inflater to get a row view
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.list_item_group, parent, false);
    }

    //fill the view with data from the cursor.
    @Override
    public void bindView(View view, Context context, android.database.Cursor cursor) {
        // get the run for the current row
        com.jumo.wepay.model.Group group = mGroupCursor.getGroup();

        // set up the start date text view
        TextView startDateTextView = (TextView)view;
        //startDateTextView.setText("Run at " + group.getStartDate());
    }

}

