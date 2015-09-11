package com.jumo.wepay.provider;

import java.util.ArrayList;

/**
 * Created by Moha on 9/7/15.
 */
class JoinTreeNode {
    private ArrayList<ColumnJoin> columnJoins;
    private String mTableName;
    private JoinTreeNode mLeft;
    private JoinTreeNode mRight;
    private JoinTreeNode mParent;

    protected JoinTreeNode(String tableName) {
        this.mTableName = tableName;
        mParent = null;
        mLeft = null;
        mRight = null;
    }

    protected JoinTreeNode() {
    }

    protected JoinTreeNode(JoinTreeNode l, JoinTreeNode r) {
        setLeft(l);
        setRight(r);
    }

    protected JoinTreeNode getRoot() {
        if (mParent == null) {
            return this;
        } else {
            return mParent.getRoot();
        }
    }

    protected boolean isCrossJoin() {
        return columnJoins == null;
    }

    protected void addJoinColumns(ColumnJoin joinCols) {
        columnJoins.add(joinCols);
    }

    protected void addJoinColumns(ArrayList<ColumnJoin> joinCols) {
        columnJoins.addAll(joinCols);
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
            if (mRight != null) {
                isInJoinTree = isInJoinTree || mRight.isInTableJoinTree(tableName);
            }
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
     * If a Node exists in the current JoinTreeNode, position it on the left position. If the node was on the right, then the left position and the right positions are switched.
     * @param node
     */
    public void moveLeft(JoinTreeNode node){
        if(mLeft == node) return;
        if(mRight == node){
            JoinTreeNode prevLeft = mLeft;
            mLeft = mRight;
            mRight = prevLeft;
        }
    }

    public JoinTreeNode getLeft() {
        return mLeft;
    }

    public void setLeft(JoinTreeNode left) {
        this.mLeft = left;
        if(mLeft != null) mLeft.setParent(this);
    }

    public JoinTreeNode getRight() {
        return mRight;
    }

    public void setRight(JoinTreeNode right) {
        this.mRight = right;
        if(mRight != null) mRight.setParent(this);
    }

    public ArrayList<ColumnJoin> getColumnJoins() {
        return columnJoins;
    }

    public void setColumnJoins(ArrayList<ColumnJoin> columnJoins) {
        this.columnJoins = columnJoins;
    }

    public JoinTreeNode getParent() {
        return mParent;
    }

    private void setParent(JoinTreeNode parent) {
        this.mParent = parent;
    }

    public String getTableName() {
        return mTableName;
    }

    public void setTableName(String tableName) {
        this.mTableName = tableName;
    }
}
