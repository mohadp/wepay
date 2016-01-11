package com.jumo.tablas.model;

import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.Table;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by Moha on 6/20/15.
 */
public class Group extends BaseEntity {
    //private Entity entity;

    public Group(Entity m){
        super(m);
    }

    public Group(){
        super();
    }

    public long getId() {
        return getLong(TablasContract.Group._ID);
    }

    public void setId(long id) {
        setField(TablasContract.Group._ID, id);
    }

    public String getName() {
        return getText(TablasContract.Group.GROUP_NAME);
    }

    public void setName(String name) {
        setField(TablasContract.Group.GROUP_NAME, name);
    }

    public Date getCreatedOn() {
        return getDate(TablasContract.Group.GROUP_CREATED_ON);
    }

    public void setCreatedOn(Date createdOn) {
        setField(TablasContract.Group.GROUP_CREATED_ON, createdOn);
    }

    public byte[] getGroupPicture() {
        return getBytes(TablasContract.Group.GROUP_PICTURE);
    }

    public void setGroupPicture(byte[] groupPicture) {
        setField(TablasContract.Group.GROUP_PICTURE, groupPicture);
    }
}
