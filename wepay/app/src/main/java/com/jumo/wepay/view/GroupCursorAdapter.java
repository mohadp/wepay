package com.jumo.wepay.view;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.jumo.wepay.R;
import com.jumo.wepay.controller.Cursors;
import com.jumo.wepay.model.Group;

/**
 * Created by Moha on 7/9/15.
 */
public class GroupCursorAdapter extends CursorAdapter {

    private Cursors.GroupCursor mGroupCursor;

    public GroupCursorAdapter(Context context, Cursors.GroupCursor cursor) {
        super(context, cursor, 0);
        mGroupCursor = cursor;
    }

    //Create view for each item
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // use a layout inflater to get a row view
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.list_item_group, parent, false);
    }

    //fill the view with data from the cursor.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // get the run for the current row
        Group group = mGroupCursor.getGroup();

        // set up the start date text view
        TextView startDateTextView = (TextView)view;
        //startDateTextView.setText("Run at " + group.getStartDate());
    }

}

