package com.jumo.tablas.provider.dao;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.jumo.tablas.model.ActualEntity;
import com.jumo.tablas.model.Entity;
import com.jumo.tablas.provider.Table;
import com.jumo.tablas.provider.Column;

import java.util.Date;

/**
* Created by Moha on 7/15/15.
*/
public class EntityCursor extends CursorWrapper {

    private static final String TAG = "EntityCursor";

    public EntityCursor(Cursor c){
        super(c);
    }

    public Entity getEntity(Table table){
        if(isBeforeFirst() || isAfterLast()){
            return null;
        }
        Entity entity = new ActualEntity(table);

        int colIndex = -1;
        for(Column column : table.getColumns()){

            colIndex = getColumnIndex(column.name);

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
        sb.append(getEntity(table).toString()).append("\n");

        while(this.moveToNext()){
            sb.append(getEntity(table).toString()).append("\n");
        }

        this.moveToPosition(pos);
        return sb.toString();
    }
}
