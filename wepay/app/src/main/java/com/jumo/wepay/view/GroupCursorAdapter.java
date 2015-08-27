package com.jumo.wepay.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.jumo.wepay.R;
import com.jumo.wepay.model.Group;
import com.jumo.wepay.provider.dao.GroupCursor;
import java.text.*;

/**
 * Created by Moha on 7/9/15.
 */
public class GroupCursorAdapter extends CursorAdapter {

    //private GroupCursor mGroupCursor;

    public GroupCursorAdapter(Context context, GroupCursor cursor) {
        super(context, cursor, 0);
        //mGroupCursor = cursor;
    }

    //Create view for each item
    @Override
    public View newView(Context context, android.database.Cursor cursor, ViewGroup parent) {
        // use a layout inflater to get a row view
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(R.layout.list_item_group, parent, false);
		
		return view;
    }

    //fill the view with data from the cursor.
    @Override
    public void bindView(View view, Context context, android.database.Cursor cursor) {
        GroupCursor groupCursor = (GroupCursor)getCursor();
        if(groupCursor == null) return;

        // get the run for the current row
        Group group = groupCursor.getGroup();

        // set up the start date text view
        ((TextView)view.findViewById(R.id.list_group_name)).setText(group.getName());
        ((TextView)view.findViewById(R.id.list_group_balance)).setText(String.format("%1$.2f",group.getUserBalance()));

    }

    public Group getItem(int i){
        GroupCursor groupCursor = (GroupCursor)getCursor();
        if(groupCursor == null) return null;

        groupCursor.moveToPosition(i);
        return groupCursor.getGroup();
    }

}

