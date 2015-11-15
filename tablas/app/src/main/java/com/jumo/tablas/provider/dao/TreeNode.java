package com.jumo.tablas.provider.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Created by Moha on 9/7/15.
 */
public class TreeNode {
    public final static int INNER_JOIN = 0;
    public final static int LEFT_OUTER_JOIN = 1;
    public final static int RIGHT_OUTER_JOIN = 2;
    public final static int FULL_OUTER_JOIN = 3;
    public final static int CROSS_JOIN = 4;

    private LinkedHashSet<ColumnJoin> mColumnJoins;
    private String mTableName;
    private int mJoinType;
    private TreeNode mLeft;
    private TreeNode mRight;
    private TreeNode mParent;


    protected TreeNode(String tableName) {
        this.mTableName = tableName;
        mParent = null;
        mLeft = null;
        mRight = null;
    }

    protected TreeNode() {
    }

    protected TreeNode(TreeNode l, TreeNode r) {
        setLeft(l);
        setRight(r);
        mJoinType = INNER_JOIN;
    }

    protected TreeNode getRoot() {
        if (mParent == null) {
            return this;
        } else {
            return mParent.getRoot();
        }
    }

    protected boolean isCrossJoin() {
        return mColumnJoins == null;
    }


    protected boolean isInTableJoinTree(String tableName) {
        boolean isInJoinTree = false;
        if (this.mTableName != null) {
            if (this.mTableName.equals(tableName)) {
                isInJoinTree = true;
            }
        } else {
            if (mLeft != null) {
                isInJoinTree = mLeft.isInTableJoinTree(tableName);
            }
            if (!isInJoinTree && mRight != null) { // if we found table on the left, then we've found the node.
                isInJoinTree = mRight.isInTableJoinTree(tableName);
            }
        }
        return isInJoinTree;
    }


    public TreeNode findTableNode(String tableName) {
        TreeNode foundNode = null;
        if (mTableName != null && mTableName.equals(tableName)) {
                foundNode = this;
        } else {
            if (foundNode == null && mLeft != null) {
                foundNode = mLeft.findTableNode(tableName);
            }
            if (foundNode == null && mRight != null) { // if we found table on the left, then we've found the node.
                foundNode = mRight.findTableNode(tableName);
            }
        }
        return foundNode;
    }


    protected boolean isDescendant(TreeNode node){
        if (this == node) {
            return true;
        }
        if(mLeft == null && mRight == null) {
            return false;
        }

        boolean isInJoinTree = false;
        if (mLeft != null) {
            isInJoinTree = mLeft.isDescendant(node);
        }
        if (!isInJoinTree && mRight != null) { // if we found table on the left, then we've found the node.
            isInJoinTree = mRight.isDescendant(node);
        }
        return isInJoinTree;

    }

    protected int numberOfTablesInJoinTree() {
        int noTables = 0;
        if (mTableName != null) {
            noTables = 1;
        } else {
            if (mLeft != null) {
                noTables += mLeft.numberOfTablesInJoinTree();
            }
            if (mRight != null) {
                noTables += mRight.numberOfTablesInJoinTree();
            }
        }
        return noTables;
    }

    /**
     * If a Node exists in the current TreeNode, position it on the left position. If the node was on the right, then the left position and the right positions are switched.
     * @param node
     */
    public void switchLeft(TreeNode node){
        if(mLeft == node) return;
        if(mRight == node){
            TreeNode prevLeft = mLeft;
            mLeft = mRight;
            mRight = prevLeft;
        }
    }

    public TreeNode getLeft() {
        return mLeft;
    }

    public void setLeft(TreeNode left) {
        this.mLeft = left;
        if(mLeft != null) mLeft.setParent(this);
    }

    public TreeNode getRight() {
        return mRight;
    }

    public void setRight(TreeNode right) {
        this.mRight = right;
        if(mRight != null) mRight.setParent(this);
    }

    public LinkedHashSet<ColumnJoin> getColumnJoins() {
        return mColumnJoins;
    }

    public void setColumnJoins(LinkedHashSet<ColumnJoin> columnJoins) {
        this.mColumnJoins = columnJoins;
    }

    public void addColumnJoins(LinkedHashSet<ColumnJoin> columnJoins){
        if(mColumnJoins == null){
            mColumnJoins = new LinkedHashSet<ColumnJoin>();
        }
        mColumnJoins.addAll(columnJoins);
    }

    protected void addColumnJoin(ColumnJoin joinCols) {
        if(mColumnJoins == null){
            mColumnJoins = new LinkedHashSet<ColumnJoin>();
        }
        mColumnJoins.add(joinCols);
    }

    public TreeNode getParent() {
        return mParent;
    }

    private void setParent(TreeNode parent) {
        this.mParent = parent;
    }

    public String getTableName() {
        return mTableName;
    }

    public void setTableName(String tableName) {
        this.mTableName = tableName;
    }

    public static TreeNode findFirstCommonParent(TreeNode node1, TreeNode node2){
        if(node1 == node2) {
            return node1;
        }

        boolean isNode2AChild = false;
        //search every ancestor of node1 that has node2 as chid
        node1 = node1.getParent();
        while(!isNode2AChild && node1 != null) {
            if(node1.isDescendant(node2)){
                isNode2AChild = true;
            }else{
                node1 = node1.getParent();
            }
        }
        return node1;
    }

    public HashSet<String> getJoinToTables() {
        if(mColumnJoins == null){
            return null;
        }

        HashSet<String> joinToTables = new HashSet<String>();
        for(ColumnJoin j : mColumnJoins) {
            joinToTables.add(j.left.table);
            joinToTables.add(j.right.table);
        }
        return joinToTables;
    }

    public int getJoinType() {
        return mJoinType;
    }

    public void setJoinType(int mJoinType) {
        this.mJoinType = mJoinType;
    }
}
