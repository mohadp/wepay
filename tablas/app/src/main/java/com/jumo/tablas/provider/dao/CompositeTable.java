package com.jumo.tablas.provider.dao;

import com.jumo.tablas.provider.TablasContract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Moha on 9/7/15.
 */
public abstract class CompositeTable extends Table {

    private static final String TAG = "CompositeTable";
    private HashSet<Table> mTables;

    protected CompositeTable() {
        super();
        mTables = new HashSet<Table>();
        defineTables();
    }

    /**
     * Determine the mTables that conform the CompositeTable by adding references to the mTables in the "mTables" attribute.
     * Having all mTables in one collection will allow the querying layer to be able to join the mTables correctly
     */
    protected abstract void defineTables();

    protected void defineColumnsAndForeignKeys() {
        mColumns = null;
        mForeignKeys = null;
        mTableName = null;
    }

    /**
     * Key in the hash map is the column name. The value contains a Column object with all its information.
     */
    @Override
    public Collection<Column> getColumns() {
        ArrayList<Column> cols = new ArrayList<Column>();
        for (Table e : mTables) {
            cols.addAll(e.getColumns());
        }
        return cols;
    }

    @Override
    public Collection<String> getColumnNames() {
        ArrayList<String> cols = new ArrayList<String>();
        for (Table e : mTables) {
            for (Column c : e.getColumns()) {
                cols.add(c.getFullName());
            }
        }
        return cols;
    }

    @Override
    public Column getColumn(String fullColName) {
        String[] colfullName = fullColName.split("\\.");

        if (colfullName.length != 2)
            return null;

        Table table = TablasContract.getTable(colfullName[0]);
        return table.getColumn(colfullName[1]);
    }

    @Override
    public String getFullColumnName(String fullColName) {
        return fullColName;
    }

    public HashSet<Table> tables() {
        return mTables;
    }

    public void addTable(Table e) {
        mTables.add(e);
    }

    /**
     * Creates a join tree with the set of tables that compose the composite table. If
     * additional base entitites are passed on, then these are also considered in the join tree.
     *
     * @param additionalTables this may be null or an arraylist of tables; null will add no additional tables to the ones that conform the composite table.
     * @return the root of the join tree.
     */
    public JoinTreeNode getTableJoinTree(HashSet<Table> additionalTables) {
        HashMap<String, JoinTreeNode> tableToJoinTreeMap = new HashMap<String, JoinTreeNode>();

        //Get the set of tables to join
        HashSet<Table> tablesToJoin = new HashSet<Table>();
        tablesToJoin.addAll(mTables);
        if (additionalTables != null) {
            tablesToJoin.addAll(additionalTables);
        }

        //Add every table, and every table's foreign tables to join tree.
        for (Table table : tablesToJoin) {
            // if a table has foreign keys, add the foreign tables to join tree as long as they are
            // part of this composite table.
            if (table.getForeignKeys() != null) {
                Set<String> foreignTables = table.getForeignKeys().keySet();
                JoinTreeNode leftJoinNode = tableToJoinTreeMap.get(table.getTableName());
                // if the join trees for these tables are null, then create a new join trees;
                // and add them to the map to refer back to them.
                if (leftJoinNode == null) {
                    leftJoinNode = new JoinTreeNode(table.getTableName());
                    tableToJoinTreeMap.put(table.getTableName(), leftJoinNode);
                }
                for (String foreignTableName : foreignTables) {
                    if (tablesToJoin.contains(TablasContract.getTable(foreignTableName))) {
                        JoinTreeNode rightJoinNode = tableToJoinTreeMap.get(foreignTableName);
                        if (rightJoinNode == null) {
                            rightJoinNode = new JoinTreeNode(foreignTableName);
                            tableToJoinTreeMap.put(foreignTableName, rightJoinNode);
                        }
                        JoinTreeNode leftParent = leftJoinNode.getParent();
                        JoinTreeNode rightParent = rightJoinNode.getParent();
                        JoinTreeNode leftRoot = leftJoinNode.getRoot();
                        JoinTreeNode rightRoot = rightJoinNode.getRoot();
                        boolean inSameTreeAlready = leftRoot == rightRoot;

                        //Construct a tree of joins such that the left nodes in joins can be tables or other joins, and the right only tables.
                        if(leftParent == null && rightParent == null){
                            JoinTreeNode newJoin = new JoinTreeNode(leftJoinNode, rightJoinNode); //this already sets the parents for the children
                            newJoin.setColumnJoins(table.getForeignKeys().get(foreignTableName));
                        }else if(leftParent != null && rightParent == null){
                            JoinTreeNode newJoin = new JoinTreeNode(leftRoot, rightJoinNode); //this already sets the parents for the children
                            newJoin.setColumnJoins(table.getForeignKeys().get(foreignTableName));
                        }else if(leftParent == null && rightParent != null){
                            JoinTreeNode newJoin = new JoinTreeNode(rightRoot, leftJoinNode); //this already sets the parents for the children
                            newJoin.setColumnJoins(table.getForeignKeys().get(foreignTableName));
                        }else if(leftParent != null && rightParent != null && !inSameTreeAlready){ //move right to left's tree with a new join, and make that join the left child of rightParent.
                            rightParent.moveLeft(rightJoinNode); //make sure the table to move to left's tree is on the left (we'l clear left in parent).
                            rightParent.setLeft(null);
                            JoinTreeNode newJoin = new JoinTreeNode(leftRoot, rightJoinNode); //this already sets the parents for the children
                            newJoin.setColumnJoins(table.getForeignKeys().get(foreignTableName));
                            rightParent.setLeft(newJoin);
                        } else if(inSameTreeAlready){
                            //Log.d(TAG, "##### In same JoinTree #####");
                            JoinTreeNode commonParent = JoinTreeNode.findFirstCommonParent(leftJoinNode, rightJoinNode);
                            if(commonParent != null){
                                commonParent.addColumnJoins(table.getForeignKeys().get(foreignTableName));
                            }
                        }
                    }
                }
            } else {
                //If a table has no foreign key, it will be added eventually with the tables that have references to it. At the end; if these tables end up not joined through referencing tables, they are cross joined at the end
                tableToJoinTreeMap.put(table.getTableName(), new JoinTreeNode(table.getTableName()));
            }
        }

        //Here, we need to identify the distinct disjoint jointrees, or all the distinct roots.
        // Then, we join them through crossjoin, and return the root.
        HashSet<JoinTreeNode> distinctJoinTrees = new HashSet<>();
        for (JoinTreeNode jt : tableToJoinTreeMap.values()) {
            distinctJoinTrees.add(jt.getRoot());
        }

        Iterator<JoinTreeNode> it = distinctJoinTrees.iterator();
        JoinTreeNode root = (it.hasNext()) ? it.next() : null;
        while (it.hasNext()) {
            JoinTreeNode xJoinTreeNode = it.next();
            root = new JoinTreeNode(root, xJoinTreeNode);
        }

        return root;
    }
}
