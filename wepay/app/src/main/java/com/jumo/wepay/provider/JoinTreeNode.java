package com.jumo.wepay.provider;

import java.util.ArrayList;

/**
 * Created by Moha on 9/7/15.
 */
class JoinTreeNode {
    private ArrayList<ColumnJoin> columnJoins;
    private String table;
    private JoinTreeNode left;
    private JoinTreeNode right;
    private JoinTreeNode parent;

    protected JoinTreeNode(String tableName) {
        table = tableName;
    }

    protected JoinTreeNode() {
    }

    protected JoinTreeNode(JoinTreeNode l, JoinTreeNode r) {
        setLeft(l);
        setRight(r);
    }

    protected JoinTreeNode getRoot() {
        if (parent == null) {
            return this;
        } else {
            return parent.getRoot();
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
        if (table != null) {
            if (table.equals(tableName)) {
                isInJoinTree = true;
            }
        } else {
            if (left != null) {
                isInJoinTree = left.isInTableJoinTree(tableName);
            }
            if (right != null) {
                isInJoinTree = isInJoinTree || right.isInTableJoinTree(tableName);
            }
        }
        return isInJoinTree;
    }

    protected int numberOfTablesInJoinTree() {
        int noTables = 0;
        if (table != null) {
            noTables = 1;
        } else {
            if (left != null) {
                noTables += left.numberOfTablesInJoinTree();
            }
            if (right != null) {
                noTables += right.numberOfTablesInJoinTree();
            }
        }
        return noTables;
    }

    public JoinTreeNode getLeft() {
        return left;
    }

    public void setLeft(JoinTreeNode left) {
        this.left = left;
        left.setParent(this);
    }

    public JoinTreeNode getRight() {
        return right;
    }

    public void setRight(JoinTreeNode right) {
        this.right = right;
        right.setParent(this);
    }

    public ArrayList<ColumnJoin> getColumnJoins() {
        return columnJoins;
    }

    public void setColumnJoins(ArrayList<ColumnJoin> columnJoins) {
        this.columnJoins = columnJoins;
    }

    public JoinTreeNode getParent() {
        return parent;
    }

    public void setParent(JoinTreeNode parent) {
        this.parent = parent;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
