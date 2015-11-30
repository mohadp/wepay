package com.jumo.tablas.model;

import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.Table;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by Moha on 6/26/15.
 */
public class Member implements Entity {

    private Entity entity;

    public Member(Entity m){
        entity = m;
    }

    public Member(){
        entity = new ActualEntity(TablasContract.Member.getInstance());
    }

    public long getId() {
        return getLong(TablasContract.Member._ID);
    }

    public void setId(long id) {
        setField(TablasContract.Member._ID, id);
    }

    public String getUserId() {
        return getText(TablasContract.Member.MEMBER_USER_ID);
    }

    public void setUserId(String userId) {
        setField(TablasContract.Member.MEMBER_USER_ID, userId);
    }

    public boolean isAdmin() {
        return getBoolean(TablasContract.Member.MEMBER_IS_ADMIN);
    }

    public void setAdmin(boolean isAdmin) {
        setField(TablasContract.Member.MEMBER_IS_ADMIN, isAdmin);
    }

    public boolean hasLeftGroup() {
        return getBoolean(TablasContract.Member.MEMBER_LEFT_GROUP);
    }

    public void setLeftGroup(boolean leftGroup) {
        setField(TablasContract.Member.MEMBER_LEFT_GROUP, leftGroup);
    }

    public long getGroupId() {
        return getLong(TablasContract.Member.MEMBER_GROUP_ID);
    }

    public void setGroupId(long groupId) {
        setField(TablasContract.Member.MEMBER_GROUP_ID, groupId);
    }

    public boolean isCurrentUser() {
        return getBoolean(TablasContract.Member.MEMBER_IS_CURR_USR);
    }

    public void setCurrentUser(boolean currUser) {
        setField(TablasContract.Member.MEMBER_IS_CURR_USR, currUser);
    }


    @Override
    public Table table(){
        return entity.table();
    }

    @Override
    public Object get(String column){
        return entity.get(column);
    }
    @Override
    public int getInt(String column) {
        return entity.getInt(column);
    }

    @Override
    public long getLong(String column) {
        return entity.getLong(column);
    }

    @Override
    public double getDouble(String column) {
        return entity.getDouble(column);
    }

    @Override
    public boolean getBoolean(String column) {
        return entity.getBoolean(column);
    }

    @Override
    public String getText(String column) {
        return entity.getText(column);
    }

    @Override
    public Date getDate(String column) {
        return entity.getDate(column);
    }

    @Override
    public byte[] getBytes(String column) {
        return entity.getBytes(column);
    }

    @Override
    public void setField(String column, Object val) {
        entity.setField(column, val);
    }

    @Override
    public Iterator<String> getFieldNameIterator(){
        return entity.getFieldNameIterator();
    }

    @Override
    public String toString(){
        return entity.toString();
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity m){
        entity = m;
    }
}
