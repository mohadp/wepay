package com.jumo.wepay.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Moha on 9/7/15.
 */
abstract class CompositeTable extends Table {

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

        Table table = WepayContract.getTable(colfullName[0]);
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
        for (Table e : tablesToJoin) {
            // if a table has foreign keys, add the foreign tables to join tree as long as they are
            // part of this composite table.
            if (e.getForeignKeys() != null) {
                Set<String> foreignTables = e.getForeignKeys().keySet();
                for (String foreignTable : foreignTables) {
                    // One table may have foreign keys to tables that are not included in this
                    // composite table' table; only add the ones in this composite table
                    if (tablesToJoin.contains(WepayContract.getTable(foreignTable))) {
                        JoinTreeNode leftTableNode = tableToJoinTreeMap.get(e.getTableName());
                        JoinTreeNode rightTableNode = tableToJoinTreeMap.get(foreignTable);

                        // if the join trees for these tables are null, then create a new join trees;
                        // and add them to the map to refer back to them.
                        if (leftTableNode == null) {
                            leftTableNode = new JoinTreeNode(e.getTableName());
                            tableToJoinTreeMap.put(e.getTableName(), leftTableNode);
                        }
                        if (rightTableNode == null) {
                            rightTableNode = new JoinTreeNode(foreignTable);
                            tableToJoinTreeMap.put(foreignTable, rightTableNode);
                        }

                        //The right node should be a table only; the left can be a branch. If right node has already a parent, need to change parents.
                        JoinTreeNode parentOfRightNode = rightTableNode.getParent(); //get a reference to the right' parent.
                        leftTableNode = leftTableNode.getRoot();

                        JoinTreeNode newJoin = new JoinTreeNode(leftTableNode, rightTableNode); //this already sets the parents for the children
                        newJoin.setColumnJoins(e.getForeignKeys().get(foreignTable));

                        if (parentOfRightNode != null) {
                            if (parentOfRightNode.getRight() == rightTableNode) {
                                // the "rightTableNode" will be the child of the new join, and the new join should be the new
                                // left child of the "parentOfRightNode"; the new join should appear on the left to be able to
                                // create a join expression such as "table join table2 on (...) join table3 on (...)", in which the
                                // left part of a join is always a join tree while the right is always just a table.
                                parentOfRightNode.setRight(parentOfRightNode.getLeft());

                            }
                            parentOfRightNode.setLeft(newJoin);
                        }
                    }
                }
            } else {
                //If a table has no foreign key, it will be added eventually with the tables that have references to it. At the end; if these tables end up not joined through referencing tables, they are cross joined at the end
                tableToJoinTreeMap.put(e.getTableName(), new JoinTreeNode(e.getTableName()));
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
