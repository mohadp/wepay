package com.jumo.tablas.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.Column;
import com.jumo.tablas.provider.dao.Table;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Moha on 9/7/15.
 */
public class ActualEntity extends Entity implements Parcelable {
    private HashMap<String, Object> attributes;
    private Table table;
    private static final String TAG = "ActualEntity";

    public static final Parcelable.Creator<ActualEntity> CREATOR = new Parcelable.Creator<ActualEntity>() {
        public ActualEntity createFromParcel(Parcel in) {
            return new ActualEntity(in);
        }

        public ActualEntity[] newArray(int size) {
            return new ActualEntity[size];
        }
    };

    public ActualEntity(Table e){
        attributes = new HashMap<String, Object>();
        table = e;
    }

    private ActualEntity(Parcel in) {
        this(TablasContract.getTable(in.readString()));
        int size = in.readInt();

        for(int i = 0; i < size; i++){
            String colName = in.readString();
            Object value = readBasedOnType(colName, in);
            setField(colName, value);
        }
    }

    @Override
    public Table table(){
        return table;
    }

    @Override
    public Object get(String column){
        return attributes.get(column);
    }

    @Override
    public int getInt(String column){
        int val = 0;
        try {
            Object field = attributes.get(column);
            val = (field == null)? 0 : ((Integer)field).intValue();
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an integer value:" + e.toString());
        }

        return val;
    }

    @Override
    public long getLong(String column){
        long val = 0;
        try {
            Object field = attributes.get(column);
            val = (field == null)? 0 :((Long)field).longValue();
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an long value:" + e.toString());
        }

        return val;
    }

    @Override
    public double getDouble(String column){
        double val = 0;
        try{
            Object field = attributes.get(column);
            val = (field == null)? 0 : ((Double)field).doubleValue();
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an double value:" + e.toString());
        }
        return val;
    }

    @Override
    public boolean getBoolean(String column){
        boolean val = false;
        try{
            Object field = attributes.get(column);
            val = (field == null)? false : ((Boolean)field).booleanValue();
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an boolean value:" + e.toString());
        }
        return val;
    }

    @Override
    public String getText(String column){
        String val = null;
        try{
            val =  (String)attributes.get(column);
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an String value:" + e.toString());
        }
        return val;
    }

    @Override
    public Date getDate(String column){
        Date val = null;
        try{
            val = (Date)attributes.get(column);
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an Date value:" + e.toString());
        }
        return val;
    }

    @Override
    public byte[] getBytes(String column){
        byte[] val = null;
        try{
            val = (byte[])attributes.get(column);
        }catch(ClassCastException e){
            Log.d(TAG, "Could not cast to a an byte[] value:" + e.toString());
        }
        return val;
    }

    @Override
    public void setField(String column, Object val){
        attributes.put(column, val);
    }

    @Override
    public Iterator<String> getFieldNameIterator(){
        return attributes.keySet().iterator();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder((table.getTableName() == null)? "<NoName>" : table.getTableName());
        sb.append(":[\n");

        for(Column c : table.getColumns()){
            sb.append("\t").append(c.name)
                    .append(":").append((get(c.name) == null)? "null": get(c.name).toString())
                    .append("\n");
        }
        sb.append("]\n");
        return sb.toString();
    }


    //////////// Parcelable Implementation ///////////
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(table.getTableName());
        out.writeInt(attributes.size());
        for(String colName : attributes.keySet()){
            Object value = get(colName);
            out.writeString(colName);
            writeBasedOnType(colName, out);
        }
    }

    private void writeBasedOnType(String colName, Parcel out){
        Column col  = table.getColumn(colName);
        Object value = get(colName);

        switch(col.datatype){
            case Column.INTERNAL_TYPE_LONG:
                out.writeLong((value != null)? (Long)value : 0);
                break;
            case Column.INTERNAL_TYPE_STRING:
                out.writeString((value != null)? (String)value : "");
                break;
            case Column.INTERNAL_TYPE_DATE:
                out.writeLong((value != null)? ((Date)value).getTime() : 0);
                break;
            case Column.INTERNAL_TYPE_BOOL:
                out.writeInt((value != null)? (((Boolean)value).booleanValue()? 1 : 0) : 0);
                break;
            case Column.INTERNAL_TYPE_INT:
                out.writeInt((value != null)? (Integer)value : 0);
                break;
            case Column.INTERNAL_TYPE_DOUBLE:
                out.writeDouble((value != null)? (Double)value : 0);
                break;
            case Column.INTERNAL_TYPE_BYTES:
                int size = (value == null)? 0 : ((byte[])value).length;
                out.writeInt(size);
                out.writeByteArray((value != null)? (byte[])value : new byte[0]);
                break;
        }
    }

    private Object readBasedOnType(String colName, Parcel in){
        Column col = table.getColumn(colName);

        switch(col.datatype){
            case Column.INTERNAL_TYPE_LONG:
                return new Long(in.readLong());
            case Column.INTERNAL_TYPE_STRING:
                return in.readString();
            case Column.INTERNAL_TYPE_DATE:
                return new Date(in.readLong());
            case Column.INTERNAL_TYPE_BOOL:
                return  new Boolean(in.readInt() != 0);
            case Column.INTERNAL_TYPE_INT:
                return in.readInt();
            case Column.INTERNAL_TYPE_DOUBLE:
                return in.readDouble();
            case Column.INTERNAL_TYPE_BYTES:
                int size = in.readInt();
                byte[] binaryData = new byte[size];
                in.readByteArray(binaryData);
                return binaryData;
        }
        return null;
    }
}
