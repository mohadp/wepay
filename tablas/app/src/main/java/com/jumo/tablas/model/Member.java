package com.jumo.tablas.model;

import com.jumo.tablas.provider.TablasContract;
import com.jumo.tablas.provider.dao.Table;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by Moha on 6/26/15.
 */
public class Member extends BaseEntity {

    //private Entity entity;

    public Member(Entity m){
        super(m);
    }

    public Member(){
        super();
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

}
