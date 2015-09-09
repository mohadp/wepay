package com.jumo.wepay.provider.dao;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.jumo.wepay.model.ActualEntity;
import com.jumo.wepay.model.Entity;
import com.jumo.wepay.provider.Table;
import com.jumo.wepay.provider.Column;

import java.util.Date;

/**
* Created by Moha on 7/15/15.
*/
public class EntityCursor extends CursorWrapper {

    private boolean considerPrefix;

    public EntityCursor(Cursor c){
        super(c);
        considerPrefix = false;
    }

    /**
     * The second parameter, a boolean, considers wether to look for column values based on only
     * column names, or considering their table prefix (in the form of table.column"). The latter
     * is useful when querying multiple tables, joined together with columns that have the same name.
     * In this case, the only way to differentiate them is through the table.
     * @param c
     * @param useFullNames true to consider column prefixes (the table name), or not (false).
     */
    public EntityCursor(Cursor c, boolean useFullNames){
        super(c);
        considerPrefix = useFullNames;
    }


    public Entity getEntity(Table table){
        if(isBeforeFirst() || isAfterLast()){
            return null;
        }
        Entity entity = new ActualEntity(table);

        int colIndex = -1;
        for(Column column : table.getColumns()){
            colIndex = getColumnIndex(considerPrefix ? column.getFullName() : column.name);
            if(colIndex >= 0){
                entity.setField(column.name, getColumnValue(column, colIndex));
            }
        }
        return entity;
    }

    private Object getColumnValue(Column col, int colIndex){
        switch(col.datatype){
            case Column.TYPE_LONG:
                return new Long(getLong(colIndex));
            case Column.TYPE_STRING:
                return getString(colIndex);
            case Column.TYPE_DATE:
                return new Date(getLong(colIndex));
            case Column.TYPE_BOOL:
                return new Boolean(getInt(colIndex) != 0);
            case Column.TYPE_INT:
                return new Integer(getInt(colIndex));
            case Column.TYPE_DOUBLE:
                return new Double(getDouble(colIndex));
            case Column.TYPE_BYTES:
                return getBlob(colIndex);
        }

        return null;
    }

    public String toString(Table table){
        int pos = this.getPosition();
        StringBuilder sb =  new StringBuilder("EntityCursor:");

        moveToFirst();
        while(this.moveToNext()){
            sb.append(getEntity(table).toString()).append("\n");
        }

        this.moveToPosition(pos);
        return sb.toString();
    }

    public boolean considerPrefix() {
        return considerPrefix;
    }

    public void setConsiderPrefix(boolean considerPrefix) {
        this.considerPrefix = considerPrefix;
    }
}
