package com.jumo.tablas.provider.dao;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.jumo.tablas.model.ActualEntity;
import com.jumo.tablas.model.Entity;

import java.util.Date;

/**
* Created by Moha on 7/15/15.
*/
public class EntityCursor extends CursorWrapper {

    private static final String TAG = "EntityCursor";

    public EntityCursor(Cursor c){
        super(c);
    }

    /**
     * Returns an entity that respresents the provided table (an entity with the columns of the table).
     * The entity returns the entity with the fields named after the table column names (not the full column names, e.g.
     * columns are named like "colname", not like "table.colname".
     *
     * @param table
     * @return an Entity object
     */
    public Entity getEntity(Table table){
        if(isBeforeFirst() || isAfterLast()){
            return null;
        }
        Entity entity = new ActualEntity(table);

        int colIndex = -1;
        for(Column column : table.getColumns()){

            colIndex = getColumnIndex((column.alias == null)? column.name : column.alias);

            if(colIndex >= 0){
                entity.setField(column.name, getColumnValue(column, colIndex));
            }
        }

        return entity;
    }

    private Object getColumnValue(Column col, int colIndex){
        switch(col.datatype){
            case Column.INTERNAL_TYPE_LONG:
                return new Long(getLong(colIndex));
            case Column.INTERNAL_TYPE_STRING:
                return getString(colIndex);
            case Column.INTERNAL_TYPE_DATE:
                return new Date(getLong(colIndex));
            case Column.INTERNAL_TYPE_BOOL:
                return new Boolean(getInt(colIndex) != 0);
            case Column.INTERNAL_TYPE_INT:
                return new Integer(getInt(colIndex));
            case Column.INTERNAL_TYPE_DOUBLE:
                return new Double(getDouble(colIndex));
            case Column.INTERNAL_TYPE_BYTES:
                return getBlob(colIndex);
        }

        return null;
    }

    //@Override



    public String toString(Table table){
        Cursor cursor = getWrappedCursor();

        int pos = cursor.getPosition();
        StringBuilder sb =  new StringBuilder("****** EntityCursor:\n");

        cursor.moveToFirst();
        sb.append(getEntity(table).toString()).append("\n");

        while(cursor.moveToNext()){
            sb.append(getEntity(table).toString()).append("\n");
        }
        cursor.moveToPosition(pos);

        return sb.toString();
    }

}
