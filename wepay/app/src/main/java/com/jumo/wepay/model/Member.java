package com.jumo.wepay.model;

/**
 * Created by Moha on 6/26/15.
 */
public class Member {

    private long id;
    private String userId;          //"Foreign key"
    private boolean isAdmin;
    private boolean leftGroup;

    public String toString(){
        StringBuilder toString = new StringBuilder("Member: {");
        toString.append(id).append(", ")
                .append(userId).append(", ")
                .append(isAdmin).append(", ")
                .append(leftGroup).append("}");
        return toString.toString();
    }

    //Ancestor
    private long groupId;            //"Foreign key"

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean hasLeftGroup() {
        return leftGroup;
    }

    public void setLeftGroup(boolean leftGroup) {
        this.leftGroup = leftGroup;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
