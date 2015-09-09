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
    private HashSet<Metric> mMetrics;

    protected CompositeTable() {
        super();
        mTables = new HashSet<Table>();
        mMetrics = new HashSet<Metric>();
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

        Table table = WepayContract.getEntity(colfullName[0]);
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

    public void addMetric(Metric m) {
        mMetrics.add(m);
    }


    /**
     * Creates a join tree with the set of tables that compose the composite entity. If
     * additional base entitites are passed on, then these are also considered in the join tree.
     *
     * @return the root of the join tree.
     */
    public JoinTree getTableJoinTree() {
        HashMap<String, JoinTree> tableToJoinTreeMap = new HashMap<String, JoinTree>();

        //Get the set of tables to join
        HashSet<Table> tablesToJoin = new HashSet<Table>();
        tablesToJoin.addAll(mTables);
        if (mMetrics != null) {
            for(Metric m : mMetrics){
                tablesToJoin.addAll(m.getDependentEntities());
            }

        }

        //Add every table, and every table's foreign tables to join tree.
        for (Table e : tablesToJoin) {
            // if a table has foreign keys, add the foreign tables to join tree as long as they are
            // part of this composite entity.
            if (e.getForeignKeys() != null) {
                Set<String> foreignTables = e.getForeignKeys().keySet();
                for (String foreignTable : foreignTables) {
                    // One table may have foreign keys to tables that are not included in this
                    // composite entity' table; only add the ones in this composite entity
                    if (tablesToJoin.contains(WepayContract.getEntity(foreignTable))) {
                        JoinTree leftTableNode = tableToJoinTreeMap.get(e.tableName());
                        JoinTree rightTableNode = tableToJoinTreeMap.get(foreignTable);

                        // if the join trees for these tables are null, then create a new join trees;
                        // and add them to the map to refer back to them.
                        if (leftTableNode == null) {
                            leftTableNode = new JoinTree(e.tableName());
                            tableToJoinTreeMap.put(e.tableName(), leftTableNode);
                        }
                        if (rightTableNode == null) {
                            rightTableNode = new JoinTree(foreignTable);
                            tableToJoinTreeMap.put(foreignTable, rightTableNode);
                        }

                        //The right node should be a table only; the left can be a branch. If right node has already a parent, need to change parents.
                        JoinTree parentOfRightNode = rightTableNode.getParent(); //get a reference to the right' parent.
                        leftTableNode = leftTableNode.getRoot();

                        JoinTree newJoin = new JoinTree(leftTableNode, rightTableNode); //this already sets the parents for the children
                        newJoin.setJoinColumns(e.getForeignKeys().get(foreignTable));

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
                tableToJoinTreeMap.put(e.tableName(), new JoinTree(e.tableName()));
            }
        }

        //Here, we need to identify the distinct disjoint jointrees, or all the distinct roots.
        // Then, we join them through crossjoin, and return the root.
        HashSet<JoinTree> distinctJoinTrees = new HashSet<>();
        for (JoinTree jt : tableToJoinTreeMap.values()) {
            distinctJoinTrees.add(jt.getRoot());
        }

        Iterator<JoinTree> it = distinctJoinTrees.iterator();
        JoinTree root = (it.hasNext()) ? it.next() : null;
        while (it.hasNext()) {
            JoinTree xJoinTree = it.next();
            root = new JoinTree(root, xJoinTree);
        }

        return root;
    }
}
